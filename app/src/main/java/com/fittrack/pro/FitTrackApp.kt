package com.fittrack.pro

import android.app.Application
import com.fittrack.pro.data.seeding.ExerciseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FitTrackApp : Application() {

    @Inject
    lateinit var exerciseSeeder: ExerciseSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Seed exercises on first run
        applicationScope.launch {
            exerciseSeeder.seedIfNeeded()
        }
    }
}

