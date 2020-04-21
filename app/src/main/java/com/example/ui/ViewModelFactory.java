package com.example.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.note.NoteDataSource;

/**
 * Creates a new ViewModel from the data source
 */
public class ViewModelFactory implements ViewModelProvider.Factory {
    private final NoteDataSource mNoteDataSource;

    public ViewModelFactory(NoteDataSource mNoteDataSource) {
        this.mNoteDataSource = mNoteDataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NoteViewModel.class)) {
            return (T) new NoteViewModel(mNoteDataSource);
        }
        // Noinspection unchecked
        throw new IllegalArgumentException("Could not find the ViewModel class.");
    }
}
