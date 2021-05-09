package com.example.mynote.util

import android.content.Context
import android.widget.Toast

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

object Constants {
    const val DATABASE_NAME = "notes_db"
}