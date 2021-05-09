package com.example.mynote.repository

import androidx.paging.*
import com.example.mynote.data.local.NoteDao
import com.example.mynote.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val noteDao: NoteDao
) {

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun observeNoteByID(noteID: String) = noteDao.observeNoteById(noteID)

    suspend fun getNoteByID(id: String) = noteDao.getNoteById(id)

    fun getAllNotes(): Flow<PagingData<Note>> = Pager(
        PagingConfig(pageSize = 1, prefetchDistance = 10, enablePlaceholders = false, maxSize = 25)
    ) {
        noteDao.getAllNotes()
    }.flow

}