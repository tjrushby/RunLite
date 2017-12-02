package com.tjrushby.runlite.contracts;


import android.content.Context;

import com.tjrushby.runlite.adapters.viewholders.ModelViewHolder;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;

public interface MainContract {
    interface Activity {
        void displayProgressBar(boolean display);

        void refreshRecyclerView();

        void startRunActivity();

        Context getContext();
    }

    interface Presenter {
        void onActivityCreated();

        void onActivityResumed();

        void onFabStartRunPressed();
    }
}
