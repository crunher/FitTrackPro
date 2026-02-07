package com.fittrack.pro.data.repository

import com.fittrack.pro.data.importer.ExerciseImporter
import com.fittrack.pro.data.local.dao.ExerciseDao
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val exerciseImporter: ExerciseImporter
) : ExerciseRepository {
    
    override fun getAllExercises(): Flow<List<ExerciseEntity>> = 
        exerciseDao.getAllExercises()
    
    override fun searchExercises(query: String): Flow<List<ExerciseEntity>> = 
        exerciseDao.searchExercises(query)
    
    override fun getExercisesByMuscle(muscle: MuscleGroup): Flow<List<ExerciseEntity>> = 
        exerciseDao.getExercisesByMuscle(muscle)
    
    override fun getExercisesByCategory(category: ExerciseCategory): Flow<List<ExerciseEntity>> = 
        exerciseDao.getExercisesByCategory(category)
    
    override fun getExercisesFiltered(muscle: MuscleGroup?, category: ExerciseCategory?): Flow<List<ExerciseEntity>> = 
        exerciseDao.getExercisesFiltered(muscle, category)
    
    override suspend fun getExerciseById(id: Long): ExerciseEntity? = 
        exerciseDao.getExerciseById(id)
    
    override suspend fun getExerciseCount(): Int = 
        exerciseDao.getExerciseCount()
    
    override suspend fun insertExercise(exercise: ExerciseEntity): Long = 
        exerciseDao.insertExercise(exercise)
    
    override suspend fun insertExercises(exercises: List<ExerciseEntity>) = 
        exerciseDao.insertExercises(exercises)
    
    override suspend fun updateExercise(exercise: ExerciseEntity) = 
        exerciseDao.updateExercise(exercise)
    
    override suspend fun deleteExercise(exercise: ExerciseEntity) = 
        exerciseDao.deleteExercise(exercise)
    
    override suspend fun importExercisesFromAssets() {
        exerciseImporter.importExercisesFromAssets()
    }
}

