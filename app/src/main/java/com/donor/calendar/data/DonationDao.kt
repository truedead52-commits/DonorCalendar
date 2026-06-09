package com.donor.calendar.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDao {

    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Query("SELECT * FROM donations ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDonation(): DonationEntity?

    // date хранится как epoch-day, поэтому сравниваем напрямую
    @Query("SELECT * FROM donations WHERE date >= :fromEpochDay AND type = 'BLOOD'")
    suspend fun getBloodDonationsSince(fromEpochDay: Long): List<DonationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donation: DonationEntity)

    @Delete
    suspend fun delete(donation: DonationEntity)
}
