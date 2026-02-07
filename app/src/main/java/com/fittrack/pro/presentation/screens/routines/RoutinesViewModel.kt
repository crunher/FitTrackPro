package com.fittrack.pro.presentation.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutinesUiState(
    val routines: List<RoutineEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()
    
    init {
        loadRoutines()
    }
    
    private fun loadRoutines() {
        viewModelScope.launch {
            routineRepository.getAllRoutines()
                .collect { routines ->
                    _uiState.update { state ->
                        state.copy(
                            routines = routines,
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun deleteRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(routine)
        }
    }
}
