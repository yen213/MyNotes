package com.example.note;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Database table for a note object
 *
 * @see NoteDao DAO class for this table
 */
@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "date_modified")
    private String modified;
    @ColumnInfo(name = "date_created")
    private String created;
    private String title, description;
    private boolean favorite;

    // Parameterized constructor
    public Note(String title, String description, String modified, String created, boolean favorite) {
        this.title = title;
        this.description = description;
        this.modified = modified;
        this.created = created;
        this.favorite = favorite;
    }

    /**
     * Set the ID of a Note
     *
     * @param id The ID number of the Note
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getters
     */
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getModified() {
        return modified;
    }

    public String getCreated() {
        return created;
    }

    public boolean isFavorite() {
        return favorite;
    }
}
