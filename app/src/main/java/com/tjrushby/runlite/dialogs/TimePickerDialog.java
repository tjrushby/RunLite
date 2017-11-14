package com.tjrushby.runlite.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.tjrushby.runlite.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimePickerDialog extends DialogFragment {
    @BindView(R.id.numberPickerMinutes)
    protected NumberPicker numberPickerMinutes;
    @BindView(R.id.numberPickerSeconds)
    protected NumberPicker numberPickerSeconds;

    // todo need to inject this somehow, probably via bundle?
    private AlertDialog.Builder builder;
    private TimePickerDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_number_picker, null);

        String timeElapsed = getArguments().getString("timeElapsed");
        String[] timeSplit = timeElapsed.split(":");

        ButterKnife.bind(this, view);
        numberPickerMinutes.setFormatter((int i) -> String.format("%02d", i));
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);
        numberPickerMinutes.setValue(Integer.parseInt(timeSplit[0]));

        numberPickerSeconds.setFormatter((int i) -> String.format("%02d", i));
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setMaxValue(59);
        numberPickerSeconds.setValue(Integer.parseInt(timeSplit[1]));

        builder.setView(view);
        builder.setTitle(R.string.dialog_number_picker_title);
        builder.setMessage(R.string.dialog_number_picker_message);
        builder.setPositiveButton(
                R.string.button_done,
                (dialogInterface, i) -> {
                    int seconds = numberPickerSeconds.getValue();
                    int minutes = numberPickerMinutes.getValue();

                    listener.onTimePickerDialogPositiveClick(minutes * 60 + seconds);
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (TimePickerDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    getActivity().toString() +
                    " must implement TimePickerDialogListener"
            );
        }
    }
}
