package com.fittrack.pro.data.repository

import com.fittrack.pro.data.local.dao.ExerciseDao
import com.fittrack.pro.data.local.dao.RoutineDao
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: ExerciseDao
) : RoutineRepository {
    
    override fun getAllRoutines(): Flow<List<RoutineEntity>> = 
        routineDao.getAllRoutines()
    
    override suspend fun getRoutineById(id: Long): RoutineEntity? = 
        routineDao.getRoutineById(id)
    
    override fun getRoutineByIdFlow(id: Long): Flow<RoutineEntity?> = 
        routineDao.getRoutineByIdFlow(id)
    
    override fun getRoutineExercises(routineId: Long): Flow<List<RoutineExerciseEntity>> = 
        routineDao.getRoutineExercises(routineId)
    
    override suspend fun getRoutineWithExercises(routineId: Long): Pair<RoutineEntity?, List<ExerciseEntity>> {
        val routine = routineDao.getRoutineById(routineId)
        if (routine == null) return null to emptyList()
        
        val routineExercises = routineDao.getRoutineExercisesSync(routineId)
        val exercises = routineExercises.mapNotNull { re ->
            exerciseDao.getExerciseById(re.exerciseId)
        }
        return routine to exercises
    }
    
    override suspend fun createRoutine(routine: RoutineEntity, exerciseIds: List<Long>): Long {
        val routineId = routineDao.insertRoutine(routine)
        
        val routineExercises = exerciseIds.mapIndexed { index, exerciseId ->
            RoutineExerciseEntity(
                routineId = routineId,
                exerciseId = exerciseId,
                orderIndex = index
            )
        }
        routineDao.insertRoutineExercises(routineExercises)
        
        return routineId
    }
    
    override suspend fun updateRoutine(routine: RoutineEntity, exerciseIds: List<Long>) {
        routineDao.updateRoutine(routine)
        routineDao.deleteRoutineExercises(routine.id)
        
        val routineExercises = exerciseIds.mapIndexed { index, exerciseId ->
            RoutineExerciseEntity(
                routineId = routine.id,
                exerciseId = exerciseId,
                orderIndex = index
            )
        }
        routineDao.insertRoutineExercises(routineExercises)
    }
    
    override suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteRoutineWithExercises(routine)
    }
    
    override suspend fun updateLastUsed(routineId: Long) {
        routineDao.updateLastUsed(routineId, System.currentTimeMillis())
    }
}
