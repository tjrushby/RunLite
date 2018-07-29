package com.tjrushby.runlite.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.EditActivityContract;
import com.tjrushby.runlite.dialogs.TimePickerDialog;
import com.tjrushby.runlite.dialogs.TimePickerDialogListener;
import com.tjrushby.runlite.injection.modules.EditActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class EditActivity extends AppCompatActivity
        implements EditActivityContract.View, TimePickerDialogListener {

    @Inject
    protected AlertDialog.Builder builder;
    @Inject
    protected Bundle bundle;
    @Inject
    protected EditActivityContract.Presenter presenter;
    @Inject
    protected Intent intent;
    @Inject
    protected TimePickerDialog timePickerDialog;

    @BindView(R.id.tilDistance)
    protected TextInputLayout tilDistance;
    @BindView(R.id.etDistance)
    protected EditText etDistance;
    @BindView(R.id.etTimeElapsed)
    protected EditText etTimeElapsed;
    @BindView(R.id.etPace)
    protected EditText etPace;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    private Menu menu;
    private String[] runDetails;

    @OnClick(R.id.etTimeElapsed)
    public void editTextTimeElapsedClicked() {
        presenter.onEditTextTimeElapsedClicked();
    }

    @OnTextChanged(R.id.etDistance)
    public void editTextDistanceEdited() {
        presenter.onEditTextDistanceChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        // inject dependencies
        App.getAppComponent()
                .plus(new EditActivityModule(this, this))
                .inject(this);

        toolbar.setTitle(R.string.activity_edit_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if(extras.containsKey("RUN_DETAILS")) {
            runDetails = extras.getStringArray("RUN_DETAILS");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        presenter.onViewCreated(runDetails);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.onBackPressed();
                return true;

            case R.id.action_save:
                presenter.onActionSaveSelected();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void endActivityWithIntent() {
        startActivity(intent
                .setClass(this, DetailsActivity.class)
                .putExtra("UPDATED_DETAILS", new String[] {
                        getEditTextTimeElapsed(),
                        getEditTextDistance(),
                        etPace.getText().toString()
                })
        );

        this.finish();
    }

    @Override
    public void displayExitAlertDialog() {
        builder.setTitle("Go Back?")
                .setMessage("Are you sure? Changes will be discarded.")
                .setPositiveButton("Yes", (dialog, which) -> presenter.onExitAlertDialogYes())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void displayTimePickerDialog() {
        bundle.clear();
        bundle.putString("timeElapsed", etTimeElapsed.getText().toString());

        timePickerDialog.setArguments(bundle);
        timePickerDialog.show(getFragmentManager(), null);
    }

    @Override
    public void displayEditTextDistanceEmptyError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_empty));
    }

    @Override
    public void displayEditTextDistanceNoNumbersError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_no_numbers));
    }

    @Override
    public void displayEditTextDistanceZeroError() {
        tilDistance.setError(getString(R.string.error_edit_text_distance_zero));
    }

    @Override
    public void clearEditTextDistanceError() {
        tilDistance.setError(null);
        tilDistance.setErrorEnabled(false);
    }

    @Override
    public void hideActionSave() {
        menu.findItem(R.id.action_save).setVisible(false);

        // display the regular back arrow as home button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
    }

    @Override
    public void showActionSave() {
        menu.findItem(R.id.action_save).setVisible(true);

        // display a cross as the home button, indicating the user would be discarding changes
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
    }

    @Override
    public void setEditTextAveragePace(String averagePace) {
        etPace.setText(averagePace);
    }

    @Override
    public String getEditTextDistance() {
        return etDistance.getText().toString();
    }

    @Override
    public void setEditTextDistance(String distance) {
        etDistance.setText(distance);
        etDistance.clearFocus();
    }

    @Override
    public String getEditTextTimeElapsed() {
        return etTimeElapsed.getText().toString();
    }

    @Override
    public void setEditTextTimeElapsed(String timeElapsed) {
        etTimeElapsed.setText(timeElapsed);
    }

    @Override
    public void onTimePickerDialogPositiveClick(int timeElapsed) {
        presenter.onEditTextTimeElapsedChanged(timeElapsed);
    }
}
