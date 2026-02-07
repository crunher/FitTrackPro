package com.fittrack.pro.presentation.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExercisesUiState(
    val exercises: List<ExerciseEntity> = emptyList(),
    val filteredExercises: List<ExerciseEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscle: MuscleGroup? = null,
    val selectedCategory: ExerciseCategory? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()
    
    init {
        loadExercises()
    }
    
    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises()
                .collect { exercises ->
                    _uiState.update { state ->
                        state.copy(
                            exercises = exercises,
                            filteredExercises = filterExercises(
                                exercises,
                                state.searchQuery,
                                state.selectedMuscle,
                                state.selectedCategory
                            ),
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredExercises = filterExercises(
                    state.exercises,
                    query,
                    state.selectedMuscle,
                    state.selectedCategory
                )
            )
        }
    }
    
    fun onMuscleSelected(muscle: MuscleGroup?) {
        _uiState.update { state ->
            state.copy(
                selectedMuscle = muscle,
                filteredExercises = filterExercises(
                    state.exercises,
                    state.searchQuery,
                    muscle,
                    state.selectedCategory
                )
            )
        }
    }
    
    fun onCategorySelected(category: ExerciseCategory?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredExercises = filterExercises(
                    state.exercises,
                    state.searchQuery,
                    state.selectedMuscle,
                    category
                )
            )
        }
    }
    
    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                selectedMuscle = null,
                selectedCategory = null,
                filteredExercises = state.exercises
            )
        }
    }
    
    private fun filterExercises(
        exercises: List<ExerciseEntity>,
        query: String,
        muscle: MuscleGroup?,
        category: ExerciseCategory?
    ): List<ExerciseEntity> {
        return exercises.filter { exercise ->
            val matchesQuery = query.isEmpty() ||
                exercise.name.contains(query, ignoreCase = true) ||
                exercise.namePl?.contains(query, ignoreCase = true) == true
            
            val matchesMuscle = muscle == null || exercise.mainMuscle == muscle
            val matchesCategory = category == null || exercise.category == category
            
            matchesQuery && matchesMuscle && matchesCategory
        }
    }
}
