package com.tjrushby.runlite.presenters;

import com.tjrushby.runlite.contracts.EditContract;
import com.tjrushby.runlite.util.StringFormatter;

import java.math.BigDecimal;

public class EditPresenter implements EditContract.Presenter {
    private boolean changed;
    private double distanceUnits;

    private EditContract.View view;
    private StringFormatter formatter;

    private String runDistance;
    private String runTime;

    public EditPresenter(EditContract.View view, StringFormatter formatter) {
        this.view = view;
        this.formatter = formatter;

        distanceUnits = formatter.getDistanceUnits();
    }

    @Override
    public void onViewCreated(String[] runDetails) {
        if(runDetails != null && runDetails.length == 3) {
            runTime = runDetails[0];
            runDistance = runDetails[1];

            view.setEditTextTimeElapsed(runTime);
            view.setEditTextDistance(runDistance);
            view.setEditTextAveragePace(runDetails[2]);
        } else {
            view.endActivityResultCancelled();
        }
    }

    @Override
    public void onBackPressed() {
        if(changed) {
            view.displayExitAlertDialog();
        } else {
            view.endActivityResultCancelled();
        }
    }

    @Override
    public void onActionSaveSelected() {
        view.endActivityResultOK();
    }

    @Override
    public void onEditTextDistanceChanged() {
        String distanceString = view.getEditTextDistance();

        // validate user input
        if(distanceString.isEmpty()) {
            view.displayEditTextDistanceEmptyError();
            view.hideActionSave();
        } else if(distanceString.equals(".")) {
            view.displayEditTextDistanceNoNumbersError();
            view.hideActionSave();
        } else if(Double.parseDouble(distanceString) == 0) {
            view.displayEditTextDistanceZeroError();
            view.hideActionSave();
        } else {
            // input is valid
            double distanceDouble = Double.parseDouble(distanceString);

            // round input to two decimal places
            BigDecimal roundedDistance = new BigDecimal(distanceDouble)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);

            // if the user input isn't rounded to two decimal places then do so, using string split
            // to check the number of decimal places allows us to remove any extra trailing zeroes
            String[] split = distanceString.split("\\.");

            if(split.length == 2 && split[1].length() > 2) {
                roundedDistance.stripTrailingZeros();
                view.setEditTextDistance(roundedDistance.toString());
            }

            // calculate new average pace value from the values in the TextViews, using rounded distance
            double averagePace = calculateAveragePace(
                    formatter.timeStringToSeconds(view.getEditTextTimeElapsed()),
                    roundedDistance.doubleValue()
            );

            // update TextView for average pace, clear error messages (if any) and enable button for
            // saving changes
            view.setEditTextAveragePace(formatter.averagePaceToTimeStringWithLabel(averagePace));
            view.clearEditTextDistanceError();

            // check if the data in the TextViews is different to initial load
            isDataChanged();
        }
    }

    @Override
    public void onEditTextTimeElapsedClicked() {
        view.displayTimePickerDialog();
    }

    @Override
    public void onEditTextTimeElapsedChanged(int timeElapsed) {
        // calculate average pace using new timeElapsed value and the current value in the EditText
        // for distance
        double averagePace = calculateAveragePace(
                timeElapsed,
                Double.parseDouble(view.getEditTextDistance().toString())
        );

        // update the TextViews for time and average pace
        view.setEditTextAveragePace(formatter.averagePaceToTimeStringWithLabel(averagePace));
        view.setEditTextTimeElapsed(formatter.secondsToTimeString(timeElapsed));

        // check if the data in the TextViews is different to initial load
        isDataChanged();
    }

    @Override
    public void onExitAlertDialogYes() {
        view.endActivityResultCancelled();
    }

    private void isDataChanged() {
        if(!view.getEditTextTimeElapsed().equals(runTime) ||
                Double.parseDouble(view.getEditTextDistance()) != Double.parseDouble(runDistance)) {
            changed = true;
            view.showActionSave();
        } else {
            changed = false;
            view.hideActionSave();
        }
    }

    private double calculateAveragePace(double timeElapsed, double distance) {
        // multiply distance by distanceUnits to ensure it is converted to miles if needed
        return (timeElapsed / (distance * distanceUnits));
    }
}
