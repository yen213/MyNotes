package com.example.note;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * DAO class for accessing the data in the Note Database
 *
 * @see Note Table structure for this DAO
 * @see NoteDatabase Room Database that uses this DAO
 */
@Dao
public interface NoteDao {
    // Insert a Note item in the table
    @Insert
    Completable insert(Note note);

    // Update a Note item in the table
    @Update
    Completable update(Note note);

    // Delete a Note item from the table
    @Delete
    Completable delete(Note note);

    // Delete all the Note items from the table
    @Query("DELETE FROM note_table")
    Completable deleteAllNotes();

    // Get all Favorite Notes from the table by date created in descending order
    @Query("SELECT * FROM note_table WHERE(favorite == 1) ORDER BY(date_created) DESC")
    Flowable<List<Note>> getFavorites();

    // Get all Note items from the table ordered by the title in ascending order
    @Query("SELECT * FROM note_table ORDER BY CAST(title as SIGNED) ASC")
    Flowable<List<Note>> getNotesTitleAsc();

    // Get all Note items from the table ordered by the title in descending order
    @Query("SELECT * FROM note_table ORDER BY CAST(title as SIGNED) DESC")
    Flowable<List<Note>> getNotesTitleDesc();

    // Get all Note items from the table ordered by the date created in ascending order
    @Query("SELECT * FROM note_table ORDER BY datetime(date_created) ASC")
    Flowable<List<Note>> getNotesCreatedAsc();

    // Get all Note items from the table ordered by the date created in descending order
    @Query("SELECT * FROM note_table ORDER BY datetime(date_created) DESC")
    Flowable<List<Note>> getNotesCreatedDesc();

    // Get all Note items from the table ordered by the date modified in ascending order
    @Query("SELECT * FROM note_table ORDER BY datetime(date_modified) ASC")
    Flowable<List<Note>> getNotesModifiedAsc();

    // Get all Note items from the table ordered by the date modified in descending order
    @Query("SELECT * FROM note_table ORDER BY datetime(date_modified) DESC")
    Flowable<List<Note>> getNotesModifiedDesc();
}
