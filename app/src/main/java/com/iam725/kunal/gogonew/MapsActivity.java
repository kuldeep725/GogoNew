package com.iam725.kunal.gogonew;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

        private static final String TAG = "LocationActivity";
        private static final long INTERVAL = 1000 * 10;             //time in milliseconds
        private static final long FASTEST_INTERVAL = 1000 * 5;
        private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates";
        private static final String INTERMEDIATE = "intermediate" ;
        private final String USER = "user";
        private final String LATITUDE = "latitude";
        private final String LONGITUDE = "longitude";
        private final String VEHICLE = "vehicle";
        private int checkBusSelection = 0;
        private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
        private Context context;
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        boolean canGetLocation = false;
        private String userId;
        RadioButton radiobutton = null;
        RadioButton lastButton = null;
        String BUS;
        String key = null;
        String theKey = null;

        protected GoogleMap mMap;
        protected DatabaseReference mDatabase;
        LocationRequest mLocationRequest;
        GoogleApiClient mGoogleApiClient;
        Location mCurrentLocation = null;
        private FusedLocationProviderClient mFusedLocationClient;
        private LocationCallback mLocationCallback;
        private Boolean mRequestingLocationUpdates;
        TextView distance;
        TextView duration;
        protected LocationManager locationManager;
        private ProgressDialog progressDialog;
        private FloatingActionButton floatingButton = null;
        private int radiobuttonId;
        private RadioButton fixedRadioButton = null;
        private String floatingClickableState = "isFloatingButtonClickable";
        private String radioButtonString = "radioButtonString";
        private String whichBus = "whichBus";
        private DatabaseReference userDatabase;
        private String keyString = "keyString";
        private int radioId;

        protected void createLocationRequest() {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        private void signingOut() {

                Intent i = new Intent(MapsActivity.this, Login.class);
                FirebaseAuth.getInstance().signOut();

                progressDialog.setTitle("Catch App");
                progressDialog.setMessage("Logging Out...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(true);
                new Thread(new Runnable() {
                        public void run() {
                                try {
                                        Thread.sleep(5000);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                                if (progressDialog != null) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                }

                        }
                }).start();
                startActivity(i);
                finish();

        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_maps);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                progressDialog = new ProgressDialog(this);
                floatingButton = (FloatingActionButton) findViewById(R.id.pick_me);
//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//                fab.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
//                        }
//                });

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                distance = (TextView) findViewById(R.id.distance);
                duration = (TextView) findViewById(R.id.time);
                mDatabase = FirebaseDatabase.getInstance().getReference();

                  mRequestingLocationUpdates = false;

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                Log.d(TAG, "onCreate ...............................");

                createLocationRequest();

                //show error dialog if GoolglePlayServices not available
//                if (!isGooglePlayServicesAvailable()) {
//                        finish();
//                }

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                                mCurrentLocation = location;
                                                Log.d(TAG, "@mFusedLocationClient -> mCurrentLocation = " + mCurrentLocation.toString());
                                                onMapReady(mMap);
                                                if (checkBusSelection != 0 && radioId != 0) {
                                                        makeMarkerOnTheLocation();
                                                        showDistanceInBetween();
                                                        radioId = 0;
                                                }
                                        }
                                }
                        });

                mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                                for (Location location : locationResult.getLocations()) {
                                        mCurrentLocation = location;
                                        //Log.d(TAG, "mLocationCallback mCurrentLocation= "+ mCurrentLocation.toString());

                                }
                        }

                };

        }

        @Override
        public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                        super.onBackPressed();
                        /*new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Closing Activity")
                                .setMessage("Are you sure you want to close this activity?")
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener()
                                {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                        }

                                })
                                .setNegativeButton("No", null)
                                .show();*/
                }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.maps, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.refresh) {
                        startLocationUpdates();
                        isInternetOn();
                        if (mMap != null) {
                                onMapReady(mMap);
                        }

                }
                else if (id == R.id.action_settings) {
                        return true;
                }

                return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.normal) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (id == R.id.terrain) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (id == R.id.satellite) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (id == R.id.settings) {

                } else if (id == R.id.about) {

                } else if (id == R.id.help) {

                }
                else if (id == R.id.signOut) {
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Closing Activity")
                                .setMessage("Are you sure you want to log out ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                signingOut();
                                        }

                                })
                                .setNegativeButton("No", null)
                                .show();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Show Zoom buttons
                mMap.getUiSettings().setZoomControlsEnabled(false);
                // Turns traffic layer on
                mMap.setTrafficEnabled(true);
                // Enables indoor maps
                mMap.setIndoorEnabled(false);
                //Turns on 3D buildings
                mMap.setBuildingsEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        /*double mylatitiude = mMap.getMyLocation().getLatitude();
        double mylongitude = mMap.getMyLocation().getLongitude();
        //mylocation = {10.802874, 76.820238}
        LatLng mylocation = new LatLng(mylatitiude, mylongitude);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("MyLocation"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mylocation));*/
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                }
                mMap.setMyLocationEnabled(true);
                String str = "My Location";
                if (null != mCurrentLocation) {

                        Geocoder geocoder = new Geocoder(getApplicationContext());

                        try {
                                List<android.location.Address> addressList = geocoder.getFromLocation(mCurrentLocation.getLatitude(),
                                        mCurrentLocation.getLongitude(), 1);
                                str = addressList.get(0).getLocality() + ",";
                                str += addressList.get(0).getCountryName();
                                Log.d(TAG, "GEOCODER STARTED.");
                        } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "GEOCODER DIDN'T WORK.");
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                .title(str)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));

                }

        }

        public void onRadioButtonClicked(View view) {
                // Is the button now checked?
                boolean checked = ((RadioButton) view).isChecked();
                radiobuttonId = view.getId();

                // Check which radio button was clicked
                switch (view.getId()) {
                        case R.id.bus1:
                                if (checked) {
                                        checkBusSelection = 1;
                                        if (lastButton != null) {
                                                Log.d(TAG, "LASTBUTTON = " + lastButton.toString());
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                lastButton.setBackground(getDrawable(R.color.cast_expanded_controller_ad_container_white_stripe_color));
                                                        }
                                                lastButton.setTextColor(Color.BLACK);
                                        }
                                        radiobutton = (RadioButton) findViewById(R.id.bus1);
                                        Log.d(TAG, "radiobutton @bus1 = "+radiobutton.toString());
                                        radiobutton.setTextColor(Color.parseColor("#08B34A"));

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                                //radiobutton.setBackground(getDrawable(R.drawable.underline));

//                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                                        makeMarkerOnTheLocation();
                                        showDistanceInBetween();
                                        break;
                                }
                        case R.id.bus2:
                                if (checked) {
                                        checkBusSelection = 2;
                                        if (lastButton != null) {
                                                Log.d(TAG, "LASTBUTTON = " + lastButton.toString());
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                lastButton.setBackground(getDrawable(R.color.cast_expanded_controller_ad_container_white_stripe_color));
                                                        }
                                                lastButton.setTextColor(Color.BLACK);
                                        }
                                        radiobutton = (RadioButton) findViewById(R.id.bus2);
                                        radiobutton.setTextColor(Color.parseColor("#08B34A"));
//                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        radiobutton.setBackground(getDrawable(R.drawable.underline));
                                                }

                                        Log.d(TAG, "radiobutton @BUS2 = " + radiobutton.toString());
                                        makeMarkerOnTheLocation();
                                        showDistanceInBetween();
                                        break;
                                }
                        case R.id.bus3:
                                if (checked) {
                                        checkBusSelection = 3;
                                        if (lastButton != null) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        lastButton.setBackground(getDrawable(R.color.cast_expanded_controller_ad_container_white_stripe_color));
                                                }
                                                lastButton.setTextColor(Color.BLACK);
                                        }
                                        radiobutton = (RadioButton) findViewById(R.id.bus3);
                                        radiobutton.setTextColor(Color.parseColor("#08B34A"));
//                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                        makeMarkerOnTheLocation();
                                        showDistanceInBetween();
                                        break;
                                }
                        case R.id.bus4:
                                if (checked) {
                                        checkBusSelection = 4;
                                        if (lastButton != null) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        lastButton.setBackground(getDrawable(R.color.cast_expanded_controller_ad_container_white_stripe_color));
                                                }
                                                lastButton.setTextColor(Color.BLACK);
                                        }
                                        radiobutton = (RadioButton) findViewById(R.id.bus4);
                                        radiobutton.setTextColor(Color.parseColor("#08B34A"));
//                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                        makeMarkerOnTheLocation();
                                        showDistanceInBetween();
                                        break;
                                }
                        case R.id.bus5:
                                if (checked) {
                                        checkBusSelection = 5;
                                        if (lastButton != null) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        lastButton.setBackground(getDrawable(R.color.cast_expanded_controller_ad_container_white_stripe_color));
                                                }
                                                lastButton.setTextColor(Color.BLACK);
                                        }
                                        radiobutton = (RadioButton) findViewById(R.id.bus5);
                                        radiobutton.setTextColor(Color.parseColor("#08B34A"));
//                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                        makeMarkerOnTheLocation();
                                        showDistanceInBetween();
                                        break;
                                }

                }
                lastButton = radiobutton;
        }
/*
        @Override
        public void onBackPressed() {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Activity")
                        .setMessage("Are you sure you want to close this activity?")
                        .setPositiveButton("EXIT", new DialogInterface.OnClickListener()
                        {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                }

                        })
                        .setNegativeButton("No", null)
                        .show();
        }*/

        private void makeMarkerOnTheLocation() {

                String BUS = "b" + checkBusSelection;
                Log.d(TAG, "CHECKBUSSELECTION = " + checkBusSelection);
                Log.d(TAG, "BUS NOW = " + BUS);

                mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userDatabase = mDatabase.child(USER).child(BUS);
                Log.d(TAG, "USERdatabase = " + userDatabase.toString());

                userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                };
                                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                                Log.d(TAG, "Data : " + dataSnapshot.getValue());

                                assert map != null;
                                String latitudeStr = map.get("latitude");
                                String longitudeStr = map.get("longitude");

                                Log.d(TAG, "Latitude = " + latitudeStr);
                                Log.d(TAG, "Longitude = " + longitudeStr);

                                double latitude = Double.parseDouble(latitudeStr);
                                double longitude = Double.parseDouble(longitudeStr);

                                String busName = "BUS " + checkBusSelection;

                                Geocoder geocoder = new Geocoder(getApplicationContext());

                                try {
                                        List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                        String str = addressList.get(0).getLocality() + ",";
                                        str += addressList.get(0).getCountryName();
                                        str += " (" + busName + ")";
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title(str))
                                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                                }
                                catch (IOException e) {
                                        e.printStackTrace();
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title(busName))
                                                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
                                        Log.e(TAG, "GEOCODER DIDN'T WORK.");
                                }


                                Log.d(TAG, "MARKER HAS BEEN MADE TO " + busName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                });
        }

        private void showDistanceInBetween() {

        /*Bundle extras = getIntent().getExtras();
        String userId = extras.getString("email");*/
                //String userId = email;
                //assert userId != null;
                //String[] temp = userId.split("@");
                //userId = temp[0];
                String BUS = "b" + checkBusSelection;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userDatabase = mDatabase.child(USER).child(BUS);

                userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                };
                                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                                Log.d(TAG, "Data : " + dataSnapshot.getValue());
                                Log.d(TAG, "My Location : " + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());

                                assert map != null;
                                String latitudeStr = map.get("latitude");
                                String longitudeStr = map.get("longitude");

                                Log.d(TAG, " Destination Latitude = " + latitudeStr);
                                Log.d(TAG, "Destination Longitude = " + longitudeStr);

                                // https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal
                                // &key=YOUR_API_KEY
                                if (mCurrentLocation != null) {
                                        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                                                + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude()
                                                + "&destination=" + latitudeStr + "," + longitudeStr + "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;
                                        /*String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                                                + "40.81649,-73.907807&destination=40.819585,-73.90177"+ "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;*/
                                         Log.d(TAG, "URL : " + url);
                                        DownloadTask downloadTask = new DownloadTask();

                                        // Start downloading json data from Google Directions API
                                        downloadTask.execute(url);
                                }

                                /*LatLng bus1_location = new LatLng(latitude,  longitude);
                                LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(),  mCurrentLocation.getLongitude());
                                String DIFFERENCE = CalculationByDistance(myLocation, bus1_location);
                                String dist = DIFFERENCE + " Km";*/
                                //distance.setText(dist);
                /*TextView time = (TextView) findViewById(R.id.time);
                String theTime = latitudeStr + ", " +  longitudeStr;
                time.setText(theTime);*/

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });
                //return myClass.getLongitude();
        }

        public String CalculationByDistance(LatLng StartP, LatLng EndP) {
                int Radius = 6371;// radius of earth in Km
                double lat1 = StartP.latitude;
                double lat2 = EndP.latitude;
                double lon1 = StartP.longitude;
                double lon2 = EndP.longitude;
                double dLat = Math.toRadians(lat2 - lat1);
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.asin(Math.sqrt(a));
                //double valueResult = Radius * c;
        /*double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);*/
                return (new DecimalFormat("##.###").format(Radius * c));
        /*double distance = 0;
        Location crntLocation = new Location("crntlocation");
        crntLocation.setLatitude(StartP.latitude);
        crntLocation.setLongitude(StartP.longitude);

        Location newLocation = new Location("newlocation");
        newLocation.setLatitude(EndP.latitude);
        newLocation.setLongitude(EndP.longitude);

        distance = crntLocation.distanceTo(newLocation) / 1000;      // in km
        return distance;*/
        }

        public void pickMe(View view) {

                if (null != mCurrentLocation) {
                        Log.d(TAG, "pickMe fired...");
                        Log.d(TAG, "theKey = " + theKey);
                        String lat = String.valueOf(mCurrentLocation.getLatitude());
                        String lng = String.valueOf(mCurrentLocation.getLongitude());

                        if (checkBusSelection != 0) {

                                BUS = "b" + checkBusSelection;
                                userDatabase = mDatabase.child(VEHICLE).child(BUS);
                                Map<String, String> userData = new HashMap<>();
                                userData.put(LATITUDE, lat);
                                userData.put(LONGITUDE, lng);

                                if (theKey == null)               theKey = userDatabase.push().getKey();

                                Map<String, Map<String, String>> mSendingData = new HashMap<>();
                                mSendingData.put("LOCATION", userData);
                                /*Map<String, Map<String, Map<String, String>>> mFinalData = new HashMap<>();
                                mFinalData.put(INTERMEDIATE, mSendingData);*/
                                userDatabase.child(theKey).setValue(mSendingData);

                                Toast.makeText(MapsActivity.this, "REQUEST SENT", Toast.LENGTH_SHORT).show();
//                                Button button = (Button) findViewById(R.id.pick_me);
                                floatingButton.setClickable(false);
                                floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                        }

                } else {
                        Log.d(TAG, "location is null ...............");
                }

        }

        public void cancel(View view) {

                if (null != mCurrentLocation) {

                        if (checkBusSelection != 0) {
                                Log.d(TAG, "cancel fired...");
                                floatingButton.setClickable(true);
                                floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primeColor)));
                                //String BUS = "b" + checkBusSelection;
                                Log.d(TAG, "theKey in @cancel = " + theKey);
                                Log.d(TAG, "BUS = " + BUS);
                                if(theKey == null) return;
                                DatabaseReference userDatabase = mDatabase.child(VEHICLE).child(BUS).child(theKey).child("LOCATION");
                                //String key = userDatabase.push().getKey();
                                Log.d(TAG, "AWESOME @ =  "+userDatabase.toString() );
                                userDatabase.removeValue();
                                Toast.makeText(MapsActivity.this, "REQUEST ENDED", Toast.LENGTH_SHORT).show();
                                theKey = null;
//                                Button button = (Button) findViewById(R.id.pick_me);

                        }

                } else {
                        Log.d(TAG, "location is null ...............");
                }

        }

        protected void startLocationUpdates() {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                }
        /*PendingResult<Status> pendingResult = FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);*/
                Log.d(TAG, "Location update started ..............: ");
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null  /*Looper*/ );
        }

        @Override
        public void onLocationChanged(Location location) {
                Log.d(TAG, "Firing onLocationChanged..............................................");
                mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                                for (Location location : locationResult.getLocations()) {
                                        // Update UI with location data
                                        // ...
                                        mCurrentLocation = location;
                                        Log.d(TAG, "@onLocationChanged--> mCurrentLocation");
                                }
                        }
                };
        }

        @Override
        public void onStop() {

                super.onStop();
                Log.d(TAG, "onStop fired ..............");

                SharedPreferences prefs = getSharedPreferences("onStop", MODE_PRIVATE);
                SharedPreferences.Editor outState =  prefs.edit();

                fixedRadioButton = (RadioButton) findViewById(radiobuttonId);

                if (userDatabase != null && theKey != null) {
                        outState.putString(keyString, theKey);
                }

                if (!floatingButton.isClickable()) {
                        outState.putBoolean(floatingClickableState, false);
                }
                else {
                        outState.putBoolean(floatingClickableState, true);
                }
                if (radiobutton != null) {
                        outState.putInt(radioButtonString, fixedRadioButton.getId());
                }
                else {
                        outState.putInt(radioButtonString, 0);
                }
                outState.putInt(whichBus, checkBusSelection);
                outState.apply();
                if (mGoogleApiClient != null) {
                        mGoogleApiClient.disconnect();
                        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
                }

        }
        @Override
        public void onStart() {
                super.onStart();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                        Intent i = new Intent(MapsActivity.this, Login.class);
                        startActivity(i);
                        finish();
                }
                isInternetOn();

                Log.d(TAG, "onStart fired ..............");
                mGoogleApiClient.connect();

                SharedPreferences prefs = getSharedPreferences("onStop", MODE_PRIVATE);

                boolean floatingClickable = prefs.getBoolean(floatingClickableState, Boolean.TRUE);
                radioId = prefs.getInt(radioButtonString, 0);
                int bus = prefs.getInt(whichBus, 0);
                theKey = prefs.getString(keyString, null);

                Log.d(TAG, floatingClickableState + " = " + floatingClickable);
                Log.d(TAG, radioButtonString + " = "  + radioId);
                Log.d(TAG, whichBus + " = " +  bus);

                checkBusSelection = bus;
                BUS = "b" + checkBusSelection;
                if (!floatingClickable)  {
                        floatingButton.setClickable(false);
                        floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                }
                if (radioId != 0) {
                        RadioButton button = (RadioButton) findViewById(radioId);
                        Log.d(TAG, "theKey = " + theKey);
                        Log.d(TAG, "radiobutton @onStart = " + button.toString());
                        button.setTextColor(Color.parseColor("#08B34A"));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                button.setBackground(getDrawable(R.drawable.underline));
                        }
                        lastButton = button;
                        Log.d(TAG, "checkbusSelection = " + checkBusSelection);
                }
                if (!checkPermissions()) {
                        requestPermissions();
                }
        }

        private boolean checkPermissions() {
                int permissionState = ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);
                return permissionState == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermissions() {
                boolean shouldProvideRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION);

                // Provide an additional rationale to the user. This would happen if the user denied the
                // request previously, but didn't check the "Don't ask again" checkbox.
                if (shouldProvideRationale) {

                        Log.i(TAG, "Displaying permission rationale to provide additional context.");

                } else {
                        Log.i(TAG, "Requesting permission");
                        // Request permission. It's possible this can be auto answered if device policy
                        // sets the permission in a given state or the user denied the permission
                        // previously and checked "Never ask again".
                        startLocationPermissionRequest();
                }
        }

        private void startLocationPermissionRequest() {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
                Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
                isInternetOn();
                startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

       /* private boolean isGooglePlayServicesAvailable() {
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (ConnectionResult.SUCCESS == status) {
                        return true;
                } else {
                        GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
                        return false;
                }
        }*/

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.d(TAG, "Connection failed: " + connectionResult.toString());
                isInternetOn();
        }

        @Override
        protected void onPause() {
                super.onPause();
                Log.d(TAG, "onPause fired....");
                stopLocationUpdates();
        }


        protected void stopLocationUpdates() {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Log.d(TAG, "Location update stopped .......................");
        }

        @Override
        protected void onResume() {
                super.onResume();
                Log.d(TAG, "onResume fired...");
                if (mRequestingLocationUpdates) {
                        startLocationUpdates();
                        isInternetOn();
                }
        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
                super.onRestoreInstanceState(savedInstanceState);
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {

                outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                        mRequestingLocationUpdates);
                // ...
                super.onSaveInstanceState(outState);

        }

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
                String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                        URL url = new URL(strUrl);
                        //Log.d(TAG, "url received in downloadUrl = "+url);

                        // Creating an http connection to communicate with url
                        urlConnection = (HttpURLConnection) url.openConnection();

                        // Connecting to url
                        urlConnection.connect();

                        // Reading data from url
                        iStream = urlConnection.getInputStream();

                        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                        StringBuilder sb = new StringBuilder();

                        String line;
                        while ((line = br.readLine()) != null) {
                                sb.append(line);
                        }

                        data = sb.toString().trim();

                        br.close();

                } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        Log.e(TAG, "CONNECTION IS TOTALLY FAILED.");
                } finally {
                        assert iStream != null;
                        iStream.close();
                        urlConnection.disconnect();
                }
                return data;
        }

        public boolean isInternetOn() {
                ConnectivityManager connec =
                        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                // Check for network connections
                if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

                        // if connected with internet

                        Toast.makeText(this, " Connected ", Toast.LENGTH_SHORT).show();
                        return true;

                } else if (
                        connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

                        Toast.makeText(this, " Not Connected ", Toast.LENGTH_SHORT).show();
                        return false;
                }
                return  false;
        }

        // Fetches data from url passed
        private class DownloadTask extends AsyncTask<String, Void, String> {

                // Downloading data in non-ui thread
                @Override
                protected String doInBackground(String... url) {

                        // For storing data from web service
                        String data = "";

                        try {
                                // Fetching the data from web service
                                data = downloadUrl(url[0]);
                                if (data != null) {
                                        //Log.d(TAG, "data from downloadUrl = " + data);
                                }
                                else {
                                        //Log.e(TAG, "data from downloadUrl is not working.");
                                }

                        } catch (Exception e) {
                               // Log.d("Background Task", e.toString());
                               // Log.e(TAG, "data from downloadUrl = FAILED.");
                        }
                        return data;
                }

                // Executes in UI thread, after the execution of
                // doInBackground()
                @Override
                protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        ParserTask parserTask = new ParserTask();
                        //if (result != null) {
                              //  Log.d(TAG, "result in Download Task = " + result);
                                // Invokes the thread for parsing the JSON data
                                parserTask.execute(result);
                        //}
                        //else {
                          //      Log.e(TAG, "result is null.");
                        //}

                }
        }

        /**
         * A class to parse the Google Places in JSON format
         */

        private class ParserTask extends AsyncTask<String, Integer, JSONObject> {

                // Parsing the data in non-ui thread
                @Override
                protected JSONObject doInBackground(String... jsonData) {

                        JSONObject jObject;
                        //List<List<HashMap<String, String>>> routes = null;

                        try {
                                jObject = new JSONObject(jsonData[0]);
                               // Log.d(TAG, "jsonData[0] = "+jsonData[0] );
                             //   Log.d(TAG, "jObject.toString() = " + jObject.toString());
                                /*DirectionsJSONParser parser = new DirectionsJSONParser();
                                Log.d(TAG, "parser.toString() = " + parser.toString());
                                Log.d(TAG, "SOMETHING IS HAPPENING");

                                // Starts parsing data
                                routes = parser.parse(jObject);
                                Log.d(TAG, "Executing routes");
                                Log.d(TAG, routes.toString());
                                Log.d(TAG, "routes = " + routes);*/
                                return jObject;
                        } catch (Exception e) {
                                e.printStackTrace();
                              //  Log.e(TAG, "JSONParser class didn't work properly");
                                return null;
                        }

                }

                // Executes in UI thread, after the parsing process
                @Override
                protected void onPostExecute(JSONObject result) {

                        List<List<HashMap<String, String>>> routes;
                        DirectionsJSONParser parser = new DirectionsJSONParser();
                   //     Log.d(TAG, "parser.toString() = " + parser.toString());
                    //    Log.d(TAG, "SOMETHING IS HAPPENING");

                        // Starts parsing data
                        routes = parser.parse(result);
                     //   Log.d(TAG, "Executing routes");
                    //    Log.d(TAG, routes.toString());
                    //    Log.d(TAG, "routes = " + routes);

                        ArrayList<LatLng> points;
                        //PolylineOptions lineOptions = null;
                        //MarkerOptions markerOptions = new MarkerOptions();
                        String thedistance;
                        String theduration;
                     //   Log.d(TAG, "result = " + routes.size());

                        try {
                                if (routes.size() < 1) {
                                        Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                                        return;
                                }
                        } catch (Exception e) {
                  //              Log.e(TAG, "result.size()  is null.");
                        }


                        // Traversing through all the routes
                        for (int i = 0; i < routes.size(); i++) {
                                points = new ArrayList<>();
                                //ineOptions = new PolylineOptions();

                                // Fetching i-th route
                                List<HashMap<String, String>> path = routes.get(i);
                           //     Log.d(TAG, "path = " + path);
                                // Fetching all the points in i-th route
                                for (int j = 0; j < path.size(); j++) {
                                        HashMap<String, String> point = path.get(j);
                                        //Log.d(TAG, "point = " + point);

                                        if (j == 0) {    // Get distance from the list
                                                thedistance = point.get("distance");
                                 //               Log.d(TAG, "DISTANCE = " + thedistance);
                                                distance.setText(thedistance);
                                                continue;
                                        } else if (j == 1) { // Get duration from the list
                                                theduration = point.get("duration");
                                       //         Log.d(TAG, "DURATION = " + theduration);

                                                duration.setText(theduration);
                                                continue;
                                        }

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng position = new LatLng(lat, lng);

                                        points.add(position);
                                }


                        }


                        // Adding all the points in the route to LineOptions
                                /*lineOptions.addAll(points);
                        assert lineOptions != null;
                        lineOptions.width(2);
                                lineOptions.color(Color.RED);*/
                        }



                        //vDistanceDuration.setText("Distance:"+thedistance + ", Duration:"+theduration);

                        // Drawing polyline in the Google Map for the i-th route
                        //mMap.addPolyline(lineOptions);
                }

        @Override
        protected void onDestroy() {

                super.onDestroy();
                Log.d(TAG, "on Destroy fired ....");
                if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                }
        }
}

