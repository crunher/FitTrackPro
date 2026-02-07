package com.fittrack.pro.data.local

import androidx.room.TypeConverter
import com.fittrack.pro.data.local.entity.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    // MuscleGroup
    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = 
        try { MuscleGroup.valueOf(value) } catch (e: Exception) { MuscleGroup.OTHER }

    // List<MuscleGroup>
    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>): String = 
        json.encodeToString(value.map { it.name })

    @TypeConverter
    fun toMuscleGroupList(value: String): List<MuscleGroup> = 
        try {
            json.decodeFromString<List<String>>(value).map { 
                try { MuscleGroup.valueOf(it) } catch (e: Exception) { MuscleGroup.OTHER }
            }
        } catch (e: Exception) { emptyList() }

    // ExerciseCategory
    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory): String = value.name

    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory = 
        try { ExerciseCategory.valueOf(value) } catch (e: Exception) { ExerciseCategory.OTHER }

    // List<ExerciseCategory>
    @TypeConverter
    fun fromExerciseCategoryList(value: List<ExerciseCategory>): String = 
        json.encodeToString(value.map { it.name })

    @TypeConverter
    fun toExerciseCategoryList(value: String): List<ExerciseCategory> = 
        try {
            json.decodeFromString<List<String>>(value).map { 
                try { ExerciseCategory.valueOf(it) } catch (e: Exception) { ExerciseCategory.OTHER }
            }
        } catch (e: Exception) { emptyList() }

    // TrackingType
    @TypeConverter
    fun fromTrackingType(value: TrackingType): String = value.name

    @TypeConverter
    fun toTrackingType(value: String): TrackingType = 
        try { TrackingType.valueOf(value) } catch (e: Exception) { TrackingType.WEIGHT_X_REPS }

    // SetType
    @TypeConverter
    fun fromSetType(value: SetType): String = value.name

    @TypeConverter
    fun toSetType(value: String): SetType = 
        try { SetType.valueOf(value) } catch (e: Exception) { SetType.WORKING }

    // Side
    @TypeConverter
    fun fromSide(value: Side?): String? = value?.name

    @TypeConverter
    fun toSide(value: String?): Side? = 
        value?.let { try { Side.valueOf(it) } catch (e: Exception) { null } }

    // GymType
    @TypeConverter
    fun fromGymType(value: GymType): String = value.name

    @TypeConverter
    fun toGymType(value: String): GymType = 
        try { GymType.valueOf(value) } catch (e: Exception) { GymType.FULL_GYM }

    // List<DayOfWeek>
    @TypeConverter
    fun fromDayOfWeekList(value: List<DayOfWeek>): String = 
        json.encodeToString(value.map { it.name })

    @TypeConverter
    fun toDayOfWeekList(value: String): List<DayOfWeek> = 
        try {
            json.decodeFromString<List<String>>(value).map { 
                try { DayOfWeek.valueOf(it) } catch (e: Exception) { DayOfWeek.MONDAY }
            }
        } catch (e: Exception) { emptyList() }

    // List<String>
    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = 
        try { json.decodeFromString(value) } catch (e: Exception) { emptyList() }

    // List<Double>
    @TypeConverter
    fun fromDoubleList(value: List<Double>): String = json.encodeToString(value)

    @TypeConverter
    fun toDoubleList(value: String): List<Double> = 
        try { json.decodeFromString(value) } catch (e: Exception) { emptyList() }

    // List<WarmupSet>
    @TypeConverter
    fun fromWarmupSetList(value: List<WarmupSet>): String = 
        json.encodeToString(value.map { "${it.percentage}:${it.reps}" })

    @TypeConverter
    fun toWarmupSetList(value: String): List<WarmupSet> = 
        try {
            json.decodeFromString<List<String>>(value).map { 
                val parts = it.split(":")
                WarmupSet(parts[0].toDouble(), parts[1].toInt())
            }
        } catch (e: Exception) { 
            listOf(WarmupSet(0.50, 12), WarmupSet(0.70, 8), WarmupSet(0.90, 2))
        }
}
