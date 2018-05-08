package com.example.lightway;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = GMapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Stockholm, Sweden) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(59.334591, 18.063240);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    // Used for Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button logOutButton;

    //Used to draw out the navigational line
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gMap);
        mapFragment.getMapAsync(this);

        logOutButton = findViewById(R.id.log_out);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(GMapsActivity.this, LoginActivity.class));
                }
            }
        };


    }

    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }


    private void logout(){
        //Firebase sign out
        mAuth.signOut();
        //Facebook sign out
        LoginManager.getInstance().logOut();
        // Google sign out
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.gMap), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        // Listen for clicks on any marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                calcTrip(m);        // When a marker is clicked, call the method to calculate the trip to it from the phones position
                return true;
            }
        });
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     * Call this whenever you need to update the device location
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "För att kunna utnyttja appen till fullo behöver du tillåta att den använder din GPS",
                        Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }




    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                mLocationPermissionGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                //getLocationPermission(); This is commented, since we don't want to fall into the "ask for permissions" loop. If the user says no we don't want to spam the question and crash the app
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void airStationsAPIActivity(View view) {
        new Thread(new Runnable() {
            public void run() {
                getDeviceLocation();        //Update the location in it's own thread (for better performance) to make sure we're starting from the correct spot
            }
        }).start();

        Intent intent = new Intent(this, AirStationsAPIActivity.class);
        startActivityForResult(intent, 0);      //Create a "startActivityForResult to be able to get the coordinates back to this activity from AirStationsAPIActivity. See: https://stackoverflow.com/questions/1124548/how-to-pass-the-values-from-one-activity-to-previous-activity
    }

    private void calcTrip(Marker destination) {     // This takes the destination marker (the one previously clicked) as input
        GeoApiContext geoApiContext = new GeoApiContext();
        Date date = Calendar.getInstance().getTime();       //Get the current time so we can display how long the ride will take
        DateTime now = new DateTime(date.getTime());

        try {
            geoApiContext = geoApiContext.setQueryRateLimit(3)      //Set everything needed for the API connection, the key should be moved to the strings
                    .setApiKey(getString(R.string.directionsApiKey))
                    .setConnectTimeout(1, TimeUnit.SECONDS)
                    .setReadTimeout(1, TimeUnit.SECONDS)
                    .setWriteTimeout(1, TimeUnit.SECONDS);

            String origin = "" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();        //Get the start-location so we now from where the polygon should draw
            String destinationString = "" + destination.getPosition().latitude + "," + destination.getPosition().longitude;

            DirectionsResult result = DirectionsApi.newRequest(geoApiContext)       //Get information from the API about the trip
                    .mode(TravelMode.BICYCLING).origin(origin)
                    .destination(destinationString)
                    .departureTime(now)
                    .await();

            destination.setSnippet(getEndLocationSnippet(result));        //Get time and distance and add it to the InfoWindow snippet
            destination.setTitle(result.routes[0].legs[0].endAddress);      //Get the adress and add it to the InfoWindow snippet
            addPolyline(result, mMap);      //Draw the navigational line
            destination.showInfoWindow();       //Display the InfoWindow snippet

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAllMarkersToMap(ArrayList<String> inputCoords) {        //Take an arraylist of strings as input. The strings are the LatLong coordinates in the following format: "Latitude,Longitude"
        try {
            for (int x = 0; x<inputCoords.size(); x++) {        //For loop since we need to go through all coordinates
                double latitude = Double.parseDouble(inputCoords.get(x).split(",")[0]);     //Split the strings into latitude and longitude
                double longitude = Double.parseDouble(inputCoords.get(x).split(",")[1]);

                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));       //Add the marker and its title
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEndLocationSnippet(DirectionsResult results){       //This can be used later on
        return  "Tid: "+ results.routes[0].legs[0].duration.humanReadable + " Sträcka: " + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        if (polyline != null) { polyline.remove(); }        //Remove the previous polyline, if it exists
        polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));     //Add the polyline
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {        //Used to get the ArrayList back from AirStationsAPIActivity
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == AirStationsAPIActivity.RESULT_OK) {
                    ArrayList<String> coordsFromAPI = data.getStringArrayListExtra(AirStationsAPIActivity.PUBLIC_STATIC_STRING_IDENTIFIER);     //Get the ArrayList and then send it to addAllMarkersToMap to draw them
                    addAllMarkersToMap(coordsFromAPI);
                }
                break;
            }
        }
    }

    public void parkingAPIActivity(View view) {
        Intent intent = new Intent(this, ParkingAPIActivity.class);
        startActivity(intent);
    }


}
