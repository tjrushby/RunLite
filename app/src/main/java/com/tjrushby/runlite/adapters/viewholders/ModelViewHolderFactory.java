package com.tjrushby.runlite.adapters.viewholders;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface ModelViewHolderFactory {
    RecyclerView.ViewHolder createViewHolder(ViewGroup parent);
}
