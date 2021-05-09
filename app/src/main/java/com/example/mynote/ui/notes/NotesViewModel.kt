package com.example.mynote.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.mynote.data.local.entities.Note
import com.example.mynote.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun getAllNotes() = mainRepository.getAllNotes().cachedIn(viewModelScope)

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.deleteNote(note)
        }
    }
}