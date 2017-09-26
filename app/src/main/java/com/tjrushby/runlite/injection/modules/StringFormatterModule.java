package com.tjrushby.runlite.injection.modules;

import com.tjrushby.runlite.util.StringFormatter;

import dagger.Module;
import dagger.Provides;

@Module
public class StringFormatterModule {
    @Provides
    StringFormatter providesStringFormatter() {
        return new StringFormatter();
    }
}
