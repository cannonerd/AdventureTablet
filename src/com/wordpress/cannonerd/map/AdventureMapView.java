package com.wordpress.cannonerd.map;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapController;

// Menus
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;

// Other widgets
import android.widget.TextView;

// Location
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.wordpress.cannonerd.Point;
import com.wordpress.cannonerd.Geohash;

import android.os.Bundle;
import android.util.Log;

public class AdventureMapView extends MapActivity {

    MapView mapView;
    TextView destinationText;

    private String mode = "geohash";
    private Point userLocation;
    private Point geohashLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private MapController mapController;

    private static final String TAG = "AdventureTablet";

    private void CenterLocation(Point centerPoint) {
        mapController.animateTo(centerPoint);
    }

    public void onDestinationClicked() {
        // Center the map to geohash
        mode = "geohash";

        if (geohashLocation != null) {
            CenterLocation(geohashLocation);
        }
    }
    
    public void onHomeClicked(){
        // Center the map to player
        mode = "home";
        CenterLocation(userLocation);
    }

    public void onSettingsClicked(){
        //menu for changing icon for player or destination
    }

    public void onAboutClicked(){
        // the objective of the game .txt
    }

    private void updateDestinationText() {
        int bearing = (int) userLocation.bearingTo(geohashLocation);
        int distance = (int) userLocation.distanceTo(geohashLocation);
        destinationText.setText("You are in " +userLocation.PrettyPrint()+ "\nDestination is " + distance + " km from you, at " + bearing + "\u00b0");
    }

    private void prepareLocationServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new AdventureLocationListener();

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0,
            locationListener
        );

        // Start with user's remembered location
        userLocation = new Point(
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()
        );
        Log.d(TAG, "Initialized with user location " + userLocation.PrettyPrint());
    }

    private void updateGeohash() {
        // Calculate geohash
        try
        {
            geohashLocation = Geohash.getGeohash(userLocation);
            Log.d(TAG, "Got geohash at " + geohashLocation.PrettyPrint());

            if (mode == "geohash")
            {
                CenterLocation(geohashLocation);
            }

            updateDestinationText();
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get GeoHash", e);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        destinationText = (TextView) findViewById(R.id.destinationtext);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();

        prepareLocationServices();
        CenterLocation(userLocation);

        updateGeohash();
    }

    private class AdventureLocationListener implements LocationListener {

        public void onLocationChanged(Location argLocation) {
            Point myPoint = new Point(
                argLocation.getLatitude(),
                argLocation.getLongitude()
            );

            userLocation = myPoint;

            if (mode == "home") {
                CenterLocation(userLocation);
            }
            else
            {
                updateGeohash();
            }

            updateDestinationText();
        }

        public void onProviderDisabled(String provider) {
            // abstract. Method has to be here
        }

        public void onProviderEnabled(String provider) {
            // abstract. Method has to be here
        }

        public void onStatusChanged(String provider,
        int status, Bundle extras) {
            // abstract. Method has to be here
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.destination:
                onDestinationClicked();
                return true;
            case R.id.home:
                onHomeClicked();
                return true;
            case R.id.settings:
                onSettingsClicked();
                return true;
            case R.id.about:
                onAboutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
