package com.fittrack.pro.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.pro.data.local.entity.WorkoutSetEntity
import com.fittrack.pro.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseSummary(
    val exerciseId: Long,
    val exerciseName: String,
    val setsCompleted: Int,
    val maxWeight: Double?,
    val volume: Double
)

data class WorkoutSummaryState(
    val isLoading: Boolean = true,
    val routineName: String = "",
    val durationMinutes: Int = 0,
    val setsCompleted: Int = 0,
    val totalVolume: Double = 0.0,
    val exerciseCount: Int = 0,
    val comment: String? = null,
    val exerciseSummaries: List<ExerciseSummary> = emptyList()
)

@HiltViewModel
class WorkoutSummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutSummaryState())
    val state: StateFlow<WorkoutSummaryState> = _state.asStateFlow()

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            try {
                val session = workoutRepository.getSessionById(sessionId)
                if (session == null) {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }

                val sets = workoutRepository.getSetsBySession(sessionId).first()
                val completedSets = sets.filter { it.completed }

                // Calculate stats
                val durationMinutes = ((session.totalDuration ?: 0) / 60000).toInt()
                val totalVolume = completedSets.sumOf { 
                    (it.weight ?: 0.0) * (it.reps ?: 0) 
                }

                // Group by exercise
                val exerciseSummaries = completedSets
                    .groupBy { it.exerciseId }
                    .map { (exerciseId, exerciseSets) ->
                        ExerciseSummary(
                            exerciseId = exerciseId,
                            exerciseName = exerciseSets.first().exerciseName,
                            setsCompleted = exerciseSets.size,
                            maxWeight = exerciseSets.mapNotNull { it.weight }.maxOrNull(),
                            volume = exerciseSets.sumOf { 
                                (it.weight ?: 0.0) * (it.reps ?: 0) 
                            }
                        )
                    }

                _state.update {
                    it.copy(
                        isLoading = false,
                        routineName = session.routineName,
                        durationMinutes = durationMinutes,
                        setsCompleted = completedSets.size,
                        totalVolume = totalVolume,
                        exerciseCount = exerciseSummaries.size,
                        comment = session.comment,
                        exerciseSummaries = exerciseSummaries
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
