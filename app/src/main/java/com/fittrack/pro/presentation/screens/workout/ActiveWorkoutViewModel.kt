package com.fittrack.pro.presentation.screens.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.ExerciseRepository
import com.fittrack.pro.domain.repository.RoutineRepository
import com.fittrack.pro.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseWithSets(
    val exercise: ExerciseEntity,
    val plannedSets: Int,
    val sets: List<WorkoutSetEntity>,
    val historicalSets: List<WorkoutSetEntity> = emptyList()
)

data class ActiveWorkoutState(
    val isLoading: Boolean = true,
    val sessionId: Long = 0,
    val routineName: String = "",
    val startTime: Long = 0,
    val elapsedSeconds: Long = 0,
    val exercises: List<ExerciseWithSets> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val restTimerSeconds: Int = 0,
    val isRestTimerRunning: Boolean = false,
    val defaultRestTime: Int = 90, // seconds
    val isFinishing: Boolean = false,
    val error: String? = null
)

sealed class ActiveWorkoutEvent {
    data class SetCompleted(val setId: Long) : ActiveWorkoutEvent()
    data class RestTimerFinished(val message: String = "Przerwa zakończona!") : ActiveWorkoutEvent()
    data class Error(val message: String) : ActiveWorkoutEvent()
    data class WorkoutFinished(val sessionId: Long) : ActiveWorkoutEvent()
}

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val routineId: Long = savedStateHandle.get<Long>("routineId") ?: 0L

    private val _state = MutableStateFlow(ActiveWorkoutState())
    val state: StateFlow<ActiveWorkoutState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ActiveWorkoutEvent>()
    val events: SharedFlow<ActiveWorkoutEvent> = _events.asSharedFlow()

    private var timerJob: kotlinx.coroutines.Job? = null
    private var restTimerJob: kotlinx.coroutines.Job? = null

    init {
        startWorkout()
    }

    private fun startWorkout() {
        viewModelScope.launch {
            try {
                val routine = routineRepository.getRoutineById(routineId)
                if (routine == null) {
                    _state.update { it.copy(error = "Rutyna nie znaleziona") }
                    return@launch
                }

                // Start session
                val sessionId = workoutRepository.startSession(routineId, routine.name)
                val startTime = System.currentTimeMillis()

                // Get routine exercises
                val (_, exercises) = routineRepository.getRoutineWithExercises(routineId)
                
                // Get routine exercise details (planned sets)
                val routineExercises = mutableListOf<RoutineExerciseEntity>()
                routineRepository.getRoutineExercises(routineId).first().let { 
                    routineExercises.addAll(it) 
                }

                // Build exercise list with historical data
                val exercisesWithSets = exercises.mapIndexed { index, exercise ->
                    val routineExercise = routineExercises.find { it.exerciseId == exercise.id }
                    val plannedSets = routineExercise?.plannedSets ?: 3
                    
                    // Get historical sets for suggestions
                    val historicalSets = workoutRepository.getRecentSetsByExercise(exercise.id, 20)
                    
                    ExerciseWithSets(
                        exercise = exercise,
                        plannedSets = plannedSets,
                        sets = emptyList(),
                        historicalSets = historicalSets
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        sessionId = sessionId,
                        routineName = routine.name,
                        startTime = startTime,
                        exercises = exercisesWithSets
                    )
                }

                // Start elapsed time timer
                startElapsedTimer()
                
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun startElapsedTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val elapsed = (System.currentTimeMillis() - _state.value.startTime) / 1000
                _state.update { it.copy(elapsedSeconds = elapsed) }
            }
        }
    }

    fun addSet(exerciseIndex: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            val exerciseWithSets = currentState.exercises.getOrNull(exerciseIndex) ?: return@launch
            val exercise = exerciseWithSets.exercise
            
            // Get suggestion from history
            val lastSet = exerciseWithSets.historicalSets.firstOrNull()
            
            val newSet = WorkoutSetEntity(
                sessionId = currentState.sessionId,
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                setNumber = exerciseWithSets.sets.size + 1,
                setType = if (exerciseWithSets.sets.isEmpty()) SetType.WARMUP else SetType.WORKING,
                weight = lastSet?.weight,
                reps = lastSet?.reps,
                side = if (exercise.unilateral) Side.LEFT else null
            )

            val setId = workoutRepository.addSet(newSet)
            val insertedSet = newSet.copy(id = setId)

            // Update state
            val updatedExercises = currentState.exercises.toMutableList()
            val updatedExerciseWithSets = exerciseWithSets.copy(
                sets = exerciseWithSets.sets + insertedSet
            )
            updatedExercises[exerciseIndex] = updatedExerciseWithSets
            
            _state.update { it.copy(exercises = updatedExercises) }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, weight: Double?, reps: Int?, rpe: Int? = null) {
        viewModelScope.launch {
            val currentState = _state.value
            val exerciseWithSets = currentState.exercises.getOrNull(exerciseIndex) ?: return@launch
            val set = exerciseWithSets.sets.getOrNull(setIndex) ?: return@launch

            val updatedSet = set.copy(weight = weight, reps = reps, rpe = rpe)
            workoutRepository.updateSet(updatedSet)

            // Update state
            val updatedSets = exerciseWithSets.sets.toMutableList()
            updatedSets[setIndex] = updatedSet
            
            val updatedExercises = currentState.exercises.toMutableList()
            updatedExercises[exerciseIndex] = exerciseWithSets.copy(sets = updatedSets)
            
            _state.update { it.copy(exercises = updatedExercises) }
        }
    }

    fun completeSet(exerciseIndex: Int, setIndex: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            val exerciseWithSets = currentState.exercises.getOrNull(exerciseIndex) ?: return@launch
            val set = exerciseWithSets.sets.getOrNull(setIndex) ?: return@launch

            val completedSet = set.copy(
                completed = true,
                completedAt = System.currentTimeMillis()
            )
            workoutRepository.updateSet(completedSet)

            // Update state
            val updatedSets = exerciseWithSets.sets.toMutableList()
            updatedSets[setIndex] = completedSet
            
            val updatedExercises = currentState.exercises.toMutableList()
            updatedExercises[exerciseIndex] = exerciseWithSets.copy(sets = updatedSets)
            
            _state.update { it.copy(exercises = updatedExercises) }

            // Start rest timer
            startRestTimer()
            
            _events.emit(ActiveWorkoutEvent.SetCompleted(set.id))
        }
    }

    fun setSetType(exerciseIndex: Int, setIndex: Int, setType: SetType) {
        viewModelScope.launch {
            val currentState = _state.value
            val exerciseWithSets = currentState.exercises.getOrNull(exerciseIndex) ?: return@launch
            val set = exerciseWithSets.sets.getOrNull(setIndex) ?: return@launch

            val updatedSet = set.copy(setType = setType)
            workoutRepository.updateSet(updatedSet)

            val updatedSets = exerciseWithSets.sets.toMutableList()
            updatedSets[setIndex] = updatedSet
            
            val updatedExercises = currentState.exercises.toMutableList()
            updatedExercises[exerciseIndex] = exerciseWithSets.copy(sets = updatedSets)
            
            _state.update { it.copy(exercises = updatedExercises) }
        }
    }

    fun deleteSet(exerciseIndex: Int, setIndex: Int) {
        viewModelScope.launch {
            val currentState = _state.value
            val exerciseWithSets = currentState.exercises.getOrNull(exerciseIndex) ?: return@launch
            val set = exerciseWithSets.sets.getOrNull(setIndex) ?: return@launch

            workoutRepository.deleteSet(set)

            val updatedSets = exerciseWithSets.sets.toMutableList()
            updatedSets.removeAt(setIndex)
            
            // Renumber remaining sets
            val renumberedSets = updatedSets.mapIndexed { index, s -> 
                s.copy(setNumber = index + 1) 
            }
            
            val updatedExercises = currentState.exercises.toMutableList()
            updatedExercises[exerciseIndex] = exerciseWithSets.copy(sets = renumberedSets)
            
            _state.update { it.copy(exercises = updatedExercises) }
        }
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        _state.update { it.copy(restTimerSeconds = it.defaultRestTime, isRestTimerRunning = true) }
        
        restTimerJob = viewModelScope.launch {
            while (_state.value.restTimerSeconds > 0) {
                kotlinx.coroutines.delay(1000)
                _state.update { it.copy(restTimerSeconds = it.restTimerSeconds - 1) }
            }
            _state.update { it.copy(isRestTimerRunning = false) }
            _events.emit(ActiveWorkoutEvent.RestTimerFinished())
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _state.update { it.copy(restTimerSeconds = 0, isRestTimerRunning = false) }
    }

    fun addRestTime(seconds: Int) {
        _state.update { it.copy(restTimerSeconds = it.restTimerSeconds + seconds) }
    }

    fun finishWorkout(comment: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isFinishing = true) }
            
            try {
                workoutRepository.endSession(_state.value.sessionId, comment)
                timerJob?.cancel()
                restTimerJob?.cancel()
                
                _events.emit(ActiveWorkoutEvent.WorkoutFinished(_state.value.sessionId))
            } catch (e: Exception) {
                _state.update { it.copy(isFinishing = false, error = e.message) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        restTimerJob?.cancel()
    }
}
