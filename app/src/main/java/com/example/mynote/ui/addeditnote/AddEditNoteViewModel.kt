package com.example.mynote.ui.addeditnote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynote.data.local.entities.Note
import com.example.mynote.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AddEditNoteViewModel"

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _savedNote = MutableLiveData<Note>()
    val savedNote: LiveData<Note> = _savedNote

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepository.updateNote(note)
        }
    }

    fun getNoteById(id: String) = viewModelScope.launch {
        val note = mainRepository.getNoteByID(id)
        _savedNote.postValue(note)
    }
}