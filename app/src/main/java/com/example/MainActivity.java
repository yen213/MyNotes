package com.example;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;
import com.example.ui.NoteAdapter;
import com.example.ui.NoteViewModel;
import com.example.ui.ViewModelFactory;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SortDialog.SortDialogListener, DeleteDialog.DeleteDialogListener {
    // Constants
    private static final String TAG = MainActivity.class.getName();
    private static final String SORT_DIALOG = TAG + ".SORT_DIALOG";
    private static final String ALL_NOTES_TITLE = "All Notes";
    private static final String FAVORITE_NOTES_TITLE = "Favorites";
    private static final int WRITE_NOTE_REQUEST = 100;
    private static final int UPDATE_NOTE_REQUEST = 200;

    // Member variables
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RecyclerView mRecyclerView;
    private NoteViewModel mViewModel;
    private NoteAdapter mNoteAdapter;
    private String mSortBy, mOrderBy, mSortTitle, mSortCreated, mDesc;
    private boolean mScrollToTop = true, mAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView totalNotesTextView = findViewById(R.id.total_notes_text_view_main_activity);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_main_activity);

        // Set up the ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);

        // Get the sort and order string values
        mSortTitle = getResources().getString(R.string.string_title);
        mSortCreated = getResources().getString(R.string.string_date_created);
        mDesc = getResources().getString(R.string.string_desc);
        mSortBy = getResources().getString(R.string.string_date_created);
        mOrderBy = getResources().getString(R.string.string_desc);

        Log.d(TAG, "onCreate: " + mSortBy);

        // Set up the FAB
        FloatingActionButton fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WriteNote.class);
            startActivityForResult(intent, WRITE_NOTE_REQUEST);
        });

        // Set up the ViewModel
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(MainActivity.this);
        mViewModel = new ViewModelProvider(MainActivity.this, mViewModelFactory).get(NoteViewModel.class);

        setUpRecyclerView();

        mViewModel.getNotes().observe(this, notes -> {
            Log.d(TAG, "onCreate: called");
            String totalNotes = notes.size() + " notes";
            mNoteAdapter.submitList(notes, () -> mRecyclerView.scrollToPosition
                        ((!mScrollToTop && mAdded) ? notes.size() - 1 : 0));
            totalNotesTextView.setText(totalNotes);
        });
    }

    /**
     * Clean up all the subscriptions that was added to the CompositeDisposable {@link #mDisposable}.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    /**
     * Get the incoming data from {@link WriteNote} that's passed in to the Intent. Based on the
     * request code, use the Intent data to create a new {@link Note} object and add it to the
     * database, or update an existing one.
     *
     * @param requestCode Either a request to create a new Note or update an existing one
     * @param resultCode  Check the result of the Intent
     * @param data        The Intent data that is passed to this activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the data
        if (data != null) {
            Note note;
            String title = data.getStringExtra(WriteNote.TITLE_EXTRA);
            String description = data.getStringExtra(WriteNote.DESCRIPTION_EXTRA);
            String modified = data.getStringExtra(WriteNote.MODIFIED_EXTRA);
            String created = data.getStringExtra(WriteNote.CREATED_EXTRA);
            int id = data.getIntExtra(WriteNote.ID_EXTRA, -1);
            boolean favorite = data.getBooleanExtra(WriteNote.FAVORITE_EXTRA, false);

            if (data.getBooleanExtra(WriteNote.DELETE_EXTRA, false)) {
                if (id == -1) {
                    return;
                }

                note = new Note(title, description, modified, created, favorite);
                note.setId(id);

                deleteNote(note);
            }
            // Add a new Note object to the database
            else if (requestCode == WRITE_NOTE_REQUEST && resultCode == RESULT_OK) {
                note = new Note(title, description, modified, created, favorite);
                addNote(note);
            }
            // Update an existing Note object in the database, if it exists
            else if (requestCode == UPDATE_NOTE_REQUEST && resultCode == RESULT_OK && id != -1) {
                note = new Note(title, description, modified, created, favorite);
                note.setId(id);

                updateNote(note);

            }
        }
    }

    /**
     * Create the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Execute the appropriate functions based on the option selected from the MenuItems.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes_main_menu:
                deleteAllNotes();
                return true;

            case R.id.sort_main_menu:
                // Open a dialog box prompting on how to display the list
                SortDialog dialog = new SortDialog(mSortBy, mOrderBy);
                dialog.show(getSupportFragmentManager(), SORT_DIALOG);
                return true;

            case R.id.favorites_notes_main_menu:
                mCollapsingToolbarLayout.setTitle(FAVORITE_NOTES_TITLE);
                mViewModel.setQueryOption(QueryType.FAVORITE);
                return true;

            case R.id.all_notes_main_menu:
                mCollapsingToolbarLayout.setTitle(ALL_NOTES_TITLE);
                loadNote();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the option of how to display the list of Note items in the RecyclerView. Sets the
     * {@link #mSortBy} and {@link #mOrderBy} member variables with the options so that they can be
     * used to call the appropriate database query. Method is called when the positive button in the
     * dialog {@link SortDialog} is clicked.
     *
     * @param sort  How to sort the list by
     * @param order The order to sort by
     */
    @Override
    public void doneClicked(String sort, String order) {
        Log.d(TAG, "Sort by:" + sort + "\tOrder: " + order);

        mSortBy = sort;
        mOrderBy = order;

        loadNote();
    }

    @Override
    public void deleteClicked(Note note) {
        deleteNote(note);
    }

    /**
     * Sets up the RecyclerView, its Adapter, Listener, and swipe to delete functionality. A row in
     * the Note Table from the {@link com.example.note.NoteDatabase} can be updated by clicking
     * on that Note in the RecyclerView.
     */
    private void setUpRecyclerView() {
        // Set up RecyclerView
        mNoteAdapter = new NoteAdapter();
        mRecyclerView = findViewById(R.id.recycler_view_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNoteAdapter);

        // Set listener to the Adapter items
        mNoteAdapter.setOnItemClickListener(this::editNoteActivity);
        mNoteAdapter.setOnLongClickListener(note -> {
            DeleteDialog dialog = new DeleteDialog(note);
            dialog.show(getSupportFragmentManager(), "Delete Dialog");
        });

    }


    /**
     * Get data from the Note object and start the {@link WriteNote} activity by passing in that
     * data through an Intent. This allows a specific Note object from the Note Table to have its
     * contents updated.
     *
     * @param note The Note item from the table to be updated
     */
    private void editNoteActivity(Note note) {
        Intent intent = new Intent(MainActivity.this, WriteNote.class);
        intent.putExtra(WriteNote.ID_EXTRA, note.getId());
        intent.putExtra(WriteNote.TITLE_EXTRA, note.getTitle());
        intent.putExtra(WriteNote.DESCRIPTION_EXTRA, note.getDescription());
        intent.putExtra(WriteNote.CREATED_EXTRA, note.getCreated());
        intent.putExtra(WriteNote.FAVORITE_EXTRA, note.isFavorite());
        startActivityForResult(intent, UPDATE_NOTE_REQUEST);
    }

    /**
     * Load the Note Table from the Note Database {@link com.example.note.NoteDatabase} based on the
     * sort {@link #mSortBy} and order {@link #mOrderBy} by methods.
     */
    private void loadNote() {
        Log.d(TAG, "loadNote: LOADED");
        mAdded = false;

        if (mSortBy.equals(mSortCreated)) {
            if (mOrderBy.equals(mDesc)) {
                mViewModel.setQueryOption(QueryType.CREATED_DESC);
                mScrollToTop = true;
                Log.d(TAG, "getNotes: " + QueryType.CREATED_DESC);
            } else {
                mViewModel.setQueryOption(QueryType.CREATED_ASC);
                mScrollToTop = false;
                Log.d(TAG, "getNotes: " + QueryType.CREATED_ASC);
            }
        } else if (mSortBy.equals(mSortTitle)) {
            if (mOrderBy.equals(mDesc)) {
                mViewModel.setQueryOption(QueryType.TITLE_DESC);
                mScrollToTop = true;
                Log.d(TAG, "getNotes: " + QueryType.TITLE_DESC);
            } else {
                mViewModel.setQueryOption(QueryType.TITLE_ASC);
                mScrollToTop = false;
                Log.d(TAG, "getNotes: " + QueryType.TITLE_ASC);
            }
        } else {
            if (mOrderBy.equals(mDesc)) {
                mViewModel.setQueryOption(QueryType.MODIFIED_DESC);
                mScrollToTop = true;
                Log.d(TAG, "getNotes: " + QueryType.MODIFIED_DESC);
            } else {
                mViewModel.setQueryOption(QueryType.MODIFIED_ASC);
                mScrollToTop = false;
                Log.d(TAG, "getNotes: " + QueryType.MODIFIED_ASC);
            }
        }
    }

    /**
     * Add a Note item to the Note Table in the Note Database {@link com.example.note.NoteDatabase}.
     */
    private void addNote(Note note) {
        mDisposable.add(mViewModel.insertNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            Log.d(TAG, "New Note added to the table.");
                            mAdded = true;
                        },
                        throwable -> Log.d(TAG, "Error adding new Note to the table: " +
                                throwable.getMessage())));
    }

    /**
     * Delete a Note item from the Note Table in the Note Database
     * {@link com.example.note.NoteDatabase}.
     */
    private void deleteNote(Note note) {
        mDisposable.add(mViewModel.deleteNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d(TAG, "Note deleted from the table."),
                        throwable -> Log.d(TAG, "Error deleting Note from the table: " +
                                throwable.getMessage())));
    }

    /**
     * Deletes all the Note items from the Note Table in the Note Database
     * {@link com.example.note.NoteDatabase}.
     */
    private void deleteAllNotes() {
        mDisposable.add(mViewModel.deleteAllNote()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Log.d(TAG, "All Notes deleted from the table."),
                        throwable -> Log.d(TAG, "Error deleting all the Notes from the table: "
                                + throwable.getMessage())));
    }

    /**
     * Update the contents of a Note item in the Note Table from the Note Database
     * {@link com.example.note.NoteDatabase}.
     */
    private void updateNote(Note note) {
        mDisposable.add(mViewModel.updateNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(MainActivity.this, "Note updated",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Note updated successfully.");
                }, throwable -> Log.d(TAG, "Failed to update Note: " + throwable.getMessage())));
    }
}
