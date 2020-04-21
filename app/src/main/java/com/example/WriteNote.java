package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.note.Note;
import com.example.note.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity where a new Note item is created or where an existing Note item is edited.
 */
public class WriteNote extends AppCompatActivity implements DeleteDialog.DeleteDialogListener {
    // Constants
    private static final String TAG = WriteNote.class.getName();
    private static final String DELETE_DIALOG = TAG + ".DELETE_DIALOG";
    public static final String ID_EXTRA = TAG + ".ID_EXTRA";
    public static final String TITLE_EXTRA = TAG + ".TITLE_EXTRA";
    public static final String DESCRIPTION_EXTRA = TAG + ".DESCRIPTION_EXTRA";
    public static final String CREATED_EXTRA = TAG + ".CREATED_EXTRA";
    public static final String MODIFIED_EXTRA = TAG + ".MODIFIED_EXTRA";
    public static final String FAVORITE_EXTRA = TAG + ".FAVORITE_EXTRA";
    public static final String DELETE_EXTRA = TAG + ".DELETE_EXTRA";

    // Views
    private ImageView mFavoriteImageView;
    private EditText mTitleEditText, mDescriptionEditText;

    // Member variables
    private Intent mReturnData = new Intent();
    private Intent mReceivedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        setViewsAndListeners();
        mReceivedData = getIntent();

        // If the activity is started to update an existing Note, get the title and description of
        // that Note object and set them to the EditTextViews.
        if (mReceivedData.hasExtra(ID_EXTRA)) {
            // Use the filled favorite icon if editing a favorite Note item
            if (mReceivedData.getBooleanExtra(FAVORITE_EXTRA, false)) {
                mFavoriteImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.favorite_icon_filled));
            }
            mTitleEditText.setText(mReceivedData.getStringExtra(TITLE_EXTRA));
            mDescriptionEditText.setText(mReceivedData.getStringExtra(DESCRIPTION_EXTRA));
        }
    }

    /**
     * Create the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Get the item selected in the options menu and perform it's actions.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_menu_item) {
            DeleteDialog dialog = new DeleteDialog(null);
            dialog.show(getSupportFragmentManager(), DELETE_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * If the delete button is clicked on the {@link DeleteDialog} dialog, sends the ID of the Note
     * object to delete from the database to the {@link MainActivity} activity.
     *
     * @param note The Note object to delete
     */
    @Override
    public void deleteClicked(Note note) {
        int id = mReceivedData.getIntExtra(ID_EXTRA, -1);

        mReturnData.putExtra(DELETE_EXTRA, true);
        mReturnData.putExtra(ID_EXTRA, id);
        setResult(RESULT_OK, mReturnData);
        finish();
    }

    /**
     * Set the Views member variables and attaches Listeners.
     */
    private void setViewsAndListeners() {
        // Set views
        mFavoriteImageView = findViewById(R.id.favorite_toolbar);
        ImageView mBackImageView = findViewById(R.id.back_toolbar);
        TextView mSaveTextView = findViewById(R.id.save_toolbar);
        mTitleEditText = findViewById(R.id.title_write_activity);
        mDescriptionEditText = findViewById(R.id.description_write_activity);

        // Set tag
        mFavoriteImageView.setTag(R.drawable.favorite_icon_unfilled);

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_write_activity);
        setSupportActionBar(toolbar);

        // Set listeners
        mFavoriteImageView.setOnClickListener(this::setFavorite);
        mSaveTextView.setOnClickListener(v -> saveNote());
        mBackImageView.setOnClickListener(v -> finish());
    }

    /**
     * Check that at least one of the EditText fields is not empty before continuing. Get the input
     * from {@link #mTitleEditText} and {@link #mDescriptionEditText}, the current System Date, and
     * Note id (if it is an existing Note) and send the data to {@link MainActivity} activity
     * through the {@link #mReturnData} Intent.
     */
    private void saveNote() {
        hideKeyboard();

        String title = mTitleEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();

        if (title.isEmpty() && description.isEmpty()) {
            return;
        }

        int id = mReceivedData.getIntExtra(ID_EXTRA, -1);

        // Format the current date
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String date = format.format(currentDate);

        mReturnData.putExtra(TITLE_EXTRA, title);
        mReturnData.putExtra(DESCRIPTION_EXTRA, description);
        mReturnData.putExtra(MODIFIED_EXTRA, date);

        // Get the creation date if it is an existing Note
        if (id != -1) {
            mReturnData.putExtra(ID_EXTRA, id);
            mReturnData.putExtra(CREATED_EXTRA, mReceivedData.getStringExtra(CREATED_EXTRA));
        } else {
            mReturnData.putExtra(CREATED_EXTRA, date);
        }
        setResult(RESULT_OK, mReturnData);
    }

    /**
     * Changes the {@link #mFavoriteImageView} icon color on the ToolBar and adds to the
     * {@link #mReturnData} Intent about this Note's favorite status.
     *
     * @param v The favorite icon on the ToolBar
     */
    private void setFavorite(View v) {
        if (v.getTag().equals(R.drawable.favorite_icon_unfilled)) {
            mFavoriteImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.favorite_icon_filled));
            mFavoriteImageView.setTag(R.drawable.favorite_icon_filled);
            mReturnData.putExtra(FAVORITE_EXTRA, true);
        } else {
            mFavoriteImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.favorite_icon_unfilled));
            mFavoriteImageView.setTag(R.drawable.favorite_icon_unfilled);
            mReturnData.putExtra(FAVORITE_EXTRA, false);
        }
    }

    /**
     * Hides the keyboard and the cursor.
     */
    private void hideKeyboard() {
        // Get the input method and the current focus
        InputMethodManager imm = (InputMethodManager) WriteNote.this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = WriteNote.this.getCurrentFocus();

        //If no view currently has focus, create a new one so we can grab a window token from it
        if (view == null) {
            view = new View(WriteNote.this);
        }

        // Hide the keyboard
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Get rid of the keyboard cursor
        mTitleEditText.clearFocus();
        mDescriptionEditText.clearFocus();
    }
}
