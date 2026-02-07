package com.fittrack.pro.presentation.screens.routines

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.ExerciseEntity
import com.fittrack.pro.data.local.entity.RoutineEntity
import com.fittrack.pro.data.local.entity.TrainingSessionEntity
import com.fittrack.pro.domain.repository.RoutineRepository
import com.fittrack.pro.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutineDetailState(
    val routine: RoutineEntity? = null,
    val exercises: List<ExerciseEntity> = emptyList(),
    val setCountPerExercise: Map<Long, Int> = emptyMap(),
    val sessions: List<TrainingSessionEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineDetailState())
    val state: StateFlow<RoutineDetailState> = _state.asStateFlow()

    fun loadRoutine(routineId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val (routine, exercises) = routineRepository.getRoutineWithExercises(routineId)
            
            // Get routine exercises with set counts
            val routineExercises = routineRepository.getRoutineExercises(routineId).first()
            val setCountMap = routineExercises.associate { it.exerciseId to it.plannedSets }
            
            // Get sessions for this routine
            val sessions = workoutRepository.getSessionsByRoutine(routineId).first()
            
            _state.update {
                it.copy(
                    routine = routine,
                    exercises = exercises,
                    setCountPerExercise = setCountMap,
                    sessions = sessions.sortedByDescending { s -> s.startTime },
                    isLoading = false
                )
            }
        }
    }
}
