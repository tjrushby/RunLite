package com.tjrushby.runlite.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.tjrushby.runlite.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TimePickerDialog extends DialogFragment {
    @BindView(R.id.numberPickerHours)
    protected NumberPicker numberPickerHours;
    @BindView(R.id.numberPickerMinutes)
    protected NumberPicker numberPickerMinutes;
    @BindView(R.id.numberPickerSeconds)
    protected NumberPicker numberPickerSeconds;

    private AlertDialog.Builder builder;
    private TimePickerDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_number_picker, null);
        ButterKnife.bind(this, view);

        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(3);

        numberPickerMinutes.setFormatter((int i) -> String.format("%02d", i));
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);

        numberPickerSeconds.setFormatter((int i) -> String.format("%02d", i));
        numberPickerSeconds.setMinValue(0);
        numberPickerSeconds.setMaxValue(59);

        String timeElapsed = getArguments().getString("timeElapsed");
        String[] timeSplit = timeElapsed.split(":");

        if(timeSplit.length == 3) {
            // timeElapsed is at least an hour, need to set all picker fields
            numberPickerHours.setValue(Integer.parseInt(timeSplit[0]));
            numberPickerMinutes.setValue(Integer.parseInt(timeSplit[1]));
            numberPickerSeconds.setValue(Integer.parseInt(timeSplit[2]));
        } else {
            Timber.d("else");
            Timber.d("timeSplit[0]: " + timeSplit[0]);
            Timber.d("timeSplit[1]: " + timeSplit[1]);
            // timeElapsed is under an hour, only need to set minutes and seconds pickers
            numberPickerMinutes.setValue(Integer.parseInt(timeSplit[0]));
            numberPickerSeconds.setValue(Integer.parseInt(timeSplit[1]));
        }

        builder.setView(view);
        builder.setTitle(R.string.dialog_number_picker_title);
        builder.setMessage(R.string.dialog_number_picker_message);

        builder.setPositiveButton(
                R.string.action_done,
                (dialogInterface, i) -> {
                    int seconds = numberPickerSeconds.getValue();
                    int minutes = numberPickerMinutes.getValue();
                    int hours = numberPickerHours.getValue();

                    listener.onTimePickerDialogPositiveClick(
                            (hours * 60 * 60) + (minutes * 60) + seconds);
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
