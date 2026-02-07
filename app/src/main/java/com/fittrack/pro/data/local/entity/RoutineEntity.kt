package com.fittrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Routine entity - a workout plan with assigned exercises
 */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val description: String? = null,
    
    val assignedDays: List<DayOfWeek> = emptyList(),
    
    val restTimeWorking: Int = 90,  // seconds
    val restTimeWarmup: Int = 60,   // seconds
    
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null,
    
    val isArchived: Boolean = false
)

/**
 * Junction table for routine exercises with order and settings
 */
@Entity(
    tableName = "routine_exercises",
    primaryKeys = ["routineId", "exerciseId"]
)
data class RoutineExerciseEntity(
    val routineId: Long,
    val exerciseId: Long,
    
    val orderIndex: Int,
    val plannedSets: Int = 3,
    
    val supersetGroupId: Int? = null, // null = not a superset
    
    val restTimeOverride: Int? = null // null = use routine default
)
