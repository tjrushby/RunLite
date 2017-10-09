package com.tjrushby.runlite.contracts;


import android.content.Context;

import com.tjrushby.runlite.adapters.viewholders.ModelViewHolder;

public interface MainContract {
    interface Activity {
        void startRunActivity();

        Context getContext();
    }

    interface Presenter {
        void onFabStartRunPressed();
    }

    interface ListPresenter {
        void onViewHolderBoundAtPosition(ModelViewHolder viewHolder, int position);

        int getRowCount();
    }
}
