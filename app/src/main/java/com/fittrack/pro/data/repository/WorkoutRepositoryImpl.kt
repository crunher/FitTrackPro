package com.fittrack.pro.data.repository

import com.fittrack.pro.data.local.dao.RoutineDao
import com.fittrack.pro.data.local.dao.WorkoutDao
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val routineDao: RoutineDao
) : WorkoutRepository {
    
    override fun getAllSessions(): Flow<List<TrainingSessionEntity>> = 
        workoutDao.getAllSessions()
    
    override suspend fun getSessionById(id: Long): TrainingSessionEntity? = 
        workoutDao.getSessionById(id)
    
    override suspend fun getActiveSession(): TrainingSessionEntity? = 
        workoutDao.getActiveSession()
    
    override fun getSessionsByRoutine(routineId: Long): Flow<List<TrainingSessionEntity>> = 
        workoutDao.getSessionsByRoutine(routineId)
    
    override fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSetEntity>> = 
        workoutDao.getSetsBySession(sessionId)
    
    override fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSetEntity>> = 
        workoutDao.getSetsByExercise(exerciseId)
    
    override suspend fun getRecentSetsByExercise(exerciseId: Long, limit: Int): List<WorkoutSetEntity> = 
        workoutDao.getRecentSetsByExercise(exerciseId, limit)
    
    override suspend fun startSession(routineId: Long, routineName: String): Long {
        val session = TrainingSessionEntity(
            routineId = routineId,
            routineName = routineName,
            startTime = System.currentTimeMillis()
        )
        val sessionId = workoutDao.insertSession(session)
        routineDao.updateLastUsed(routineId, System.currentTimeMillis())
        return sessionId
    }
    
    override suspend fun endSession(sessionId: Long, comment: String?) {
        val session = workoutDao.getSessionById(sessionId) ?: return
        val endTime = System.currentTimeMillis()
        val duration = endTime - session.startTime
        
        workoutDao.updateSession(
            session.copy(
                endTime = endTime,
                totalDuration = duration,
                comment = comment,
                isCompleted = true
            )
        )
    }
    
    override suspend fun addSet(set: WorkoutSetEntity): Long = 
        workoutDao.insertSet(set)
    
    override suspend fun updateSet(set: WorkoutSetEntity) = 
        workoutDao.updateSet(set)
    
    override suspend fun deleteSet(set: WorkoutSetEntity) = 
        workoutDao.deleteSet(set)
    
    override suspend fun getMaxWeight(exerciseId: Long): Double? = 
        workoutDao.getMaxWeight(exerciseId)
    
    override suspend fun getMaxReps(exerciseId: Long): Int? = 
        workoutDao.getMaxReps(exerciseId)
}
