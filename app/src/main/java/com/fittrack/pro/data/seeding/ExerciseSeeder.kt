package com.fittrack.pro.data.seeding

import android.content.Context
import com.fittrack.pro.data.local.dao.ExerciseDao
import com.fittrack.pro.data.local.entity.ExerciseCategory
import com.fittrack.pro.data.local.entity.ExerciseEntity
import com.fittrack.pro.data.local.entity.MuscleGroup
import com.fittrack.pro.data.local.entity.TrackingType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the database with exercises from JSON file in assets
 */
@Singleton
class ExerciseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseDao: ExerciseDao
) {
    companion object {
        private const val EXERCISES_FILE = "exercises.json"
        private const val PREFS_NAME = "exercise_seeder_prefs"
        private const val KEY_SEEDED_VERSION = "exercises_seeded_v3" // Bumped to force reseed
        private const val EXPECTED_EXERCISE_COUNT = 400 // Minimum expected
    }

    /**
     * Seeds exercises if not already seeded or if count is too low
     */
    suspend fun seedIfNeeded() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Check if already seeded with new version
        if (prefs.getBoolean(KEY_SEEDED_VERSION, false)) {
            val existingCount = exerciseDao.getExerciseCount()
            if (existingCount >= EXPECTED_EXERCISE_COUNT) {
                return // Already have enough exercises
            }
        }

        // Clear old exercises and reseed
        exerciseDao.deleteNonCustomExercises()
        seedExercises()
        prefs.edit().putBoolean(KEY_SEEDED_VERSION, true).apply()
    }

    /**
     * Seeds all exercises from JSON
     */
    suspend fun seedExercises() = withContext(Dispatchers.IO) {
        val json = context.assets.open(EXERCISES_FILE).bufferedReader().use { it.readText() }
        val exercises = parseExercises(json)
        exerciseDao.insertAll(exercises)
    }

    private fun parseExercises(json: String): List<ExerciseEntity> {
        val jsonArray = JSONArray(json)
        val exercises = mutableListOf<ExerciseEntity>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            exercises.add(parseExercise(obj))
        }

        return exercises
    }

    private fun parseExercise(obj: JSONObject): ExerciseEntity {
        val id = obj.getLong("id")
        val name = obj.getString("name")
        val namePl = obj.optString("namePl", name)
        val description = obj.optString("description", null)
        
        val category = parseCategory(obj.optString("category", "OTHER"))
        val trackingType = parseTrackingType(obj.optString("trackingType", "WEIGHT_X_REPS"))
        val mainMuscle = parseMuscleGroup(obj.optString("mainMuscle", "OTHER"))
        val secondaryMuscles = parseSecondaryMuscles(obj.optJSONArray("secondaryMuscles"))
        
        val videoUrl = obj.optString("videoUrl", null)
        val imageUrls = parseStringArray(obj.optJSONArray("imageUrls"))
        
        val unilateral = obj.optBoolean("unilateral", false)
        val volumeMultiplier = obj.optDouble("volumeMultiplier", 1.0)
        val isCustom = obj.optBoolean("isCustom", false)

        return ExerciseEntity(
            id = id,
            name = name,
            namePl = namePl,
            description = description,
            category = category,
            trackingType = trackingType,
            mainMuscle = mainMuscle,
            secondaryMuscles = secondaryMuscles,
            videoPath = videoUrl,
            imagePaths = imageUrls,
            unilateral = unilateral,
            volumeMultiplier = volumeMultiplier,
            isCustom = isCustom
        )
    }

    private fun parseCategory(value: String): ExerciseCategory {
        return try {
            ExerciseCategory.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ExerciseCategory.OTHER
        }
    }

    private fun parseTrackingType(value: String): TrackingType {
        return try {
            TrackingType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TrackingType.WEIGHT_X_REPS
        }
    }

    private fun parseMuscleGroup(value: String): MuscleGroup {
        return try {
            MuscleGroup.valueOf(value)
        } catch (e: IllegalArgumentException) {
            MuscleGroup.OTHER
        }
    }

    private fun parseSecondaryMuscles(array: JSONArray?): List<MuscleGroup> {
        if (array == null) return emptyList()
        val muscles = mutableListOf<MuscleGroup>()
        for (i in 0 until array.length()) {
            val value = array.getString(i)
            muscles.add(parseMuscleGroup(value))
        }
        return muscles
    }

    private fun parseStringArray(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val result = mutableListOf<String>()
        for (i in 0 until array.length()) {
            result.add(array.getString(i))
        }
        return result
    }
}
