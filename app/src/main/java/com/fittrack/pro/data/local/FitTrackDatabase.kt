package com.fittrack.pro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fittrack.pro.data.local.dao.*
import com.fittrack.pro.data.local.entity.*

@Database(
    entities = [
        ExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        TrainingSessionEntity::class,
        WorkoutSetEntity::class,
        BodyMeasurementEntity::class,
        UserSettingsEntity::class
    ],
    version = 2, // Bumped: removed autoGenerate from ExerciseEntity
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    
    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun userDao(): UserDao
    
    companion object {
        const val DATABASE_NAME = "fittrack_database"
    }
}
