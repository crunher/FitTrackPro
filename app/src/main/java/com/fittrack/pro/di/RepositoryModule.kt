package com.fittrack.pro.di

import com.fittrack.pro.data.importer.ExerciseImporter
import com.fittrack.pro.data.local.dao.*
import com.fittrack.pro.data.repository.*
import com.fittrack.pro.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideExerciseRepository(
        exerciseDao: ExerciseDao,
        exerciseImporter: ExerciseImporter
    ): ExerciseRepository {
        return ExerciseRepositoryImpl(exerciseDao, exerciseImporter)
    }
    
    @Provides
    @Singleton
    fun provideRoutineRepository(
        routineDao: RoutineDao,
        exerciseDao: ExerciseDao
    ): RoutineRepository {
        return RoutineRepositoryImpl(routineDao, exerciseDao)
    }
    
    @Provides
    @Singleton
    fun provideWorkoutRepository(
        workoutDao: WorkoutDao,
        routineDao: RoutineDao
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(workoutDao, routineDao)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository {
        return UserRepositoryImpl(userDao)
    }
}
