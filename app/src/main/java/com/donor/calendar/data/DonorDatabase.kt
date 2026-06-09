package com.donor.calendar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DonationEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DonorDatabase : RoomDatabase() {

    abstract fun donationDao(): DonationDao

    companion object {
        @Volatile
        private var INSTANCE: DonorDatabase? = null

        fun getInstance(context: Context): DonorDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DonorDatabase::class.java,
                    "donor_calendar.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
