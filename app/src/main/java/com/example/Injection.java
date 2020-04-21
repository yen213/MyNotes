package com.example;

import android.content.Context;

import com.example.note.NoteDataSource;
import com.example.note.NoteDatabase;
import com.example.note.NoteRepository;
import com.example.ui.ViewModelFactory;

/**
 * Enables injection of data sources.
 */
public class Injection {
    public static NoteDataSource provideNoteRepository(Context context) {
        NoteDatabase db = NoteDatabase.getInstance(context);
        return new NoteRepository(db.noteDao());
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        NoteDataSource dataSource = provideNoteRepository(context);
        return new ViewModelFactory(dataSource);
    }
}
