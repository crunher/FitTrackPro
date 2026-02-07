package com.fittrack.pro.data.local.dao

import androidx.room.*
import com.fittrack.pro.data.local.entity.TrainingSessionEntity
import com.fittrack.pro.data.local.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    
    // Sessions
    @Query("SELECT * FROM training_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<TrainingSessionEntity>>
    
    @Query("SELECT * FROM training_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): TrainingSessionEntity?
    
    @Query("SELECT * FROM training_sessions WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<TrainingSessionEntity?>
    
    @Query("SELECT * FROM training_sessions WHERE isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSession(): TrainingSessionEntity?
    
    @Query("SELECT * FROM training_sessions WHERE routineId = :routineId ORDER BY startTime DESC")
    fun getSessionsByRoutine(routineId: Long): Flow<List<TrainingSessionEntity>>
    
    @Query("""
        SELECT * FROM training_sessions 
        WHERE startTime >= :startDate AND startTime <= :endDate 
        ORDER BY startTime DESC
    """)
    fun getSessionsInRange(startDate: Long, endDate: Long): Flow<List<TrainingSessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity): Long
    
    @Update
    suspend fun updateSession(session: TrainingSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: TrainingSessionEntity)
    
    // Sets
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSetEntity>>
    
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getSetsBySessionSync(sessionId: Long): List<WorkoutSetEntity>
    
    @Query("""
        SELECT * FROM workout_sets 
        WHERE exerciseId = :exerciseId 
        ORDER BY completedAt DESC
    """)
    fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSetEntity>>
    
    @Query("""
        SELECT * FROM workout_sets 
        WHERE exerciseId = :exerciseId AND completed = 1
        ORDER BY completedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentSetsByExercise(exerciseId: Long, limit: Int = 50): List<WorkoutSetEntity>
    
    @Query("""
        SELECT ws.* FROM workout_sets ws
        INNER JOIN training_sessions ts ON ws.sessionId = ts.id
        WHERE ws.exerciseId = :exerciseId 
        AND ws.completed = 1
        AND ts.startTime >= :startDate
        ORDER BY ws.completedAt DESC
    """)
    fun getSetsByExerciseInRange(exerciseId: Long, startDate: Long): Flow<List<WorkoutSetEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)
    
    @Update
    suspend fun updateSet(set: WorkoutSetEntity)
    
    @Delete
    suspend fun deleteSet(set: WorkoutSetEntity)
    
    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsBySession(sessionId: Long)
    
    // Stats queries
    @Query("""
        SELECT MAX(weight) FROM workout_sets 
        WHERE exerciseId = :exerciseId AND completed = 1 AND weight IS NOT NULL
    """)
    suspend fun getMaxWeight(exerciseId: Long): Double?
    
    @Query("""
        SELECT MAX(reps) FROM workout_sets 
        WHERE exerciseId = :exerciseId AND completed = 1 AND reps IS NOT NULL
    """)
    suspend fun getMaxReps(exerciseId: Long): Int?
    
    @Query("""
        SELECT COUNT(DISTINCT sessionId) FROM workout_sets 
        WHERE exerciseId = :exerciseId AND completed = 1
    """)
    suspend fun getSessionCountForExercise(exerciseId: Long): Int
}
