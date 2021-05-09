package com.example.mynote.data.local.converters

import androidx.room.TypeConverter
import com.example.mynote.data.local.entities.Priority
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    // For convert Priority type to String
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    // For convert String type to Priority
    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }

    // For convert List of String type to String
    @TypeConverter
    fun fromList1(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    // For convert String type to List of String
    @TypeConverter
    fun toList2(string: String?): List<String>? {
        return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
    }
}