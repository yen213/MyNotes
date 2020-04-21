package com.example.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.QueryType;
import com.example.note.Note;
import com.example.note.NoteDataSource;

import java.util.List;

import io.reactivex.Completable;

/**
 * ViewModel class used to access the Note items in the database and observe their changes and update
 * the UI if needed.
 */
public class NoteViewModel extends ViewModel {
    private final NoteDataSource mNoteDataSource;
    private MutableLiveData<Integer> mSetQuery = new MutableLiveData<>(QueryType.CREATED_DESC);
    private LiveData<List<Note>> mNotes = Transformations.switchMap(mSetQuery, this::getNotes);

    /**
     * Instantiate the ViewModel with the data source object
     *
     * @param mNoteDataSource Object to get the data from
     */
    public NoteViewModel(NoteDataSource mNoteDataSource) {
        this.mNoteDataSource = mNoteDataSource;
    }

    /**
     * Used to changed the {@link #mNotes} list of Note objects to the appropriate query type so
     * that RecyclerView using this list can change its data as well to reflect the new query type.
     *
     * @param query The query method to search the database by
     * @return List of Notes reflecting the new query type
     */
    public LiveData<List<Note>> getNotes(int query) {
        switch (query) {
            case QueryType.CREATED_DESC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesCreatedDesc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.CREATED_ASC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesCreatedAsc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.TITLE_DESC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesTitleDesc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.TITLE_ASC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesTitleAsc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.MODIFIED_DESC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesModifiedDesc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.MODIFIED_ASC:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getNotesModifiedAsc());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;

            case QueryType.FAVORITE:
                mNotes = LiveDataReactiveStreams.fromPublisher(mNoteDataSource.getFavorites());
                Log.d("ViewModel", "getNotes: " + mSetQuery.getValue());
                break;
        }
        return mNotes;
    }

    /**
     * Get the list of Note items from the database.
     *
     * @return List of Notes
     */
    public LiveData<List<Note>> getNotes() {
        return mNotes;
    }

    /**
     * Set the database query type.
     *
     * @param queryOption Query method to search the database by
     */
    public void setQueryOption(int queryOption) {
        mSetQuery.setValue(queryOption);
    }

    /**
     * Insert a Note item into the database.
     *
     * @param note New Note item
     */
    public Completable insertNote(Note note) {
        return mNoteDataSource.insert(note);
    }

    /**
     * Delete a Note item from the database.
     *
     * @param note Note item to delete
     */
    public Completable deleteNote(Note note) {
        return mNoteDataSource.delete(note);
    }

    /**
     * Insert all Note items from the database.
     */
    public Completable deleteAllNote() {
        return mNoteDataSource.deleteAllNotes();
    }

    /**
     * Update a Note item in the database
     *
     * @param note Note item to update
     */
    public Completable updateNote(Note note) {
        return mNoteDataSource.update(note);
    }
}
