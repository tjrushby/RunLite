package com.tjrushby.runlite.presenters;


import com.tjrushby.runlite.contracts.DetailsContract;
import com.tjrushby.runlite.contracts.RunContract;
import com.tjrushby.runlite.util.StringFormatter;

import javax.inject.Inject;

public class DetailsPresenter implements DetailsContract.Presenter {
    private DetailsContract.Activity view;
    private RunContract.Model model;
    private StringFormatter formatter;

    @Inject
    public DetailsPresenter(DetailsContract.Activity view,
                            RunContract.Model model,
                            StringFormatter formatter) {
        this.view = view;
        this.model = model;
        this.formatter = formatter;
    }

    @Override
    public void onViewCreated() {
        double averagePace = model.getTimeElapsed() / model.getDistanceTravelled();

        view.updateTextViews(
                formatter.longToMinutesSeconds(model.getTimeElapsed()),
                formatter.doubleToDistanceString(model.getDistanceTravelled()),
                formatter.longToMinutesSeconds((long) averagePace)
        );
    }

    @Override
    public void onButtonDoneClicked() {
        // todo persistence here

        view.endActivity();
    }

    @Override
    public void onButtonDeleteClicked() {
        // todo remove from database if exists
    }

    @Override
    public void onMapFragmentReady() {
        if (!model.getRunCoordinates().isEmpty()) {
            view.calculateMapBounds(model.getRunCoordinates());
            view.calculateMapPolyline(model.getRunCoordinates());
        }
    }

    @Override
    public void onMapLoaded() {
        if (!model.getRunCoordinates().isEmpty()) {
            view.addMapMarkers(model.getRunCoordinates());
            view.moveMapCamera();
        }
    }
}
