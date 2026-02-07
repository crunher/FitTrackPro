package com.fittrack.pro.domain.repository

import com.fittrack.pro.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<ExerciseEntity>>
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>
    fun getExercisesByMuscle(muscle: MuscleGroup): Flow<List<ExerciseEntity>>
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<ExerciseEntity>>
    fun getExercisesFiltered(muscle: MuscleGroup?, category: ExerciseCategory?): Flow<List<ExerciseEntity>>
    suspend fun getExerciseById(id: Long): ExerciseEntity?
    suspend fun getExerciseCount(): Int
    suspend fun insertExercise(exercise: ExerciseEntity): Long
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
    suspend fun updateExercise(exercise: ExerciseEntity)
    suspend fun deleteExercise(exercise: ExerciseEntity)
    suspend fun importExercisesFromAssets()
}

interface RoutineRepository {
    fun getAllRoutines(): Flow<List<RoutineEntity>>
    suspend fun getRoutineById(id: Long): RoutineEntity?
    fun getRoutineByIdFlow(id: Long): Flow<RoutineEntity?>
    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExerciseEntity>>
    suspend fun getRoutineWithExercises(routineId: Long): Pair<RoutineEntity?, List<ExerciseEntity>>
    suspend fun createRoutine(routine: RoutineEntity, exerciseIds: List<Long>): Long
    suspend fun updateRoutine(routine: RoutineEntity, exerciseIds: List<Long>)
    suspend fun deleteRoutine(routine: RoutineEntity)
    suspend fun updateLastUsed(routineId: Long)
}

interface WorkoutRepository {
    fun getAllSessions(): Flow<List<TrainingSessionEntity>>
    suspend fun getSessionById(id: Long): TrainingSessionEntity?
    suspend fun getActiveSession(): TrainingSessionEntity?
    fun getSessionsByRoutine(routineId: Long): Flow<List<TrainingSessionEntity>>
    fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSetEntity>>
    fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSetEntity>>
    suspend fun getRecentSetsByExercise(exerciseId: Long, limit: Int = 50): List<WorkoutSetEntity>
    suspend fun startSession(routineId: Long, routineName: String): Long
    suspend fun endSession(sessionId: Long, comment: String?)
    suspend fun addSet(set: WorkoutSetEntity): Long
    suspend fun updateSet(set: WorkoutSetEntity)
    suspend fun deleteSet(set: WorkoutSetEntity)
    suspend fun getMaxWeight(exerciseId: Long): Double?
    suspend fun getMaxReps(exerciseId: Long): Int?
}

interface UserRepository {
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>>
    suspend fun getLatestMeasurement(): BodyMeasurementEntity?
    fun getLatestMeasurementFlow(): Flow<BodyMeasurementEntity?>
    suspend fun addMeasurement(measurement: BodyMeasurementEntity): Long
    fun getUserSettingsFlow(): Flow<UserSettingsEntity?>
    suspend fun getUserSettings(): UserSettingsEntity?
    suspend fun updateUserSettings(settings: UserSettingsEntity)
}
