package com.example.note;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Access point for managing Note data
 *
 * @see NoteDao
 */
public interface NoteDataSource {
    // Insert a Note object to the Note Table
    Completable insert(Note note);

    // Update a Note object in the Note Table
    Completable update(Note note);

    // Delete a Note object from the Note Table
    Completable delete(Note note);

    // Delete all Note objects from the Note Table
    Completable deleteAllNotes();

    // Get all Note items from the Note Table ordered by the title in ascending order
    Flowable<List<Note>> getNotesTitleAsc();

    // Get all Note items from the Note Table ordered by the title in descending order
    Flowable<List<Note>> getNotesTitleDesc();

    // Get all Note items from the Note Table ordered by the date created in ascending order
    Flowable<List<Note>> getNotesCreatedAsc();

    // Get all Note items from the Note Table ordered by the date created in descending order
    Flowable<List<Note>> getNotesCreatedDesc();

    // Get all Note items from the Note Table ordered by the date modified in ascending order
    Flowable<List<Note>> getNotesModifiedAsc();

    // Get all Note items from the Note Table ordered by the date modified in descending order
    Flowable<List<Note>> getNotesModifiedDesc();

    // Get all the favorite Note items from the Note Table
    Flowable<List<Note>> getFavorites();
}
