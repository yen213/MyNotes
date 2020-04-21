package com.example.note;

import androidx.room.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Use the Note database as the data source
 *
 * @see NoteDataSource
 * @see NoteDao
 */
public class NoteRepository implements NoteDataSource {
    private final NoteDao mNoteDao;

    // Constructor
    public NoteRepository(NoteDao noteDao) {
        mNoteDao = noteDao;
    }

    /**
     * Database operations below
     */
    @Override
    public Completable insert(Note note) {
        return mNoteDao.insert(note);
    }

    @Override
    public Completable update(Note note) {
        return mNoteDao.update(note);
    }

    @Override
    public Completable delete(Note note) {
        return mNoteDao.delete(note);
    }

    @Override
    public Completable deleteAllNotes() {
        return mNoteDao.deleteAllNotes();
    }

    @Override
    public Flowable<List<Note>> getNotesTitleAsc() {
        return mNoteDao.getNotesTitleAsc();
    }

    @Override
    public Flowable<List<Note>> getNotesTitleDesc() {
        return mNoteDao.getNotesTitleDesc();
    }

    @Override
    public Flowable<List<Note>> getNotesCreatedAsc() {
        return mNoteDao.getNotesCreatedAsc();
    }

    @Override
    public Flowable<List<Note>> getNotesCreatedDesc() {
        return mNoteDao.getNotesCreatedDesc();
    }

    @Override
    public Flowable<List<Note>> getNotesModifiedAsc() {
        return mNoteDao.getNotesModifiedAsc();
    }

    @Override
    public Flowable<List<Note>> getNotesModifiedDesc() {
        return mNoteDao.getNotesModifiedDesc();
    }

    @Override
    public Flowable<List<Note>> getFavorites() {
        return mNoteDao.getFavorites();
    }
}
