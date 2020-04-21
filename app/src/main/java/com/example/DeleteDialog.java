package com.example;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.note.Note;
import com.example.note.R;

/**
 * Dialog to ask for confirmation before delete a Note item from the database.
 */
public class DeleteDialog extends DialogFragment {
    private DeleteDialogListener listener;
    private Note mNote;

    // Default constructor
    public DeleteDialog() {
    }

    // Constructor with a Note object to delete
    public DeleteDialog(Note note) {
        mNote = note;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.delete_dialog, null);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        // Set the Dialog window background and gravity
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(R.drawable.rounded_background);

        // Set views and listeners
        Button cancelButton = view.findViewById(R.id.cancel_button_delete_dialog);
        Button doneButton = view.findViewById(R.id.delete_button_delete_dialog);

        cancelButton.setOnClickListener(v -> dismiss());
        doneButton.setOnClickListener(v -> {
            listener.deleteClicked(mNote);
            dismiss();
        });
        setRetainInstance(true);
        return alertDialog;
    }

    /**
     * Attach the {@link #listener} to the activity instantiating this Dialog. Throw
     * ClassCastException error if an activity does not implement the {@link DeleteDialogListener}
     * listener.
     *
     * @param context The activity that instantiates this Dialog
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DeleteDialogListener");
        }
    }

    /**
     * Listener interface to get the Note object to delete when the Positive Button is clicked
     */
    public interface DeleteDialogListener {
        void deleteClicked(Note note);
    }
}
