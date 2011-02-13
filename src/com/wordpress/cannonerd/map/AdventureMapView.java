package com.wordpress.cannonerd.map;

import com.google.android.maps.MapActivity;
import android.os.Bundle;

public class AdventureMapView extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
