package com.example.lightway;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.xml.sax.SAXParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GMapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // widget for the Searchbar
    private AutoCompleteTextView mSearchText;


    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    // adapter object for AutocompleteTextView
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Stockholm, Sweden) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(59.327528, 18.071843);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    public static final int AIRSTATION_REQUEST = 2; // Activity code for airstations used in activityResult.

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

    private Button cancelButton;

    //Used to draw out the navigational line
    private Polyline polyline;

    //Used for Userinfo popup
    Dialog myDialog;
    public ImageView testImage;
    public String providerData;
    private Uri imageFromFirebase;
    public static final int GALLERY_REQUEST = 3;

    private double distanceTraveled;
    private double totalEmissionsSaved;
    private String userName;
    private int noOfRides;
    private DatabaseReference mDatabase;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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

        //
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);

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

        myDialog = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {

                    startActivity(new Intent(GMapsActivity.this, LoginActivity.class));
                }
            }
        };

        cancelButton = findViewById(R.id.btnCancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancel();
                cancelButton.setVisibility(View.GONE);
            }
        });



        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadProfileInfo();

        // imageFromFirebase = mAuth.getCurrentUser().getPhotoUrl();  //moved to userpoup for now.
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "goeLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(GMapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "goeLocate: IOExeption: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "gorLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
            address.getAddressLine(0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void logout() {
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

        // Listen for clicks on any InfoWindow
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker m) {
                cancelButton.setVisibility(View.VISIBLE);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();      //Get the user ID
                String[] snippet = m.getSnippet().split("Sträcka:");        //Get the actual distance from the snippet string
                snippet = snippet[1].split(" ");
                final double distanceToAdd = Double.parseDouble(snippet[1]);

                DatabaseReference mDatabase;        //Connect to the Firebase database
                mDatabase = FirebaseDatabase.getInstance().getReference();

                try {
                    mDatabase.child("Users").child(uid).child("distance_traveled").addListenerForSingleValueEvent(      //Connect to the "Distance traveled" child
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    double oldDistance = 0;

                                    if (dataSnapshot.getValue() != null) {
                                        oldDistance = Double.parseDouble(dataSnapshot.getValue().toString());       //If there already is an entry in "distance_traveled", get it
                                    }

                                    setDistanceValue(oldDistance + distanceToAdd);        //Add the previous and new values and upload them
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    mDatabase.child("Users").child(uid).child("no_of_rides").addListenerForSingleValueEvent(        //Connect to the "no_of_rides" child, then the same as above
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int oldNo = 0;

                                    if (dataSnapshot.getValue() != null) {
                                        oldNo = Integer.parseInt(dataSnapshot.getValue().toString());
                                    }
                                    setNoOfRides(oldNo + 1);
                                    loadProfileInfo();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                } catch (Exception e){
                }

                Toast toast = Toast.makeText(getApplicationContext(), "Let the light guide your way!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
    private void setDistanceValue(double newDistance) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).child("distance_traveled").setValue(newDistance);

    }

    private void setNoOfRides(int newNo){
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Users").child(uid).child("no_of_rides").setValue(newNo);
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
            } else {
                Toast.makeText(this, "För att kunna utnyttja appen till fullo behöver du tillåta att den använder din GPS",
                        Toast.LENGTH_LONG).show();
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);

        hideSoftKeyboard();

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
                Toast.makeText(this, "För att kunna utnyttja appen till fullo behöver du tillåta att den använder din GPS",
                        Toast.LENGTH_LONG).show();
            }
            init();
        } catch (SecurityException e) {
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
        startActivityForResult(intent, AIRSTATION_REQUEST);      //Create a "startActivityForResult to be able to get the coordinates back to this activity from AirStationsAPIActivity. See: https://stackoverflow.com/questions/1124548/how-to-pass-the-values-from-one-activity-to-previous-activity
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

        // Rensar kartan på markers från egen destionationssökning innan pumpmarkers placeras ut
        mMap.clear();

        try {
            for (int x = 0; x < inputCoords.size(); x++) {        //For loop since we need to go through all coordinates
                double latitude = Double.parseDouble(inputCoords.get(x).split(",")[0]);     //Split the strings into latitude and longitude
                double longitude = Double.parseDouble(inputCoords.get(x).split(",")[1]);

                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));       //Add the marker and its title
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEndLocationSnippet(DirectionsResult results) {       //This can be used later on
        return "Tid: " + results.routes[0].legs[0].duration.humanReadable + " Sträcka: " + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        if (polyline != null) {
            polyline.remove();
        }        //Remove the previous polyline, if it exists
        polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));     //Add the polyline
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {        //Used to get the ArrayList back from AirStationsAPIActivity TODO: Loading spinner instead of showign new acitivty xml
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (AIRSTATION_REQUEST):
                if (resultCode == AirStationsAPIActivity.RESULT_OK) {
                    ArrayList<String> coordsFromAPI = data.getStringArrayListExtra(AirStationsAPIActivity.PUBLIC_STATIC_STRING_IDENTIFIER);     //Get the ArrayList and then send it to addAllMarkersToMap to draw them
                    addAllMarkersToMap(coordsFromAPI);
                }
                break;
            case (GALLERY_REQUEST):
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData(); //Gets the data from the selected image.
                    changePicWithUri(selectedImage); //Uploads the image to firebase
                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e("TAG", "Selecting picture cancelled");
                }
                break;
        }
    }

    public void parkingAPIActivity(View view) {     //TODO: Break out all the functionality from AirStationsAPIAcitivty and make sure parkingAPI can use it aswell
        Intent intent = new Intent(this, ParkingAPIActivity.class);
        startActivity(intent);
    }


    //Clears the map of the polyline
    private void cancel() {
        if (polyline != null) {     //Remove the previous polyline, if it exists
            polyline.remove();
        }
    }

    public void showUserPopup(View v) {
        //Loads name, picture, distance traveled, number of rides
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Button btnLogout;
                TextView txtclose;
                TextView txtEmissions;
                TextView txtDistance;
                TextView txtNoOfRides;
                TextView txtUserName;

                myDialog.setContentView(R.layout.profile_popup);

                txtclose = myDialog.findViewById(R.id.txtclose);
                txtclose.setText("X");
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                //CONNECT AND UPDATE ALL TEXTFIELDS IN POPUP
                //Emissions
                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);
                totalEmissionsSaved = calculateEmissions(distanceTraveled);
                txtEmissions = myDialog.findViewById(R.id.txtEmissions);
                txtEmissions.setText(df.format(totalEmissionsSaved));

                loadUsername();
                txtUserName = myDialog.findViewById(R.id.txtUserName);
                txtUserName.setText(userName);

                txtDistance = myDialog.findViewById(R.id.txtDistance);
                txtDistance.setText("" + distanceTraveled);

                txtNoOfRides = myDialog.findViewById(R.id.txtNoOfRides);
                txtNoOfRides.setText("" + noOfRides);


                btnLogout = myDialog.findViewById(R.id.btnLogout);
                btnLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logout();
                    }
                });


                //PROFILE PICTURE UPDATE

                imageFromFirebase = mAuth.getCurrentUser().getPhotoUrl();
                testImage = myDialog.findViewById(R.id.profilePic);
                setDisplayProfilePic();

            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    // Gathers the profile picture of either Facebook or Google.
    private String gatherProviderData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // find the Facebook profile and get the user's id
        for (UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                String facebookUserId = profile.getUid();
                return "https://graph.facebook.com/" + facebookUserId + "/picture?height=300";
            }
            //Checks if the provider id matches with "google.com"
            if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                String url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                url = url.replace("/s96-c/", "/s300-c/");

                return url;
            }
        }
        return "http://2.bp.blogspot.com/-HzFJhEY3KtU/Tea7Ku92cpI/AAAAAAAAALw/uBMzwdFi_kA/s400/1.jpg";
    }

    //This method can be used to change the firebase users profile pic with an Uri
    public void changePicWithUri(Uri photo) {
        FirebaseUser user;

        try {
            user = mAuth.getCurrentUser(); //Gets the current user

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photo) //Sets the photo from the picture gathered
                    .build();
            user.updateProfile(profileUpdates) //Updates the profile on firebase
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Tag", "User profile updated.");
                                //Toast.makeText(GMapsActivity.this, "Profile picture has been changed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (NullPointerException e) {
            System.out.print(e);
        }
    }

    private void setDisplayProfilePic() {

        RequestOptions options = new RequestOptions();

        Glide.with(GMapsActivity.this)
                .load(imageFromFirebase)
                .apply(options.centerInside())
                .into(testImage);

    }

    private double calculateEmissions(double distance) {
        double avgEmissionsPerKm = 134.64;
        double totalEmissions = (avgEmissionsPerKm * distance) / 1000;

        return (totalEmissions);
    }

    private void loadProfileInfo() {

        String uid = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(uid).child("distance_traveled").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            distanceTraveled = Double.parseDouble(dataSnapshot.getValue().toString());
                        }
                        Log.d("Distance", "Distance traveled: " + distanceTraveled);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("ERROR", "Error: " + databaseError);
                    }
                });

        mDatabase.child("Users").child(uid).child("no_of_rides").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            noOfRides = Integer.parseInt(dataSnapshot.getValue().toString());
                        }
                        Log.d("NoOFRides", "Number of rides: " + noOfRides);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("ERROR", "Error: " + databaseError);
                    }
                });
    }

    /*public void changeProfilePic(){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_REQUEST);
    }*/

    private void chooseUserDestination (View v){
        //do something
    }


    // Using firebase .getdDisplayName instead of the "name" in the database. So that the name shows up properly for google/facebook users.
    private void loadUsername(){
        userName = mAuth.getCurrentUser().getDisplayName();

    }

    // Hides the soft keyboard after having pressed enter while searching for a destination
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(mSearchText.getWindowToken(), 0);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager immm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            immm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    /*
        ------------------- google places API autocomplete suggestions ----------------------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id: " + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteURI(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

//            nedan ifall vi får nullpointers
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());
            moveCamera(mPlace.getLatlng(), DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };

   // public void setPadding(View view) {
     //   mMap.setPadding(0, 100, 0, 0);
    //}
//
//
  //  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    View view = inflater.inflate(R.layout.activity_main, null);
//
  //      view.setPadding(0,100,0,0);
//
//        return view;
  //  }

//    UIEdgeInsets Padding = mapView_.padding;

  //  mapView_.padding = UIEdgeInsetsMake(0, 0, kOverlayHeight, 0);
    //mapView_.padding = UIEdgeInsetsZero;

}



