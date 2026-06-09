package com.donor.calendar.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDonationType(type: DonationType): String = type.name

    @TypeConverter
    fun toDonationType(name: String): DonationType = DonationType.valueOf(name)
}
