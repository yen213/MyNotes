package com.example;

/**
 * Interface used to identify which query method to use to get all the Note items from the database.
 *
 * @see com.example.ui.NoteViewModel
 * @see com.example.note.NoteDataSource
 */
public interface QueryType {
    int CREATED_ASC = 100;
    int CREATED_DESC = 200;
    int TITLE_ASC = 300;
    int TITLE_DESC = 400;
    int MODIFIED_ASC = 500;
    int MODIFIED_DESC = 600;
    int FAVORITE = 700;
}
