package com.example.mynote.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mynote.data.local.entities.Priority
import com.example.mynote.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {


    fun verifyDataFromUser(title: String): Boolean {
        return title.isNotEmpty()
    }

    fun parsePriority(priority: String): Priority {
        return when (priority) {
            "High Priority" -> {
                Priority.HIGH
            }
            "Medium Priority" -> {
                Priority.MEDIUM
            }
            "Low Priority" -> {
                Priority.LOW
            }
            else -> Priority.LOW
        }
    }

    fun getIndexFromPriority(priority: Priority): Int {
        return when (priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }
}