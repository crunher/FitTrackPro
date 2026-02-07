package com.fittrack.pro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Body measurement for tracking weight and body composition
 */
@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val date: Long,
    val bodyWeight: Double, // kg
    val bodyFat: Double? = null, // percentage
    
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * User settings and profile
 */
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Long = 1, // Singleton
    
    val displayName: String? = null,
    val email: String? = null,
    
    // Equipment profile
    val gymType: GymType = GymType.FULL_GYM,
    val availableEquipment: List<ExerciseCategory> = ExerciseCategory.entries,
    val availablePlates: List<Double> = listOf(1.25, 2.5, 5.0, 10.0, 15.0, 20.0, 25.0),
    val barbellWeight: Double = 20.0,
    
    // Preferences
    val useMetric: Boolean = true, // kg vs lbs
    val defaultRestTimeWorking: Int = 90,
    val defaultRestTimeWarmup: Int = 60,
    
    // Warmup template (percentages)
    val warmupTemplate: List<WarmupSet> = listOf(
        WarmupSet(0.50, 12),
        WarmupSet(0.70, 8),
        WarmupSet(0.90, 2)
    ),
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class GymType {
    FULL_GYM,      // Pełna siłownia
    HOME_GYM,      // Domowa siłownia
    DUMBBELLS_ONLY, // Tylko hantle
    BODYWEIGHT,    // Bez sprzętu
    OUTDOOR;       // Na zewnątrz

    fun getDisplayName(): String = when (this) {
        FULL_GYM -> "Pełna siłownia"
        HOME_GYM -> "Domowa siłownia"
        DUMBBELLS_ONLY -> "Tylko hantle"
        BODYWEIGHT -> "Bez sprzętu"
        OUTDOOR -> "Na zewnątrz"
    }
}

data class WarmupSet(
    val percentage: Double,
    val reps: Int
)
