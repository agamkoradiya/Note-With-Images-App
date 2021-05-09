package com.example.mynote.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.example.mynote.data.local.entities.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note WHERE id = :noteID")
    fun observeNoteById(noteID: String): LiveData<Note>

    @Query("select * from note where id = :id")
    suspend fun getNoteById(id: String): Note

    @Query("SELECT * FROM note ORDER BY date DESC")
    fun getAllNotes(): PagingSource<Int, Note>
}