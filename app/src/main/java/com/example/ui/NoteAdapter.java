package com.example.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.note.Note;
import com.example.note.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * RecyclerView adapter for the Note items displayed in the {@link com.example.MainActivity}.
 */
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    // Milliseconds constant for a day and week
    long MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1);
    long MILLIS_PER_WEEK = TimeUnit.DAYS.toMillis(7);

    // Member variables
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    SimpleDateFormat mDbFormat;
    Date mCurrentDate;
    Locale mLocale;

    /**
     * Constructor
     */
    public NoteAdapter() {
        super(DIFF_CALLBACK);
        mLocale = Locale.getDefault();
        mCurrentDate = Calendar.getInstance().getTime();
        mDbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);
    }

    /**
     * Use the DiffUtil class to animate the items inserted and removed in the RecyclerView
     */
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        // Return true if both items are the same (same ID)
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        // Return true if all the contents except the modified date are the same
        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            if (oldItem != null || newItem != null) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.getCreated().equals(newItem.getCreated());
            }
            return false;
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);

        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);

        // Parse the Note creation string and format it based on the current date
        if (mCurrentDate != null && mLocale != null) {
            try {
                Date created = (mDbFormat.parse(currentNote.getCreated()));
                String time;
                long createdTime;
                long currentTime = mCurrentDate.getTime();

                if (created != null) {
                    createdTime = created.getTime();

                    // Note is less than a day old
                    if (Math.abs(currentTime - createdTime) < MILLIS_PER_DAY) {
                        time = new SimpleDateFormat("hh:mm a", mLocale)
                                .format(createdTime);
                        holder.dateTextView.setText(time);
                    }
                    // Note is more than a day old but less than a week old
                    else if (Math.abs(currentTime - createdTime) > MILLIS_PER_DAY &&
                            Math.abs(currentTime - createdTime) < MILLIS_PER_WEEK) {
                        time = new SimpleDateFormat("EE 'at' hh:mm a", mLocale)
                                .format(createdTime);
                        holder.dateTextView.setText(time);
                    }
                    // Note is more than a week old
                    else {
                        time = new SimpleDateFormat("MMM dd, yyyy", mLocale)
                                .format(createdTime);
                        holder.dateTextView.setText(time);
                    }
                } else {
                    holder.dateTextView.setText(currentNote.getCreated());
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.dateTextView.setText(currentNote.getCreated());
            }
        } else {
            holder.dateTextView.setText(currentNote.getCreated());
        }

        holder.titleTextView.setText(currentNote.getTitle());
        holder.descriptionTextView.setText(currentNote.getDescription());
    }

    /**
     * ViewHolder class
     */
    public class NoteHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, descriptionTextView, dateTextView;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            // Set views
            titleTextView = itemView.findViewById(R.id.title_note_item);
            descriptionTextView = itemView.findViewById(R.id.description_note_item);
            dateTextView = itemView.findViewById(R.id.date_note_item);

            // Set listeners
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (mClickListener != null && position != RecyclerView.NO_POSITION) {
                    mClickListener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();

                if (mLongClickListener != null && position != RecyclerView.NO_POSITION) {
                    mLongClickListener.onItemLongClicked(getItem(position));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Interface for onItemClickListener for each item in the adapter
     */
    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    /**
     * Interface for onItemLongClickedListener for each item in the adapter
     */
    public interface OnItemLongClickListener {
        void onItemLongClicked(Note note);
    }

    // Set the OnItemClickListener member variable
    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    // Set the OnItemLongClickListener member variable
    public void setOnLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }
}
