package com.fittrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Exercise entity - represents a single exercise
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey // No autoGenerate - we provide IDs from JSON for seeded exercises
    val id: Long = 0,
    
    val name: String,
    val namePl: String? = null,
    val description: String? = null,
    
    val category: ExerciseCategory = ExerciseCategory.OTHER,
    val trackingType: TrackingType = TrackingType.WEIGHT_X_REPS,
    
    val mainMuscle: MuscleGroup = MuscleGroup.OTHER,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    
    val videoPath: String? = null,
    val imagePaths: List<String> = emptyList(),
    
    val unilateral: Boolean = false,
    val volumeMultiplier: Double = 1.0,
    
    val notes: String? = null,
    
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
