package com.tjrushby.runlite.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.modules.RunActivityContextModule;
import com.tjrushby.runlite.injection.modules.RunActivityModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RunActivity extends AppCompatActivity implements RunContract.Activity {
    private static final int REQUEST_PERMISSIONS = 255;

    private final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
    };

    @Inject
    public Intent intentRunService;
    @Inject
    public RunContract.Presenter presenter;

    private Handler handler;
    private Runnable tick;

    @BindView(R.id.ivAccuracy)
    protected AppCompatImageView ivAccuracy;
    @BindView(R.id.buttonLock)
    protected Button buttonLock;
    @BindView(R.id.buttonPause)
    protected Button buttonPause;
    @BindView(R.id.buttonStart)
    protected Button buttonStart;
    @BindView(R.id.buttonStop)
    protected Button buttonStop;
    @BindView(R.id.buttonUnlock)
    protected Button buttonUnlock;
    @BindView(R.id.tvDistance)
    protected TextView tvDistance;
    @BindView(R.id.tvAveragePace)
    protected TextView tvAveragePace;
    @BindView(R.id.tvTime)
    protected TextView tvTime;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);

        App.getAppComponent()
                .plus(new RunActivityModule(this), new RunActivityContextModule(this))
                .inject(this);

        Timber.d("view: " + this);
        Timber.d("presenter: " + presenter);

        setSupportActionBar(toolbar);

        handler = new Handler();
        tick = () -> presenter.onTick();

        // check the Activity has the correct permissions, request them if not
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Activity doesn't have the required permissions, request them
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            presenter.havePermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentRunService);
    }

    @Override
    public void onBackPressed() {
        presenter.confirmExit();
    }

    // callback method for ActivityCompat.requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.havePermissions();
                } else {
                    // permissions were not granted, end the Activity
                    this.finish();
                }
        }
    }

    /* OnClick methods for Button objects */

    @OnClick(R.id.buttonLock)
    public void buttonLockClicked() {
        presenter.lockButtons();
    }

    @OnClick(R.id.buttonUnlock)
    public void buttonUnlockClicked() {
        presenter.unlockButtons();
    }

    @OnClick(R.id.buttonStart)
    public void buttonStartClicked() {
        presenter.startRun();
    }

    @OnClick(R.id.buttonPause)
    public void buttonPauseClicked() {
        presenter.pauseRun();
    }

    @OnClick(R.id.buttonStop)
    public void buttonStopClicked() {
        presenter.stopRun();
    }

    /* end OnClick methods */

    @Override
    public void startService() {
        startService(intentRunService);
    }

    @Override
    public void nextTick() {
        handler.postDelayed(tick, 1000);
    }

    @Override
    public void pauseTick() {
        handler.removeCallbacks(tick);
    }

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void endRun(String runId) {
        startActivity(new Intent(this, DetailsActivity.class).putExtra("runId", runId));
        this.finish();
    }

    @Override
    public void displayEndRunAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("End Run?")
                .setMessage("Have you finished?")
                .setPositiveButton("Yes", (dialog, which) -> presenter.endRunAlertDialogYes())
                .setNegativeButton("No", (dialog, which) -> {
                    presenter.endRunAlertDialogNo();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void displayExitAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("If you go back now you will lose all progress.")
                .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void defaultScreenTimeout() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void noScreenTimeout() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /* methods for changing the state and properties of Button elements */

    @Override
    public void hideButtonLock() {
        buttonLock.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showButtonLock() {
        buttonLock.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtonUnlock() {
        buttonUnlock.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showButtonUnlock() {
        buttonUnlock.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtonPause() {
        buttonPause.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showButtonPause() {
        buttonPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideButtonStart() {
        buttonStart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showButtonStart() {
        buttonStart.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateButtonStartText() {
        buttonStart.setText(R.string.button_resume);
    }

    @Override
    public void disableButtonsStartPauseStop() {
        buttonStart.setEnabled(false);
        buttonPause.setEnabled(false);
        buttonStop.setEnabled(false);
    }

    @Override
    public void enableButtonsStartPauseStop() {
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(true);
        buttonStop.setEnabled(true);
    }

    /* methods for changing the tint of the GPS Icon */

    @Override
    public void updateGPSIconAverage() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSAverage)
                ));
    }

    @Override
    public void updateGPSIconBad() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSBad)
                ));
    }

    @Override
    public void updateGPSIconGood() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSGood)
                ));
    }

    /* methods for updating the TextView elements */

    @Override
    public void updateTextViewTime(String timeElapsed) {
        tvTime.setText(timeElapsed);
    }

    @Override
    public void updateTextViewDistance(String distanceTravelled) {
        tvDistance.setText(distanceTravelled);
    }

    @Override
    public void updateTextViewPace(String currentPace) {
        tvAveragePace.setText(currentPace);
    }

    @Override
    public void setTextViewPaceDefaultText() {
        tvAveragePace.setText(R.string.tv_default_pace);
    }
}
