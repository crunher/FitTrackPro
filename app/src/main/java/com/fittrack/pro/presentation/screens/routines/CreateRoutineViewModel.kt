package com.fittrack.pro.presentation.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.DayOfWeek
import com.fittrack.pro.data.local.entity.ExerciseEntity
import com.fittrack.pro.data.local.entity.RoutineEntity
import com.fittrack.pro.domain.repository.ExerciseRepository
import com.fittrack.pro.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateRoutineState(
    val name: String = "",
    val description: String = "",
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val restTimeWorking: Int = 90,
    val restTimeWarmup: Int = 60,
    val selectedExercises: List<ExerciseEntity> = emptyList(),
    val availableExercises: List<ExerciseEntity> = emptyList(),
    val searchQuery: String = "",
    val isShowingExercisePicker: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class CreateRoutineEvent {
    data class RoutineCreated(val routineId: Long) : CreateRoutineEvent()
    data class Error(val message: String) : CreateRoutineEvent()
}

@HiltViewModel
class CreateRoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRoutineState())
    val state: StateFlow<CreateRoutineState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreateRoutineEvent>()
    val events: SharedFlow<CreateRoutineEvent> = _events.asSharedFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _state.update { it.copy(availableExercises = exercises) }
            }
        }
    }

    fun updateName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun toggleDay(day: DayOfWeek) {
        _state.update { current ->
            val newDays = if (day in current.selectedDays) {
                current.selectedDays - day
            } else {
                current.selectedDays + day
            }
            current.copy(selectedDays = newDays)
        }
    }

    fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun showExercisePicker() {
        _state.update { it.copy(isShowingExercisePicker = true) }
    }

    fun hideExercisePicker() {
        _state.update { it.copy(isShowingExercisePicker = false, searchQuery = "") }
    }

    fun addExercise(exercise: ExerciseEntity) {
        _state.update { current ->
            if (exercise !in current.selectedExercises) {
                current.copy(selectedExercises = current.selectedExercises + exercise)
            } else current
        }
    }

    fun removeExercise(exercise: ExerciseEntity) {
        _state.update { current ->
            current.copy(selectedExercises = current.selectedExercises - exercise)
        }
    }

    fun moveExercise(from: Int, to: Int) {
        _state.update { current ->
            val exercises = current.selectedExercises.toMutableList()
            if (from in exercises.indices && to in exercises.indices) {
                val item = exercises.removeAt(from)
                exercises.add(to, item)
            }
            current.copy(selectedExercises = exercises)
        }
    }

    fun saveRoutine() {
        val currentState = _state.value
        
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Podaj nazwę rutyny") }
            return
        }
        
        if (currentState.selectedExercises.isEmpty()) {
            _state.update { it.copy(error = "Dodaj przynajmniej jedno ćwiczenie") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            try {
                val routine = RoutineEntity(
                    name = currentState.name.trim(),
                    description = currentState.description.trim().takeIf { it.isNotBlank() },
                    assignedDays = currentState.selectedDays.toList().sorted(),
                    restTimeWorking = currentState.restTimeWorking,
                    restTimeWarmup = currentState.restTimeWarmup
                )
                
                val exerciseIds = currentState.selectedExercises.map { it.id }
                val routineId = routineRepository.createRoutine(routine, exerciseIds)
                
                _events.emit(CreateRoutineEvent.RoutineCreated(routineId))
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
