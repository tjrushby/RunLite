package com.tjrushby.runlite.contracts;


import android.content.Context;

import com.tjrushby.runlite.adapters.viewholders.ModelViewHolder;
import com.tjrushby.runlite.models.RunWithLatLng;

import java.util.List;

public interface MainContract {
    interface Activity {
        void refreshRecyclerView();

        void startRunActivity();

        Context getContext();
    }

    interface Presenter {
        void onFabStartRunPressed();

        void onActivityResumed();
    }
}
