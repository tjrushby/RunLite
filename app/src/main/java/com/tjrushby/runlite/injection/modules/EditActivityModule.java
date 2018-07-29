package com.tjrushby.runlite.injection.modules;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.tjrushby.runlite.contracts.EditActivityContract;
import com.tjrushby.runlite.dialogs.TimePickerDialog;
import com.tjrushby.runlite.injection.scopes.EditActivityScope;
import com.tjrushby.runlite.presenters.EditPresenter;
import com.tjrushby.runlite.util.StringFormatter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class EditActivityModule {
    private EditActivityContract.View view;
    private Context context;

    public EditActivityModule(EditActivityContract.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Provides
    @EditActivityScope
    EditActivityContract.View providesView() {
        return view;
    }

    @Provides
    @EditActivityScope
    EditActivityContract.Presenter providesPresenter(EditActivityContract.View view,
                                                     StringFormatter formatter) {
        return new EditPresenter(view, formatter);
    }

    @Provides
    @EditActivityScope
    AlertDialog.Builder providesAlertDialogBuilder(@Named("edit_context") Context context) {
        return new AlertDialog.Builder(context);
    }

    @Provides
    Bundle providesBundle() {
        return new Bundle();
    }

    @Provides
    @Named("edit_context")
    Context providesContext() {
        return context;
    }

    @Provides
    Intent providesIntent() {
        return new Intent();
    }

    @Provides
    TimePickerDialog providesTimePickerDialog() {
        return new TimePickerDialog();
    }
}
