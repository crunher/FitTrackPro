package com.fittrack.pro.data.local.dao

import androidx.room.*
import com.fittrack.pro.data.local.entity.ExerciseCategory
import com.fittrack.pro.data.local.entity.ExerciseEntity
import com.fittrack.pro.data.local.entity.MuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity?
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseByIdFlow(id: Long): Flow<ExerciseEntity?>
    
    @Query("""
        SELECT * FROM exercises 
        WHERE name LIKE '%' || :query || '%' 
        OR namePl LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE mainMuscle = :muscle ORDER BY name ASC")
    fun getExercisesByMuscle(muscle: MuscleGroup): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<ExerciseEntity>>
    
    @Query("""
        SELECT * FROM exercises 
        WHERE (:muscle IS NULL OR mainMuscle = :muscle)
        AND (:category IS NULL OR category = :category)
        ORDER BY name ASC
    """)
    fun getExercisesFiltered(muscle: MuscleGroup?, category: ExerciseCategory?): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE isCustom = 1 ORDER BY name ASC")
    fun getCustomExercises(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)
    
    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
    
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
    
    @Query("DELETE FROM exercises WHERE isCustom = 0")
    suspend fun deleteNonCustomExercises()
}
