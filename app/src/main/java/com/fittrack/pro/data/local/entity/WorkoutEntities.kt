package com.fittrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Training session - a single workout instance
 */
@Entity(
    tableName = "training_sessions",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("routineId")]
)
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val routineId: Long?,
    val routineName: String, // Snapshot of routine name
    
    val startTime: Long,
    val endTime: Long? = null,
    
    val totalDuration: Long? = null, // in milliseconds
    
    val comment: String? = null,
    
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Workout set - a single set within a session
 */
@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = TrainingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("exerciseId")]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String, // Snapshot
    
    val setNumber: Int,
    val setType: SetType = SetType.WORKING,
    
    // Tracking data (nullable based on tracking type)
    val weight: Double? = null,
    val reps: Int? = null,
    val time: Int? = null,       // seconds
    val distance: Double? = null, // km
    val resistance: String? = null, // e.g., "green band"
    
    // Intensity
    val rpe: Int? = null,  // 1-10
    val rir: Int? = null,  // 0-5 (reps in reserve)
    
    // Unilateral
    val side: Side? = null,
    
    val completed: Boolean = false,
    val completedAt: Long? = null,
    
    val notes: String? = null,
    
    // For temporary exercise swaps
    val originalExerciseId: Long? = null,
    val isSwapped: Boolean = false
)
