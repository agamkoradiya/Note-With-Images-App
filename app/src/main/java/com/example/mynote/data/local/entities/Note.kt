package com.example.mynote.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val date: Long,
    val title: String,
    val priority: Priority,
    val content: String?,
    val imageUrls: List<String>?
) : Parcelable