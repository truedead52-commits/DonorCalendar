package com.donor.calendar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // Хранится как epoch-day (количество дней с 1970-01-01) для простоты
    val date: Long,
    val type: DonationType
)
