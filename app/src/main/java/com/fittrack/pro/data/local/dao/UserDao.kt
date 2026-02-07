package com.fittrack.pro.data.local.dao

import androidx.room.*
import com.fittrack.pro.data.local.entity.BodyMeasurementEntity
import com.fittrack.pro.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    // Body Measurements
    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurementEntity>>
    
    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMeasurement(): BodyMeasurementEntity?
    
    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    fun getLatestMeasurementFlow(): Flow<BodyMeasurementEntity?>
    
    @Query("SELECT * FROM body_measurements WHERE date >= :startDate ORDER BY date DESC")
    fun getMeasurementsInRange(startDate: Long): Flow<List<BodyMeasurementEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurementEntity): Long
    
    @Update
    suspend fun updateMeasurement(measurement: BodyMeasurementEntity)
    
    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurementEntity)
    
    // User Settings
    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettings(): UserSettingsEntity?
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettingsFlow(): Flow<UserSettingsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettingsEntity)
    
    @Update
    suspend fun updateUserSettings(settings: UserSettingsEntity)
}
