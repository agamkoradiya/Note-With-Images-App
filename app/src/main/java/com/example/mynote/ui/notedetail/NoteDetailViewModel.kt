package com.example.mynote.ui.notedetail

import androidx.lifecycle.ViewModel
import com.example.mynote.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    fun observeNoteByID(noteID: String) = mainRepository.observeNoteByID(noteID)

}