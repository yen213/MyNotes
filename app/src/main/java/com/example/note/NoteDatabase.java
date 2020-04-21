package com.example.note;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Access point for the DAO class, used to get info from the Note Table
 *
 * @see Note Info in the Note Table
 * @see NoteDao Calls methods in this class to get info from the Note Table
 */
@Database(entities = {Note.class}, version = 2)
public abstract class NoteDatabase extends RoomDatabase {
    // Create only one instance of this class, can be used throughout the app
    private static NoteDatabase INSTANCE;

    // Method used to access the NoteDao class
    public abstract NoteDao noteDao();

    // Create the database and get a handle to the instance
    public static synchronized NoteDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
