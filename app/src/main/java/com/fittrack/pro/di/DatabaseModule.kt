package com.fittrack.pro.di

import android.content.Context
import androidx.room.Room
import com.fittrack.pro.data.local.FitTrackDatabase
import com.fittrack.pro.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FitTrackDatabase {
        return Room.databaseBuilder(
            context,
            FitTrackDatabase::class.java,
            FitTrackDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideExerciseDao(database: FitTrackDatabase): ExerciseDao {
        return database.exerciseDao()
    }
    
    @Provides
    @Singleton
    fun provideRoutineDao(database: FitTrackDatabase): RoutineDao {
        return database.routineDao()
    }
    
    @Provides
    @Singleton
    fun provideWorkoutDao(database: FitTrackDatabase): WorkoutDao {
        return database.workoutDao()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: FitTrackDatabase): UserDao {
        return database.userDao()
    }
}
