package com.tjrushby.runlite.contracts;

public interface EditActivityContract {
    interface View {
        void endActivity();

        void endActivityWithIntent();

        void displayExitAlertDialog();

        void displayTimePickerDialog();

        void displayEditTextDistanceEmptyError();

        void displayEditTextDistanceNoNumbersError();

        void displayEditTextDistanceZeroError();

        void clearEditTextDistanceError();

        void hideActionSave();

        void showActionSave();

        void setEditTextAveragePace(String averagePace);

        String getEditTextDistance();

        void setEditTextDistance(String distance);

        String getEditTextTimeElapsed();

        void setEditTextTimeElapsed(String timeElapsed);
    }

    interface Presenter {
        void onViewCreated(String[] runDetails);

        void onBackPressed();

        void onActionSaveSelected();

        void onEditTextDistanceChanged();

        void onEditTextTimeElapsedClicked();

        void onEditTextTimeElapsedChanged(int timeElapsed);

        void onExitAlertDialogYes();
    }
}