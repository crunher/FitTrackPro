package com.fittrack.pro.data.importer

import android.content.Context
import com.fittrack.pro.data.local.dao.ExerciseDao
import com.fittrack.pro.data.local.entity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ExerciseJson(
    val name: String,
    val namePl: String? = null,
    val description: String? = null,
    val category: String = "OTHER",
    val mainMuscle: String = "OTHER",
    val secondaryMuscles: List<String> = emptyList(),
    val videoPath: String? = null,
    val imagePaths: List<String> = emptyList(),
    val trackingType: String = "WEIGHT_X_REPS"
)

@Singleton
class ExerciseImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseDao: ExerciseDao
) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }
    
    suspend fun importExercisesFromAssets(): Int = withContext(Dispatchers.IO) {
        // Check if already imported
        val existingCount = exerciseDao.getExerciseCount()
        if (existingCount > 0) {
            return@withContext 0 // Already imported
        }
        
        try {
            val jsonString = context.assets.open("exercises.json")
                .bufferedReader()
                .use { it.readText() }
            
            val exerciseList = json.decodeFromString<List<ExerciseJson>>(jsonString)
            
            val entities = exerciseList.map { exercise ->
                ExerciseEntity(
                    name = exercise.name,
                    namePl = exercise.namePl,
                    description = exercise.description,
                    category = parseCategory(exercise.category),
                    trackingType = parseTrackingType(exercise.trackingType),
                    mainMuscle = parseMuscle(exercise.mainMuscle),
                    secondaryMuscles = exercise.secondaryMuscles.mapNotNull { parseMuscleOrNull(it) },
                    videoPath = exercise.videoPath,
                    imagePaths = exercise.imagePaths,
                    isCustom = false
                )
            }
            
            exerciseDao.insertAll(entities)
            entities.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
    
    private fun parseCategory(value: String): ExerciseCategory {
        return try {
            ExerciseCategory.valueOf(value)
        } catch (e: Exception) {
            // Map from muscle group names to equipment categories
            when (value.uppercase()) {
                "SHOULDERS", "BACK", "CHEST", "BICEPS", "TRICEPS", 
                "ABS", "LEGS", "GLUTES", "CALVES", "FOREARMS" -> ExerciseCategory.OTHER
                else -> ExerciseCategory.OTHER
            }
        }
    }
    
    private fun parseTrackingType(value: String): TrackingType {
        return try {
            TrackingType.valueOf(value)
        } catch (e: Exception) {
            TrackingType.WEIGHT_X_REPS
        }
    }
    
    private fun parseMuscle(value: String): MuscleGroup {
        return try {
            MuscleGroup.valueOf(value)
        } catch (e: Exception) {
            MuscleGroup.OTHER
        }
    }
    
    private fun parseMuscleOrNull(value: String): MuscleGroup? {
        return try {
            MuscleGroup.valueOf(value)
        } catch (e: Exception) {
            null
        }
    }
}
