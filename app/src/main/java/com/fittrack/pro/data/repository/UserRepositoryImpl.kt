package com.fittrack.pro.data.repository

import com.fittrack.pro.data.local.dao.UserDao
import com.fittrack.pro.data.local.entity.*
import com.fittrack.pro.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    
    override fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>> = 
        userDao.getAllMeasurements()
    
    override suspend fun getLatestMeasurement(): BodyMeasurementEntity? = 
        userDao.getLatestMeasurement()
    
    override fun getLatestMeasurementFlow(): Flow<BodyMeasurementEntity?> = 
        userDao.getLatestMeasurementFlow()
    
    override suspend fun addMeasurement(measurement: BodyMeasurementEntity): Long = 
        userDao.insertMeasurement(measurement)
    
    override fun getUserSettingsFlow(): Flow<UserSettingsEntity?> = 
        userDao.getUserSettingsFlow()
    
    override suspend fun getUserSettings(): UserSettingsEntity? {
        val settings = userDao.getUserSettings()
        if (settings == null) {
            // Create default settings
            val defaultSettings = UserSettingsEntity()
            userDao.insertUserSettings(defaultSettings)
            return defaultSettings
        }
        return settings
    }
    
    override suspend fun updateUserSettings(settings: UserSettingsEntity) {
        userDao.updateUserSettings(settings.copy(updatedAt = System.currentTimeMillis()))
    }
}
