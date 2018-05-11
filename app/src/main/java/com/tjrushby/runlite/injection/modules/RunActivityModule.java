package com.tjrushby.runlite.injection.modules;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.TypedValue;

import com.tjrushby.runlite.R;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.injection.scopes.RunningActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class RunActivityModule {
    private RunContract.Activity activity;

    public RunActivityModule(RunContract.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @RunningActivityScope
    RunContract.Activity providesActivity() {
        return activity;
    }

    @Provides
    Intent providesIntent() {
        return new Intent();
    }

    @Provides
    @RunningActivityScope
    NotificationCompat.Builder providesNotificationCompatBuilder(@Named("activity_context") Context context) {
        return new NotificationCompat.Builder(
                context, context.getString(R.string.notification_channel_id)
        );
    }

    @Provides
    @RunningActivityScope
    NotificationManagerCompat providesNotificationManagerCompat(@Named("activity_context") Context context) {
        return NotificationManagerCompat.from(context);
    }

    @Provides
    @RunningActivityScope
    TypedValue providesTypedValue() {
        return new TypedValue();
    }

    /*NotificationCompat.Action providesNotificationCompatAction() {
        return new NotificationCompat.Action();
    }*/
}
