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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.note.R;

/**
 * Dialog that shows the list of sorting options for querying the database for the Note items
 * {@link com.example.note.NoteDao}.
 */
public class SortDialog extends DialogFragment {
    private String mSort, mOrder, mSortTitle, mSortCreated, mDesc;
    private RadioGroup sortByGroup, orderByGroup;
    private SortDialogListener listener;

    // Default constructor
    public SortDialog() {
        mSort = " ";
        mOrder = " ";
        mSortTitle = " ";
        mSortCreated = " ";
        mDesc = " ";
    }

    // Constructor with the sort and order by options to be used to have the appropriate radio
    // buttons selected
    public SortDialog(String mSort, String mOrder) {
        this.mSort = mSort;
        this.mOrder = mOrder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.sort_dialog, null);

        builder.setView(view);

        final AlertDialog alertDialog = builder.create();

        // Set the Dialog window background and gravity
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(R.drawable.rounded_background);

        // Set views and listeners
        sortByGroup = view.findViewById(R.id.sort_by_group_dialog);
        orderByGroup = view.findViewById(R.id.order_by_group_dialog);
        Button cancelButton = view.findViewById(R.id.cancel_button_dialog);
        Button doneButton = view.findViewById(R.id.done_button_dialog);

        // Set up string selection variables
        mSortTitle = getActivity().getResources().getString(R.string.string_title);
        mSortCreated = getActivity().getResources().getString(R.string.string_date_created);
        mDesc = getActivity().getResources().getString(R.string.string_desc);

        setCheckedButtons(view);

        cancelButton.setOnClickListener(v -> dismiss());
        doneButton.setOnClickListener(v -> {
            // Get checked button ids
            RadioButton sortButton = view.findViewById(sortByGroup.getCheckedRadioButtonId());
            RadioButton orderButton = view.findViewById(orderByGroup.getCheckedRadioButtonId());

            listener.doneClicked(sortButton.getText().toString(), orderButton.getText().toString());

            dismiss();
        });
        return alertDialog;
    }

    /**
     * Attach the {@link #listener} to the activity instantiating this Dialog. Throw
     * ClassCastException error if an activity does not implement the {@link SortDialogListener}
     * listener.
     *
     * @param context The activity that instantiates this Dialog
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SortDialogListener");
        }
    }

    /**
     * Sets the appropriate radio buttons to checked status
     */
    private void setCheckedButtons(View view) {
        RadioButton sortButton, orderButton;

        // Set order by button
        if (mOrder.equals(mDesc)) {
            orderButton = view.findViewById(R.id.desc_radio_button_dialog);
        } else {
            orderButton = view.findViewById(R.id.asc_radio_button_dialog);
        }

        // Set sort by button
        if (mSort.equals(mSortTitle)) {
            sortButton = view.findViewById(R.id.title_radio_button_dialog);
        } else if (mSort.equals(mSortCreated)) {
            sortButton = view.findViewById(R.id.created_radio_button_dialog);
        } else {
            sortButton = view.findViewById(R.id.modified_radio_button_dialog);
        }
        orderButton.setChecked(true);
        sortButton.setChecked(true);
    }

    /**
     * Listener interface to get the sort and order by options when the Positive Button is clicked
     */
    public interface SortDialogListener {
        void doneClicked(String text1, String text2);
    }
}
