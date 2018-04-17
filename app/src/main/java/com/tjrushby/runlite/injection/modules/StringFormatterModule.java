package com.tjrushby.runlite.injection.modules;

import android.content.Context;

import com.tjrushby.runlite.util.StringFormatter;
import com.tjrushby.runlite.util.StringFormatterImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StringFormatterModule {
    @Singleton
    @Provides
    StringFormatter providesStringFormatter(@Named("app_context") Context context) {
        return new StringFormatterImpl(context);
    }
}
