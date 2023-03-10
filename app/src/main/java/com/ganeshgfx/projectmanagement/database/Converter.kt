package com.ganeshgfx.projectmanagement.database

import androidx.room.TypeConverter
import com.ganeshgfx.projectmanagement.models.Status

class Converter {
    @TypeConverter
    fun toStatus(value: Int) = enumValues<Status>()[value]

    @TypeConverter
    fun fromStatus(value: Status) = value.ordinal
}