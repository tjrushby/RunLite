package com.tjrushby.runlite.views;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
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
import com.tjrushby.runlite.services.AudioCueService;
import com.tjrushby.runlite.services.RunService;
import com.tjrushby.runlite.util.SeekBarAnimation;
import com.tjrushby.runlite.util.ThumbOnlySeekBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class RunActivity extends BaseActivity
        implements RunContract.Activity, ThumbOnlySeekBar.OnSeekBarChangeListener {
    public static final int REQUEST_HIGH_ACCURACY_GPS = 254;

    private static final int REQUEST_PERMISSIONS = 255;

    private static final int NOTIFICATION_ID = 666;

    private static final String CHANNEL_ID = "1";

    private final String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET
    };

    private BroadcastReceiver receiver;

    @Inject
    protected Handler handler;
    @Inject
    protected Intent intentRunActivity;
    @Inject
    protected Intent intentAudioCueService;
    @Inject
    protected Intent intentRunService;
    @Inject
    protected Intent intentPause;
    @Inject
    protected Intent intentResume;
    @Inject
    protected Intent intentStop;
    @Inject
    protected NotificationCompat.Builder notifBuilder;
    @Inject
    protected NotificationManagerCompat notifManager;
    @Inject
    protected RunContract.Presenter presenter;
    @Inject
    protected TypedValue typedValue;

    private Runnable tick;

    @BindView(R.id.ivAccuracy)
    protected AppCompatImageView ivAccuracy;
    @BindView(R.id.ivLock)
    protected AppCompatImageView ivLock;
    @BindView(R.id.ivUnlock)
    protected AppCompatImageView ivUnlock;

    @BindView(R.id.sbLock)
    protected ThumbOnlySeekBar sbLock;

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
    @BindView(R.id.tvDistanceUnit)
    protected TextView tvDistanceUnit;
    @BindView(R.id.tvPaceUnit)
    protected TextView tvPaceUnit;

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

        setSupportActionBar(toolbar);

        // set intent properties
        intentRunService.setClass(this, RunService.class);
        intentAudioCueService.setClass(this, AudioCueService.class);

        intentPause.setAction(getString(R.string.notification_action_pause));
        intentResume.setAction(getString(R.string.notification_action_resume));

        intentStop.setClass(this, this.getClass());
        intentStop.putExtra(getString(R.string.notification_action_stop), true);

        // check the Activity has the correct permissions, request them if not
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Activity doesn't have the required permissions, request them
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        } else {
            presenter.havePermissions();
        }

        // set the notification to be ongoing so it can't be dismissed
        notifBuilder.setOngoing(true);

        // set the notification visibility so that all details can be viewed on the lock screen
        notifBuilder.setVisibility(VISIBILITY_PUBLIC);

        // set the click action for the notification
        notifBuilder.setContentIntent(
                PendingIntent.getActivity(
                        this,
                        R.string.notification_default_content_title,
                        intentRunActivity.setClass(this, this.getClass()),
                        0)
        );

        // set the initial action buttons for the notification
        NotificationCompat.Action actionPause = new NotificationCompat.Action(
                0,
                getString(R.string.notification_action_pause),
                PendingIntent.getBroadcast(
                        this,
                        R.string.notification_action_pause,
                        intentPause,
                        PendingIntent.FLAG_UPDATE_CURRENT)
        );

        NotificationCompat.Action actionStop = new NotificationCompat.Action(
                0,
                getString(R.string.notification_action_stop),
                PendingIntent.getActivity(
                        this,
                        R.string.notification_action_stop,
                        intentStop,
                        PendingIntent.FLAG_UPDATE_CURRENT)
        );

        notifBuilder.addAction(actionPause);
        notifBuilder.addAction(actionStop);

        // create notification channel for API 26+
        createNotificationChannel();

        // create a receiver for the 'Pause' and 'Resume' notification actions
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(getString(R.string.notification_action_pause))) {
                    presenter.onButtonPausePressed();
                } else if(intent.getAction()
                        .equals(getString(R.string.notification_action_resume))) {
                    presenter.onButtonStartPressed();
                }
            }
        };

        // register the receiver
        registerReceiver(receiver, new IntentFilter(getString(R.string.notification_action_pause)));
        registerReceiver(receiver, new IntentFilter(getString(R.string.notification_action_resume)));

        sbLock.setOnSeekBarChangeListener(this);

        // define runnable for timer
        tick = () -> presenter.onTick();

        // notify presenter
        presenter.onActivityCreated();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(getString(R.string.notification_action_stop))) {
            presenter.onButtonStopPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onActivityResumed();
    }

    @Override
    protected void onDestroy() {
        stopService(intentRunService);
        stopService(intentAudioCueService);
        unregisterReceiver(receiver);
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
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    presenter.onDialogEnableHighAccuracyNo();
            }
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

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

    @Override
    public void endActivity() {
        this.finish();
    }

    @Override
    public void startDetailsActivity(String runId) {
        startActivity(new Intent(this, DetailsActivity.class).putExtra("runId", runId));
        this.finish();
    }

    @Override
    public void startServices() {
        startService(intentRunService);
        startService(intentAudioCueService);
    }

    @Override
    public void speak(String message) {
        startService(intentAudioCueService.putExtra("message", message));
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
    public void displayNotification() {
        notifBuilder
                .setSmallIcon(R.drawable.ic_run_black_24dp)
                .setContentTitle(getString(R.string.notification_default_content_title))
                .setContentText(getString(R.string.tv_default_time_elapsed))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID);

        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    @Override
    public void removeNotification() {
        notifManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void setNotificationContent(String content) {
        notifBuilder.setContentText(content);
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    @Override
    public void setNotificationContentTitle(String contentTitle) {
        notifBuilder.setContentTitle(contentTitle);
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    @Override
    public void setNotificationActionPause() {
        NotificationCompat.Action actionPause = new NotificationCompat.Action(
                0,
                getString(R.string.notification_action_pause),
                PendingIntent.getBroadcast(
                        this,
                        R.string.notification_action_pause,
                        intentPause,
                        PendingIntent.FLAG_UPDATE_CURRENT)
        );

        notifBuilder.mActions.set(0, actionPause);
    }

    @Override
    public void setNotificationActionResume() {
        NotificationCompat.Action actionResume = new NotificationCompat.Action(
                0,
                getString(R.string.notification_action_resume),
                PendingIntent.getBroadcast(
                        this,
                        R.string.notification_action_resume,
                        intentResume,
                        PendingIntent.FLAG_UPDATE_CURRENT)
        );

        notifBuilder.mActions.set(0, actionResume);
    }

    @Override
    public void displayDialogCancelRun() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("If you go back now you will lose all progress.")
                .setPositiveButton("Yes", (dialog, which) -> presenter.onDialogCancelRunYes())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void displayDialogEndRun() {
        new AlertDialog.Builder(this)
                .setTitle("End Run?")
                .setMessage("Have you finished?")
                .setPositiveButton("Yes", (dialog, which) -> presenter.onDialogEndRunYes())
                .setNegativeButton("No", (dialog, which) -> {
                    presenter.onDialogEndRunNo();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public void displayToastGPSMode() {
        Toast.makeText(this, R.string.toast_gps_mode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayToastRunNotSaved() {
        Toast.makeText(this, R.string.toast_no_save_distance_low, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayToastRunSaved() {
        Toast.makeText(this, R.string.toast_run_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setScreenTimeoutDefault() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void setScreenTimeoutNone() {
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
    public void tintIconGPSAverage() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSAverage)
                ));
    }

    @Override
    public void tintIconGPSBad() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSBad)
                ));
    }

    @Override
    public void tintIconGPSGood() {
        ImageViewCompat.setImageTintList(ivAccuracy,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorGPSGood)
                ));
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
        ImageViewCompat.setImageTintList(ivLock, ColorStateList.valueOf(typedValue.data));
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
        ImageViewCompat.setImageTintList(ivUnlock, ColorStateList.valueOf(typedValue.data));
    }

    @Override
    public boolean isAudioCueEnabled() {
        return sharedPrefs.getBoolean(getString(R.string.pref_audio_cue_enabled_key), true);
    }

    @Override
    public String getAudioCueType() {
        return sharedPrefs.getString(getString(R.string.pref_audio_cue_type_key), "distance");
    }

    @Override
    public double getAudioCueDistanceInterval() {
        return Double.parseDouble(
                sharedPrefs.getString(getString(R.string.pref_audio_cue_interval_distance_key), "1"));
    }

    @Override
    public int getAudioCueTimeInterval() {
        return Integer.parseInt(
                sharedPrefs.getString(getString(R.string.pref_audio_cue_interval_time_key), "1"));
    }

    @Override
    public void setButtonStartText() {
        buttonStart.setText(R.string.button_resume);
    }

    @Override
    public void setColorAccentTypedValue() {
        this.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
    }

    @Override
    public int getGPSAccuracyBadThreshold() {
        return App.GPS_ACCURACY_BAD_THRESHOLD;
    }

    @Override
    public int getGPSAccuracyGoodThreshold() {
        return App.GPS_ACCURACY_GOOD_THRESHOLD;
    }

    @Override
    public int getSeekBarProgress() {
        return sbLock.getProgress();
    }

    @Override
    public void setSeekBarProgress(int newProgress) {
        SeekBarAnimation animation = new SeekBarAnimation(sbLock, sbLock.getProgress(), newProgress);
        animation.setDuration(75);
        sbLock.startAnimation(animation);
    }

    @Override
    public void setTextViewsDistanceUnit(String distanceUnitsString) {
        tvDistanceUnit.setText(distanceUnitsString);
        tvPaceUnit.setText("Mins/" + distanceUnitsString);
    }

    @Override
    public void setTextViewDistance(String distanceTravelled) {
        tvDistance.setText(distanceTravelled);
    }

    @Override
    public void setTextViewAveragePace(String currentPace) {
        tvAveragePace.setText(currentPace);
    }

    @Override
    public void setTextViewPaceDefaultText() {
        tvAveragePace.setText(R.string.tv_default_pace);
    }

    @Override
    public void setTextViewTime(String timeElapsed) {
        tvTime.setText(timeElapsed);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.onSeekBarChanged();
    }

    private void createNotificationChannel() {
        // create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            // IMPORTANCE_LOW so as to have no sound played for the notification
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }
}
