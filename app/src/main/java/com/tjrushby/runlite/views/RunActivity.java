package com.tjrushby.runlite.views;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tjrushby.runlite.App;
import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.modules.RunActivityContextModule;
import com.tjrushby.runlite.injection.modules.RunActivityModule;
import com.tjrushby.runlite.util.SeekBarAnimation;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RunActivity extends AppCompatActivity implements RunContract.Activity {
    public static final int REQUEST_HIGH_ACCURACY_GPS = 254;

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
    @BindView(R.id.ivLock)
    protected AppCompatImageView ivLock;
    @BindView(R.id.ivUnlock)
    protected AppCompatImageView ivUnlock;

    @BindView(R.id.sbLock)
    protected AppCompatSeekBar sbLock;

    @BindView(R.id.buttonPause)
    protected Button buttonPause;
    @BindView(R.id.buttonStart)
    protected Button buttonStart;
    @BindView(R.id.buttonStop)
    protected Button buttonStop;

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

        presenter.onViewCreated();

        // check the Activity has the correct permissions, request them if not
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Activity doesn't have the required permissions, request them
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            presenter.havePermissions();
        }

        sbLock.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.onSeekBarChanged();
            }
        });

        handler = new Handler();
        tick = () -> presenter.onTick();
    }

    @Override
    protected void onDestroy() {
        stopService(intentRunService);
        super.onDestroy();
    }

    // callback method for ActivityCompat.requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.havePermissions();
                } else {
                    // permissions were not granted, end the Activity
                    this.finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_HIGH_ACCURACY_GPS) {
            switch(resultCode) {
                case Activity.RESULT_CANCELED :
                    presenter.enableHighAccuracyDialogNo();
            }
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    /* OnClick methods for Button objects */

    @OnClick(R.id.buttonStart)
    public void buttonStartClicked() {
        presenter.onButtonStartPressed();
    }

    @OnClick(R.id.buttonPause)
    public void buttonPauseClicked() {
        presenter.onButtonPausePressed();
    }

    @OnClick(R.id.buttonStop)
    public void buttonStopClicked() {
        presenter.onButtonStopPressed();
    }

    /* end OnClick methods */

    @Override
    public void startService() {
        Timber.d("Calling startService()");
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
    public void displayGPSModeToast() {
        Toast.makeText(this, R.string.toast_gps_mode, Toast.LENGTH_LONG).show();
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
                .setPositiveButton("Yes", (dialog, which) -> presenter.endRunAlertDialogYes())
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

    @Override
    public void disableSeekBar() {
        sbLock.setEnabled(false);
    }

    @Override
    public void enableSeekBar() {
        sbLock.setEnabled(true);
    }

    @Override
    public int getSeekBarProgress() {
        return sbLock.getProgress();
    }

    @Override
    public void setSeekBarProgress(int newProgress) {
        // todo need to increase the size of the thumb, it's hard to grab on screen,
        // todo would be a nightmare whilst running
        SeekBarAnimation animation = new SeekBarAnimation(sbLock, sbLock.getProgress(), newProgress);
        animation.setDuration(75);
        sbLock.startAnimation(animation);
    }

    @Override
    public void fadeIconLock() {
        ImageViewCompat.setImageTintList(ivLock,
                ColorStateList.valueOf(ContextCompat.getColor(
                        this,
                        R.color.common_google_signin_btn_text_light_disabled)
                )
        );
    }

    @Override
    public void tintIconLock() {
        ImageViewCompat.setImageTintList(ivLock,
                ColorStateList.valueOf(ContextCompat.getColor(
                        this,
                        R.color.colorAccent)
                )
        );
    }

    @Override
    public void fadeIconUnlock() {
        ImageViewCompat.setImageTintList(ivUnlock,
                ColorStateList.valueOf(ContextCompat.getColor(
                        this,
                        R.color.common_google_signin_btn_text_light_disabled)
                )
        );
    }

    @Override
    public void tintIconUnlock() {
        ImageViewCompat.setImageTintList(ivUnlock,
                ColorStateList.valueOf(ContextCompat.getColor(
                        this,
                        R.color.colorAccent)
                )
        );
    }

    /* methods for changing the state and properties of Button elements */

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
