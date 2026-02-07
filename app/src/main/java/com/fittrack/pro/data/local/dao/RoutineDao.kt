package com.fittrack.pro.data.local.dao

import androidx.room.*
import com.fittrack.pro.data.local.entity.DayOfWeek
import com.fittrack.pro.data.local.entity.RoutineEntity
import com.fittrack.pro.data.local.entity.RoutineExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    
    @Query("SELECT * FROM routines WHERE isArchived = 0 ORDER BY lastUsedAt DESC, createdAt DESC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>
    
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: Long): RoutineEntity?
    
    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineByIdFlow(id: Long): Flow<RoutineEntity?>
    
    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExerciseEntity>>
    
    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    suspend fun getRoutineExercisesSync(routineId: Long): List<RoutineExerciseEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(routineExercise: RoutineExerciseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(routineExercises: List<RoutineExerciseEntity>)
    
    @Update
    suspend fun updateRoutine(routine: RoutineEntity)
    
    @Query("UPDATE routines SET lastUsedAt = :timestamp WHERE id = :routineId")
    suspend fun updateLastUsed(routineId: Long, timestamp: Long)
    
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
    
    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteRoutineExercises(routineId: Long)
    
    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId AND exerciseId = :exerciseId")
    suspend fun deleteRoutineExercise(routineId: Long, exerciseId: Long)
    
    @Query("UPDATE routines SET isArchived = 1 WHERE id = :routineId")
    suspend fun archiveRoutine(routineId: Long)
    
    @Transaction
    suspend fun deleteRoutineWithExercises(routine: RoutineEntity) {
        deleteRoutineExercises(routine.id)
        deleteRoutine(routine)
    }
}
