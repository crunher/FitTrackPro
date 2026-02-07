package com.fittrack.pro.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.ExerciseRepository
import com.fittrack.pro.domain.repository.RoutineRepository
import com.fittrack.pro.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class HomeUiState(
    val todayRoutine: RoutineEntity? = null,
    val recentRoutines: List<RoutineEntity> = emptyList(),
    val activeSession: TrainingSessionEntity? = null,
    val todayDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val exerciseCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            // Determine today's day
            val today = getTodayDayOfWeek()
            _uiState.update { it.copy(todayDayOfWeek = today) }
            
            // Seed sample exercises if needed
            exerciseRepository.importExercisesFromAssets()
            
            // Load exercise count
            val count = exerciseRepository.getExerciseCount()
            _uiState.update { it.copy(exerciseCount = count) }
            
            // Check for active session
            val activeSession = workoutRepository.getActiveSession()
            _uiState.update { it.copy(activeSession = activeSession) }
            
            // Load routines
            routineRepository.getAllRoutines()
                .collect { routines ->
                    val todayRoutine = routines.find { routine ->
                        routine.assignedDays.contains(today)
                    }
                    
                    _uiState.update { state ->
                        state.copy(
                            todayRoutine = todayRoutine,
                            recentRoutines = routines.take(5),
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    private fun getTodayDayOfWeek(): DayOfWeek {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            Calendar.SUNDAY -> DayOfWeek.SUNDAY
            else -> DayOfWeek.MONDAY
        }
    }
    
    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadData()
    }
}
