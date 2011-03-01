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
import android.app.Dialog;
import android.widget.ImageView;

// Map overlays
import android.graphics.drawable.Drawable;
import com.google.android.maps.OverlayItem;

// Location
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.wordpress.cannonerd.Point;
import com.wordpress.cannonerd.Geohash;

import android.os.Bundle;
import android.util.Log;
//settings
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import android.content.SharedPreferences;
/**
 * Adventure Map View, the activities of drawing the map
 * 
 *
 * @param   UserLocation, Geohash, 
 * 
 * @return Map with player and Geohash markers. Updates on bearing and distance textboxes
 */

public class AdventureMapView extends MapActivity 
    {

    MapView mapView;
    TextView destinationText;

    private AdventureItemizedOverlay playerOverlay;
    private OverlayItem playerMarker;

    private String mode = "geohash";
    private Point userLocation;
    private Point geohashLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private MapController mapController;

    private static final String TAG = "AdventureTablet";

    private void CenterLocation(Point centerPoint) 
    {
        mapController.animateTo(centerPoint);
    }

    public void onDestinationClicked() 
    {
        // Center the map to geohash
        mode = "geohash";

        if (geohashLocation != null) 
        {
            CenterLocation(geohashLocation);
        }
    }
    
    public void onHomeClicked()
    {
        // Center the map to player
        mode = "home";

        if (userLocation != null) 
        {
            CenterLocation(userLocation);
        }
    }

    public void onSettingsClicked()
    {      
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Give your Qaiku api-key");
        final EditText input = new EditText(this);
        input.setText(loadApiKey());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
                String value = input.getText().toString().trim();
                if (saveApiKey(value)) {
                    Toast.makeText(getApplicationContext(), "Your Qaiku api-key is valid",
                        Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Your Qaiku api-key is invalid",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                        dialog.cancel();
                    }
                });
        alert.show();

    }


    public void onAboutClicked()
    {
        // the objective of the game .txt
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);
        dialog.setTitle("Adventure Tablet");

        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Adventure Tablet is a social adventure game for the real world. The main objective is to get out there and find new places. By taking part in Geohashes you can spike your life with a healthy dose of randomness.");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.icon);
        dialog.show();
    }

    private void updateDestinationText() 
    {
        if (geohashLocation == null) 
        {
            return;
        }

        int bearing = (int) userLocation.bearingTo(geohashLocation);
        int distance = (int) userLocation.distanceTo(geohashLocation);
        destinationText.setText("You are in " +userLocation.PrettyPrint()+ "\nDestination is " + distance + " km from you, at " + bearing + "\u00b0");
    }

    private void prepareLocationServices() 
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new AdventureLocationListener();

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0,
            locationListener
        );

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0,
            locationListener
        );

        // Start with user's remembered location
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) 
        {
            userLocation = new Point(
                lastLocation.getLatitude(),
                lastLocation.getLongitude()
            );
            Log.d(TAG, "Initialized with user location " + userLocation.PrettyPrint());
        }
    }
    public boolean saveApiKey(String apikey)
    {
        QaikuActions qaiku = new QaikuActions(apikey);
        boolean keyOk = qaiku.checkApiKey();
        if (!keyOk) {
            return false;
        }

        SharedPreferences settings = getSharedPreferences("Qaiku", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("apikey", apikey);
        editor.commit();
        return true;
    }

    public String loadApiKey()
    {
       SharedPreferences settings = getSharedPreferences("Qaiku", 0);
       String apikey = settings.getString("apikey", "");  
       return apikey;
    }
    private void updateGeohash() 
    {
        if (userLocation == null) 
        {
            return;
        }

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
            OverlayItem destinationMarker = new OverlayItem(geohashLocation, "Today's Geohash", geohashLocation.PrettyPrint());
            Drawable iconFlag = this.getResources().getDrawable(R.drawable.lippu);
            int w = iconFlag.getIntrinsicWidth();
            int h = iconFlag.getIntrinsicHeight();
            iconFlag.setBounds(-w / 2, -h, w / 2, 0);
            destinationMarker.setMarker(iconFlag);
            playerOverlay.addItem(destinationMarker);
        }
        catch (Exception e)
        {
            Log.d(TAG, "Failed to get GeoHash", e);
        }
    }

    private void updatePlayerOnMap() 
    {
        if (userLocation == null) 
        {
            // No user location, skip placing on map
            Log.d(TAG, "Skipping player update, no location");
            return;
        }

        if (playerOverlay == null) 
        {
            // No overlay set, define
            Log.d(TAG, "Defining map overlay");
            Drawable drawable = this.getResources().getDrawable(R.drawable.red); 
    		playerOverlay = new AdventureItemizedOverlay(drawable);
    		mapView.getOverlays().add(playerOverlay);
        }

        if (playerMarker != null) 
        {
            Log.d(TAG, "Removing player marker");
            playerOverlay.removeItem(playerMarker);
        }

        playerMarker = new OverlayItem(userLocation, "You", "You are here");
        playerOverlay.addItem(playerMarker);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        destinationText = (TextView) findViewById(R.id.destinationtext);

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();

        prepareLocationServices();

        if (userLocation != null) 
        {
            updatePlayerOnMap();

            CenterLocation(userLocation);

            updateGeohash();
        }
    }

    private class AdventureLocationListener implements LocationListener 
    {

        public void onLocationChanged(Location argLocation) 
        {
            Point myPoint = new Point(
                argLocation.getLatitude(),
                argLocation.getLongitude()
            );

            userLocation = myPoint;
            updatePlayerOnMap();

            if (mode == "home") 
            {
                CenterLocation(userLocation);
            }
            else
            {
                updateGeohash();
            }

            updateDestinationText();
        }

        public void onProviderDisabled(String provider) 
        {
            // abstract. Method has to be here
        }

        public void onProviderEnabled(String provider) 
        {
            // abstract. Method has to be here
        }

        public void onStatusChanged(String provider,
        int status, Bundle extras) 
        {
            // abstract. Method has to be here
        }
    }

    @Override
    protected boolean isRouteDisplayed() 
    {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle item selection
        switch (item.getItemId()) 
        {
            case R.id.destination:
                onDestinationClicked();
                return true;
            case R.id.home:
                onHomeClicked();
                return true;
            case R.id.about:
                onAboutClicked();
                return true;
            case R.id.settings:
                onSettingsClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
