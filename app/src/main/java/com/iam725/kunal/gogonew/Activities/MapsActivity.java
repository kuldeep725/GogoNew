package com.iam725.kunal.gogonew.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.iam725.kunal.gogonew.R;
import com.iam725.kunal.gogonew.Utilities.DirectionsJSONParser;
import com.iam725.kunal.gogonew.Utilities.NetworkChangeReceiver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import com.google.maps.android.SphericalUtil;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

        private static final String TAG = "LocationActivity";
        private static final long INTERVAL = 1000 * 10;             //time in milliseconds (earlier value=1000 * 10)
        private static final long FASTEST_INTERVAL = 1000 * 5;
        private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates";
        private static final String INTERMEDIATE = "intermediate" ;
        private final String ROUTE = "route";
        private final String USER = "user";
        private final String LATITUDE = "latitude";
        private final String LONGITUDE = "longitude";
        private final String VEHICLE = "vehicle";
        private int checkBusSelection = 0;
        private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

        private boolean isWindowReady = false;
        RadioButton radiobutton = null;
        RadioButton lastButton = null;
        String BUS;                     //for pickMe and cancel only ( and few functions linked to them)
        String theKey = null;

        protected GoogleMap mMap;
        protected DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        LocationRequest mLocationRequest;
        GoogleApiClient mGoogleApiClient;
        Location mCurrentLocation = null;
        private FusedLocationProviderClient mFusedLocationClient;
        private LocationCallback mLocationCallback;
        private Boolean mRequestingLocationUpdates;
        TextView distance;
        TextView duration;
        private ProgressDialog progressDialog;
        private FloatingActionButton floatingButton = null;
        private int radiobuttonId;
        private String radioButtonString = "radioButtonString";
        private String whichBus = "whichBus";
        private DatabaseReference userDatabase;
        private String keyString = "keyString";
        private final String LOCATION = "location";
        private long noOfBuses = 0;
        private int flag = 0;
        private int flag2;
        private LatLng mCurrentPosition;
        private double minDistance = Double.MAX_VALUE;
        private LatLng minLocation;
        public static boolean activityVisible; // Variable that will check the
        private LatLng pos = null;
//        public NetworkChangeReceiver broadCast;
        private boolean flagInternet = false;
        private Marker[] markerList;
        private RadioButton pickMeRadioButton = null;
        private int pickMeRadioButtonId;
        private int previousSelection = -1;
        private String userEmail = null;
        private double latitudeBus;
        private double longitudeBus;
        private double minimumDistance;
        private String keyStr;
        private int flagShowDistanceInBetween = 0;
        private int flagOnChild = 0;
//        private String primeColorString = "#08B34A";
        private String primeColorString = "#4286f4";
        private HashMap<LatLng, Marker> markerMap = new HashMap<>();
        private HashMap<LatLng, String> markerNameList = new HashMap<>();
        private Marker minMarker;
        private boolean doubleBackToExitPressedOnce = false;
        private int flagSendOrCancel = 0;               //0 for pickMe , 1 for Cancel
        private String minName;
        private String status = "0";            //status = 0 means notification can be sent
        private double diff;
        private String pickMeBusDistance = "";
        private boolean pickMeDone = false;
        private final String minNameStr = "minName";

        protected void
        createLocationRequest() {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(INTERVAL);
                mLocationRequest.setFastestInterval(INTERVAL);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        private void signingOut() {

                radiobuttonId = 0;
                if(pickMeRadioButton != null) {
                        pickMeRadioButton.setTypeface(Typeface.DEFAULT);
                        pickMeRadioButton.setTextColor(Color.BLACK);
                        pickMeRadioButton = null;
                        if(theKey == null) return;
                        DatabaseReference userDatabase = mDatabase.child(VEHICLE).child(BUS).child(theKey);
                        Log.d(TAG, "AWESOME @ =  "+userDatabase.toString());
                        userDatabase.removeValue();
                        Log.d(TAG, "AWESOME2 @ =  "+userDatabase.toString());
                        Toast.makeText(MapsActivity.this, "REQUEST ENDED", Toast.LENGTH_SHORT).show();
                        theKey = null;
                }
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
                flag2 = 1;
//                broadCast = new NetworkChangeReceiver(new MapsActivity());
                Intent i = new Intent(this, NetworkChangeReceiver.class);
                sendBroadcast(i);

//                registerReceiver(broadCast, new IntentFilter("INTERNET_LOST"));

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

                if (checkPermissions()) {
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
                        //mFusedLocationClient is actually responsible for mCurrentLocation
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                        mCurrentLocation = location;
                                                        Log.d(TAG, "@mFusedLocationClient -> mCurrentLocation = " + mCurrentLocation.toString());
                                                        onMapReady(mMap);
//                                                if (checkBusSelection != 0 && flag2 != 0) {
//                                                        makeMarkerOnTheLocation();
//                                                        showMarkers();
//                                                        showDistanceInBetween();
//                                                        flag2 = 0;
//                                                }
                                                }
                                        }
                                });

                        //brilliant function keeps checking the change in the location and update it in the interval set by us in createLocationRequest
                        mLocationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                        for (Location location : locationResult.getLocations()) {

                                                mCurrentLocation = location;
//                                        Log.d(TAG, "mLocationCallback mCurrentLocation= "+ mCurrentLocation.toString());
                                                showInternetStatus();

                                        }
//                                        showMyLocationMarker();
                                }

                        };
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setupWindowAnimations();
                }

        }

        private void setupWindowAnimations() {
                Slide slide;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        slide = new Slide();
                        slide.setDuration(2000);
                        getWindow().setExitTransition(slide);
                }

        }

        private void createRadioButtons() {

                Log.d(TAG, "createRadioButtons fired... ");
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.group_radio);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.weight = 1.0f;
//                Log.d(TAG, "NO_OF_BUSES = " + noOfBuses);
                if (noOfBuses != 0 && flag == 0) {
                        flag = 1;
                        isWindowReady = true;
                        markerList = new Marker[(int) noOfBuses];               //noOfBuses is of type "long" and we need "int" here
                        for (int i = 0;  i < noOfBuses; i++) {

                                final RadioButton radioButton = new RadioButton(this);
//                                RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button, null);
                                radioButton.setId(i+1);                         //can't write simply setId(i) we need to write setId(i+1)
                                String buttonText = "Bus " + (i+1);

                                radioButton.setText(buttonText);
                                LinearLayout ll = (LinearLayout) findViewById(R.id.locationDetail);
                                float  screen = ll.getWidth();
                                final float scale = getResources().getDisplayMetrics().density;
                                Display display = getWindowManager().getDefaultDisplay();
                                DisplayMetrics outMetrics = new DisplayMetrics ();
                                display.getMetrics(outMetrics);

                                float density  = getResources().getDisplayMetrics().density;
//                                float dpHeight = outMetrics.heightPixels / density;
//                                float density = ((screen - 408.0f)/156.0f);
                                float dpWidth  = outMetrics.widthPixels /
                                        density;
                                Log.e(TAG, "dpWidth = "+dpWidth);
                                screen = dpWidth;
                                int pixels = 0;
                                Log.d(TAG, "screen = "+screen);
//                                screen = (int) ((screen - 0.5f) / scale);
                                float dps;
                                if(noOfBuses <= 5) {
//                                       / dps = 43;

                                         dps = (screen *(1-(float)noOfBuses/6))/(2*(float)noOfBuses)+(13*(screen)/360);
                                        Log.e(TAG, "dps = "+dps);
                                        pixels = (int) (dps * density);
                                }
                                else {
                                        dps = 13*screen/360;
                                        pixels = (int) (dps * scale + 0.5f);                //converting 40 dp into pixels
                                }

                                final float x = 9;
                                int pixels_top = (int) (x*scale + 0.5f);
                                radioButton.setPadding(pixels,pixels_top,pixels,pixels_top);
                                radioButton.setTextSize(18);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        radioButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        radioButton.setBackground(getResources().getDrawable(R.drawable.radio_button_event));
                                }
                                radioButton.setButtonDrawable(R.color.white);
                                if(i >= 3) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                                radioButton.setBackground(getResources().getDrawable(R.color.grayMore));
                                        }
                                }
/*                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        radioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#666666")));
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        radioButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#666666")));
                                }*/
//                                radioGroup.addView(radioButton);
                                radioGroup.addView(radioButton, new RadioGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));

                        }       //for loop ends here
//                        Log.d(TAG, "@noOfBuses is seen");
                        if (checkBusSelection != 0 && flag2 != 0) {
                                makeMarkerOnTheLocation();
                                showMarkers();
                                flagShowDistanceInBetween = 0;                  //for using google distance api in showDistanceInBetween
                                showDistanceInBetween();
                                flag2 = 0;
                        }

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                                        Log.d(TAG, "checkedId = " + checkedId);
                                        radiobutton = (RadioButton) findViewById(checkedId);
                                        if(checkedId == checkBusSelection) {
                                                makeMarkerOnTheLocation();
                                                return;
                                        }
                                        checkBusSelection = checkedId;
                                        radiobuttonId = checkedId;
                                        if (lastButton != null) {
                                                Log.d(TAG, "LASTBUTTON = " + lastButton.toString());
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                        lastButton.setBackground(getResources().getDrawable(R.drawable.radio_button_event));
                                                        lastButton.setPaintFlags(radiobutton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                                        if (lastButton != pickMeRadioButton) {
                                                                lastButton.setTypeface(Typeface.DEFAULT);
                                                        }
                                                }
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                                        lastButton.setBackground(getDrawable(R.color.white));
////                                                        lastButton.setElevation(0);
//                                                }
//                                                else {
//                                                        lastButton.setPaintFlags(radiobutton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
//                                                        lastButton.setTypeface(Typeface.DEFAULT);
//                                                        lastButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                                                }
                                                if (lastButton != pickMeRadioButton) {
                                                        lastButton.setTextColor(Color.BLACK);
                                                }

                                        }
                                        radiobutton.setTextColor(Color.parseColor(primeColorString));
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                                radiobutton.setBackground(getResources().getDrawable(R.drawable.underline));
//                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
//                                                radiobutton.setElevation(10 * getResources().getDisplayMetrics().density);
                                        }
                                        else {
                                                radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                                                radiobutton.setTypeface(Typeface.DEFAULT_BOLD);
                                                radiobutton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        }
                                        radiobutton.setClickable(true);
                                        radiobutton.setChecked(false);

                                        if (mMap != null)      {
                                                mMap.clear();
                                                onMapReady(mMap);
                                        }

                                        makeMarkerOnTheLocation();
                                        showMarkers ();
                                        flagShowDistanceInBetween = 0;                  //for using google distance api in showDistanceInBetween
                                        showDistanceInBetween();
                                        lastButton = radiobutton;
                                }
                        });
                }

        }

        private void showMarkers() {

                if (checkBusSelection != 0) {

                        showInternetStatus();
                        if (!isInternetOn())            return;

//                        Log.d(TAG, "showMarkers IS FIRED...");
                        String BUS = "b"+checkBusSelection;
                        final DatabaseReference routeDatabase = mDatabase.child(USER).child(BUS).child(ROUTE);
                        routeDatabase.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    String bus = "b"+checkBusSelection;
                                    if(!routeDatabase.toString().equals(mDatabase.child(USER).child(bus).child(ROUTE).toString()))        return;

                                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                            };
//                                            Log.d(TAG, "Data : " + dataSnapshot.getValue(genericTypeIndicator));
                                            Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                                            assert map != null;
                                            String latitudeStr = map.get("latitude");
                                            String longitudeStr = map.get("longitude");

//                                            Log.d(TAG, "Latitude = " + latitudeStr);
//                                            Log.d(TAG, "Longitude = " + longitudeStr);

                                            double latitude = Double.parseDouble(latitudeStr);
                                            double longitude = Double.parseDouble(longitudeStr);

                                            String busName = "BUS " + checkBusSelection;

//                                            Geocoder geocoder = new Geocoder(getApplicationContext());

//                                            try {
//                                                    List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
//                                                    String str = "";
//                                                    if (addressList.get(0).getSubLocality() != null) {
//                                                            str = addressList.get(0).getSubLocality()+",";
//                                                    }
//                                                    str += addressList.get(0).getLocality();
//                                                    str += addressList.get(0).getCountryName();
                                                    String str = "";
                                                    str += /*" (" + */dataSnapshot.getKey() /*+ ")"*/;
                                                    LatLng latlng = new LatLng(latitude, longitude);
                                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                                            .position(latlng)
                                                            .title(str));
                                                   marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_stop_10));
                                                   markerMap.put(latlng, marker);
                                                   markerNameList.put(latlng, str);
                        //                         mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
//                                            }
//                                            catch (IOException e) {
//                                                    e.printStackTrace();
//                                                    mMap.addMarker(new MarkerOptions()
//                                                            .position(new LatLng(latitude, longitude))
//                                                            .title(busName))
//                                                            .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_stop_10));
//                        //                          mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
//                                                    Log.e(TAG, "GEOCODER DIDN'T WORK.");
//                                            }

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                        );
                }

        }

        @Override
        public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                                 if (doubleBackToExitPressedOnce) {
                                        super.onBackPressed();
                                        return;
                                }

                                this.doubleBackToExitPressedOnce = true;
//                                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                                Snackbar.make(coordinatorLayout, "Click again to exit", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                                doubleBackToExitPressedOnce=false;
                                        }
                                }, 2000);
                        }
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
                        refresh();
                }
                else if (id == R.id.action_settings) {
                        return true;
                }
                else if (id == R.id.reset) {
                        resetPassword();

                }

                return super.onOptionsItemSelected(item);
        }

        private void resetPassword() {

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Reset Password")
                        .setMessage("Are you sure you want to reset your password ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                        Log.d(TAG, "Email sent.");
                                                                        Toast.makeText(MapsActivity.this, "Email Sent to " + userEmail, Toast.LENGTH_SHORT).show();
                                                                }
                                                        }
                                                });
                                }
                        })
                        .setNegativeButton("No", null)
                        .show();

        }

        private void refresh() {
                Log.d(TAG, "refresh fired...");
                startLocationUpdates();
                showInternetStatus();
                if (mMap != null) {
                        mMap.clear();
                        onMapReady(mMap);
                        if (radiobutton != null) {
                                Log.d(TAG, "radiobutton is Not Null");
//                                radiobutton.setChecked(true);
                                radiobutton.setTextColor(Color.parseColor(primeColorString));

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        radiobutton.setBackground(getDrawable(R.drawable.underline));
//                                        radiobutton.setElevation(10 * getResources().getDisplayMetrics().density);
                                }

                                else {
                                        radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                                                radiobutton.setTypeface(Typeface.DEFAULT_BOLD);
//                                                radiobutton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                }
//                                radiobutton.setClickable(true);
//                                radiobutton.setChecked(false);
                                lastButton = radiobutton;
                                Log.d(TAG, "checkbusSelection = " + checkBusSelection);
                                makeMarkerOnTheLocation();
                                showMarkers ();
                                flagShowDistanceInBetween = 0;                  //for using google distance api in showDistanceInBetween
                                showDistanceInBetween();

                        }
                }
        }

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
                } /*else if (id == R.id.nearestBus) {
                        getNearestBus();
                }*/ else if (id == R.id.settings) {

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

        private void getNearestBus() {

                minimumDistance = Double.MAX_VALUE;
                if (mCurrentLocation == null)           return;

                if (mDatabase != null) {
                        final DatabaseReference userDatabase = mDatabase.child(USER);
                        userDatabase.addChildEventListener(new ChildEventListener() {
                                                                   @Override
                                                                   public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                           keyStr = dataSnapshot.getKey();
                                                                           Log.d(TAG, "keyStr = " + keyStr);
                                                                           Log.d(TAG, "myDataSnapShot = " + dataSnapshot);
                                                                           Log.d(TAG, "dataSnapshot.child(keyStr).child(\"location\").child(\"latitude\") : " + dataSnapshot.child("location").child("latitude"));
                                                                           String latStr = (String) dataSnapshot.child("location").child("latitude").getValue();
                                                                           String lngStr = (String) dataSnapshot.child("location").child("longitude").getValue();
                                                                           Log.d(TAG, "latStr = " + latStr);
                                                                           Log.d(TAG, "lngStr = " + lngStr);
                                                                           Double latdub = Double.parseDouble(latStr);
                                                                           Double lngdub = Double.parseDouble(lngStr);
//                                                                           Double diff = SphericalUtil.computeDistanceBetween(
//                                                                                   new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
//                                                                                   new LatLng(latdub, lngdub));
                                                                           diff = CalculationByDistance(
                                                                                   new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                                                                                   new LatLng(latdub, lngdub)) * 1000;
                                                                           Log.d(TAG, "diff = " + diff);
                                                                           if (diff < minimumDistance) {
                                                                                   minimumDistance = diff;
//                                                                                   minimumLocation = new LatLng(latdub, lngdub);
                                                                                   if (keyStr.contains("b"))               radiobuttonId = Integer.parseInt(keyStr.split("b")[1]);
                                                                           }
                                                                           Log.d(TAG, "radiobuttonId = "+ radiobuttonId);
//                                                                           if (mCurrentLocation != null) {
//                                                                                   String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
//                                                                                           + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude()
//                                                                                           + "&destination=" + latStr + "," + lngStr + "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;
//                                        /*String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
//                                                + "40.81649,-73.907807&destination=40.819585,-73.90177"+ "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;*/
////                                         Log.d(TAG, "URL : " + url);
//                                                                                   DownloadTask downloadTask = new DownloadTask();
////                                        Log.d(TAG, "@busDistance (SphericalUtil.computeDistanceBetween) = " + SphericalUtil.computeDistanceBetween(mCurrentPosition, new LatLng(Double.parseDouble(latitudeStr), Double.parseDouble(longitudeStr))));
//                                                                                   // Start downloading json data from Google Directions API
//                                                                                   downloadTask.execute(url);
//                                                                           }
                                                                   }

                                                                   @Override
                                                                   public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                                   }

                                                                   @Override
                                                                   public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                                   }

                                                                   @Override
                                                                   public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                                   }

                                                                   @Override
                                                                   public void onCancelled(DatabaseError databaseError) {

                                                                   }
                                                           }
                        );
                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                        Log.d(TAG,  "@addListener radioButtonId = " + radiobuttonId);
                                        radiobutton = (RadioButton) findViewById(radiobuttonId);
                                        checkBusSelection = radiobuttonId;
                                        if (lastButton != null) {
                                                Log.d(TAG, "LASTBUTTON = " + lastButton.toString());
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                        lastButton.setBackground(getResources().getDrawable(R.drawable.radio_button_event));
                                                        lastButton.setPaintFlags(radiobutton.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                                        if (lastButton != pickMeRadioButton) {
                                                                lastButton.setTypeface(Typeface.DEFAULT);
                                                        }
                                                }

                                                if (lastButton != pickMeRadioButton) {
                                                        lastButton.setTextColor(Color.BLACK);
                                                }

                                        }
                                        radiobutton.setTextColor(Color.parseColor(primeColorString));

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                        else {
                                                radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                                radiobutton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        }
//                                        radiobutton.setClickable(true);
//                                        radiobutton.setChecked(false);

                                        if (mMap != null)      {
                                                mMap.clear();
                                                onMapReady(mMap);
                                        }

                                        makeMarkerOnTheLocation();
                                        showMarkers ();
//                                        previousSelection = -1;
                                        flagShowDistanceInBetween = 0;                  //for using google distance api in showDistanceInBetween
                                        showDistanceInBetween();
//                                        String dist = distance.getText().toString();
                                        Log.e(TAG, "minDistance = "+ minimumDistance);
                                        long distLong = Math.round(minimumDistance);
                                        String distStr;
                                        if (minimumDistance < 1000) {
                                                distStr = String.valueOf(distLong) + " m";
                                                Log.d(TAG, "distStr = " + distStr);
                                                //                                                long timeLong =Math.round(9 * distLong / 100);          //      40km/hr into m/s
                                        }
                                        else {
                                                distLong = Math.round(distLong/1000);
                                                distStr = String.valueOf(distLong + " km");
                                                Log.d(TAG, "distStr = " + distStr);
                                        }
                                        String str = "The nearest bus is Bus "+ checkBusSelection + " at distance "+ distStr;
                                        Toast.makeText(MapsActivity.this, str, Toast.LENGTH_SHORT).show();
//                                        Log.d(TAG, "minDistStr = " + minDistStr);
//                                        Log.d(TAG, "minimum Time = " + minimumTime);
//                                        distance.setText(minDistStr);
//                                        duration.setText(minimumTime);
                                        lastButton = radiobutton;

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                        });
                }

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "OnMapReady fired");
                showInternetStatus();
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
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                if(checkPermissions()) {
                        mMap.setMyLocationEnabled(true);
                        Log.d(TAG, "it worked");
                }
                else {
                        Log.e(TAG, "setMyLocationEnabled is false");
                }
                if(checkPermissions()) {

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                        pos = marker.getPosition();
//                                Log.e(TAG, "pos ="+pos);
                                        return false;
                                }
                        });
                        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                @Override
                                public void onMapLongClick(LatLng latLng) {
                                        if (pos != null) {
//                                        Log.e(TAG, "Now pos = "+pos);
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
                                                pos = null;
                                        }
                                        else {
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                        }

                                }
                        });

                }

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                }

//                showMyLocationMarker();

                if (null != mCurrentLocation) {
                        mCurrentPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                }
        }

        private void makeMarkerOnTheLocation() {

                String BUS = "b" + checkBusSelection;
//                Log.d(TAG, "CHECKBUSSELECTION = " + checkBusSelection);
//                Log.d(TAG, "BUS NOW = " + BUS);

                mDatabase = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference locationDatabase = mDatabase.child(USER).child(BUS).child(LOCATION);
//                Log.d(TAG, "USERdatabase = " + userDatabase.toString());

                showInternetStatus();
                if (!isInternetOn())    return;

                locationDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                String bus = "b"+checkBusSelection;
                                if(!locationDatabase.toString().equals(mDatabase.child(USER).child(bus).child(LOCATION).toString()))        return;

                                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                };
                                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

//                                Log.d(TAG, "Data : " + dataSnapshot.getValue());
//                                Log.d(TAG, "dataSnapshot.getKey() = " + dataSnapshot.getKey());
//                                Log.d(TAG, "locationDatabase = " + locationDatabase.toString());

                                assert map != null;
                                String latitudeStr = map.get("latitude");
                                String longitudeStr = map.get("longitude");

//                                Log.d(TAG, "Latitude = " + latitudeStr);
//                                Log.d(TAG, "Longitude = " + longitudeStr);

                                double latitude = Double.parseDouble(latitudeStr);
                                double longitude = Double.parseDouble(longitudeStr);
                                LatLng midLocation = null;
                                if(mCurrentLocation != null) {
                                        double midLatitude = (latitude + mCurrentLocation.getLatitude())/2;
                                        double midLongitude = (longitude + mCurrentLocation.getLongitude())/2;
                                        midLocation = new LatLng(midLatitude, midLongitude);
                                }

                                String busName = "BUS " + checkBusSelection;

                                Geocoder geocoder = new Geocoder(getApplicationContext());

                                try {
                                        List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                                        String str = "";
                                        if (addressList.get(0).getSubLocality() != null) {
                                                str = addressList.get(0).getSubLocality()+",";
                                        }
                                        str += addressList.get(0).getLocality();
                                        str += " (" + busName + ")";
//                                        Log.d(TAG, "str = "+str);
                                        if (previousSelection != -1)    {
                                                markerList[previousSelection-1].remove();
                                        }
                                       markerList[checkBusSelection-1] = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title(str));
                                        markerList[checkBusSelection-1].setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp));
                                        if(previousSelection != checkBusSelection) {

                                                if(midLocation != null)
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(midLocation, 11.0f));
                                                else
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
                                        }
                                        previousSelection =  checkBusSelection;
                                }
                                catch (IOException e) {
                                        e.printStackTrace();
                                        if (previousSelection != -1)    {
                                                markerList[previousSelection-1].remove();
                                        }
                                        markerList[checkBusSelection-1] = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title(busName));
                                        markerList[checkBusSelection-1].setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp));
                                        if (previousSelection != checkBusSelection) {

                                                if(midLocation != null)
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(midLocation, 11.0f));
                                                else
                                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
                                        }

                                        Log.e(TAG, "GEOCODER DIDN'T WORK.");
                                        previousSelection  = checkBusSelection;
                                }
//                                Log.d(TAG, "MARKER HAS BEEN MADE TO " + busName);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                });

        }


        private void showDistanceInBetween() {

                Log.e(TAG, "showDistanceInBetween fired...");
                String BUS = "b" + checkBusSelection;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference userDatabase = mDatabase.child(USER).child(BUS).child(LOCATION);

                showInternetStatus();
                if (!isInternetOn())    return;

                userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                String bus = "b"+checkBusSelection;
                                if(!userDatabase.toString().equals(mDatabase.child(USER).child(bus).child(LOCATION).toString()))        return;

                                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                };
                                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

//                                Log.d(TAG, "Data : " + dataSnapshot.getValue());
                                if (mCurrentLocation == null)           return;
//                                Log.d(TAG, "My Location : " + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());

                                assert map != null;
                                String latitudeStr = map.get("latitude");
                                String longitudeStr = map.get("longitude");
                                latitudeBus = Double.parseDouble(latitudeStr);
                                longitudeBus = Double.parseDouble(longitudeStr);
//                                Log.e(TAG, "Now onDataChange fired...");

//                                Log.d(TAG, " Destination Latitude = " + latitudeStr);
//                                Log.d(TAG, "Destination Longitude = " + longitudeStr);


                                /*** Syntax for google direction url page retrieval
                                 * https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal
                                 * &key=YOUR_API_KEY
                                 **/

                               if (flagShowDistanceInBetween == 0) {
                                       if (mCurrentLocation != null) {
                                               flagShowDistanceInBetween = 1;
                                               String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                                                       + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude()
                                                       + "&destination=" + latitudeStr + "," + longitudeStr + "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;
                                                /*String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                                                + "40.81649,-73.907807&destination=40.819585,-73.90177"+ "&key=AIzaSyChXllnUaESuRZPDpSHtb3oyXgL1edHITg";// + R.string.google_direction_api_key;*/
//                                              Log.d(TAG, "URL : " + url);
                                               Log.d(TAG, url);
                                               DownloadTask downloadTask = new DownloadTask();
//                                              Log.d(TAG, "@busDistance (SphericalUtil.computeDistanceBetween) = " + SphericalUtil.computeDistanceBetween(mCurrentPosition, new LatLng(Double.parseDouble(latitudeStr), Double.parseDouble(longitudeStr))));
                                               // Start downloading json data from Google Directions API
                                               downloadTask.execute(url);
                                       }
                               }
                               else {

//                                        double dist = SphericalUtil.computeDistanceBetween(mCurrentPosition,  new LatLng(latitudeBus, longitudeBus));
                                        double  dist = CalculationByDistance(mCurrentPosition, new LatLng(latitudeBus, longitudeBus))*1000;
//                                        Log.d(TAG, "dist = "+ dist);
                                        long distLong = Math.round(dist);
                                        String timeStr;
                                        if (dist < 1000) {
                                                String distStr = String.valueOf(distLong) + " m";
//                                                Log.d(TAG, "distStr = " + distStr);
                                                distance.setText(distStr);
                                                distance.setPaintFlags(distance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //                                                long timeLong =Math.round(9 * distLong / 100);          //      40km/hr into m/s
                                                long timeLong =Math.round(12 * distLong / 100);          //      30km/hr into m/s
                                                if (timeLong > 60)            {
                                                        timeLong = Math.round(timeLong / 60);
                                                        timeStr = String.valueOf(timeLong) + " min";
                                                }
                                                else {
                                                        timeStr = String.valueOf(timeLong) + " s";
                                                }

//                                                Log.d(TAG, "timeStr = " +timeStr);
                                                duration.setText(timeStr);
                                                duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        }
                                        else {
                                                distLong = Math.round(distLong/1000);
                                                String distStr = String.valueOf(distLong + " km");
//                                                Log.d(TAG, "distStr = " + distStr);
                                                distance.setText(distStr);
                                                distance.setPaintFlags(distance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //                                                long timeLong = Math.round(3 * distLong / 2);           //      40km/hr into km/min
                                                long timeLong = Math.round(2 * distLong);           //      30km/hr into km/min
                                                if (timeLong > 60) {
                                                        timeLong = Math.round(timeLong/60);
                                                        timeStr = String.valueOf(timeLong) + " hr";
                                                }
                                                else {
                                                        timeStr = String.valueOf(timeLong) + " min";
                                                }
//                                                Log.d(TAG, "timeStr = " +timeStr);
                                                duration.setText(timeStr);
                                                duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        }
                               }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });
                //return myClass.getLongitude();
        }

        public double CalculationByDistance(LatLng StartP, LatLng EndP) {
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
//                double c = 2000* Math.asin(Math.sqrt(a));
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                return  c * Radius;

        }

        public void pickMe(View view) {

                showInternetStatus();
                if (!isInternetOn()) return;
                if (!isWindowReady) return;

                if (null != mCurrentLocation && checkBusSelection != 0) {

                        Log.d(TAG, "pickMe fired...");
                        Log.d(TAG, "theKey = " + theKey);
                        BUS = "b" + checkBusSelection;
                        pickMeRadioButton = radiobutton;
                        pickMeBusDistance = distance.getText().toString().trim();
                        Log.e(TAG, "BUS (pickme before) = " + BUS);
                        if (pickMeRadioButton != null) {
                                pickMeRadioButton.setTypeface(Typeface.DEFAULT_BOLD);
                                pickMeRadioButton.setTextColor(getResources().getColor(R.color.primeColor));
                        }

                        String lat = String.valueOf(mCurrentLocation.getLatitude());
                        String lng = String.valueOf(mCurrentLocation.getLongitude());

                        findNearestBusStop();
                        Log.d(TAG, "findNearestBusStop has now STOPPED");

//                        Log.e(TAG, "BUS (pickme after) = " + BUS);
                        DatabaseReference routeDatabase = FirebaseDatabase.getInstance().getReference().child(USER).child(BUS).child("route");
                        /*NOTE : addListenerForSingleValueEvent runs only after all the instances of onChildAdded have been run*/
                        DatabaseReference vehicleDatabase = FirebaseDatabase.getInstance().getReference().child(VEHICLE).child(BUS);
//                        routeDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        vehicleDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (minName != null) {
                                                Log.d(TAG, "minName = " + minName);
                                                status = (String) dataSnapshot.child(minName).child("status").getValue();
                                                Log.d(TAG, "status = " + status);

                                                if (checkBusSelection != 0) {

//                                                Log.d(TAG, "WE ARE INSIDE NOW....");
                                                        userDatabase = mDatabase.child(VEHICLE).child(BUS);
                                                        Map<String, String> userData = new HashMap<>();
                                                        if (minLocation == null) {
                                                                Log.d(TAG, "minLocation is NULL :(");
                                                                minLocation = mCurrentPosition;
                                                        }
                                                        minMarker = markerMap.get(minLocation);
                                                        if (minMarker != null) {
                                                                minMarker.remove();
                                                                if (mMap != null) {
                                                                        minMarker = mMap.addMarker(new MarkerOptions()
                                                                                .position(minLocation)
                                                                                .title(markerNameList.get(minLocation)));
                                                                        minMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green_stop_10));
                                                                }
                                                        }
                                                        if (minName == null) {
                                                                minName = "bus stop";
                                                        }
                                                        Log.d(TAG, "minLocation.latitude = " + minLocation.latitude);
                                                        Log.d(TAG, "minLocation.longitude = " + minLocation.longitude);
                                                        Log.d(TAG, "minDistance  = " + minDistance);
                                                        userData.put(LATITUDE, String.valueOf(minLocation.latitude));
                                                        userData.put(LONGITUDE, String.valueOf(minLocation.longitude));

                                                        if (theKey == null)
                                                                theKey = userDatabase.push().getKey();

                                                        if(status == null) {
                                                                status = "0";
                                                                UserRequest userRequest = new UserRequest(
                                                                        theKey,
                                                                        String.valueOf(minLocation.latitude),
                                                                        String.valueOf(minLocation.longitude),
                                                                        status);
                                                                Map<String, String> userRequestMap = userRequest.toMap();
                                                                userDatabase.child(minName).setValue(userRequestMap);
                                                        }

                                                        else {
//                                                                                status = String.valueOf(Integer.parseInt(status)+1);
//                                                                                status = "1";
                                                                userDatabase.child(minName).child(theKey).setValue("0");
                                                        }

                                                        double difference = CalculationByDistance(new LatLng(latitudeBus, longitudeBus), minLocation);
                                                        Log.e(TAG, "difference = " + difference);
                                                        String differenceStr = "Bus " + checkBusSelection + " is at distance " + String.format(Locale.US, "%.2f", difference) + " km"
                                                                + " from " + markerNameList.get(minLocation);
                                                        String toastString = "Request sent for " + markerNameList.get(minLocation);
                                                        Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(MapsActivity.this, differenceStr, Toast.LENGTH_SHORT).show();
                                                        pickMeBusDistance = "";

                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                                        floatingButton.setBackground(getResources().getDrawable(R.drawable.ic_cancel_white_24dp));
                                                                floatingButton.setImageResource(R.drawable.ic_cancel_white_24dp);
//                                                        floatingButton.setBackgroundColor(Color.parseColor("#ff0000"));
                                                                floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.myRed)));
                                                        }
                                                        pickMeDone = true;

                                                        Log.d(TAG, "findNearestBusStop has STOPPED");
                                                } else {
                                                        Log.d(TAG, "location is null ...............");
                                                }


                                        }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                        });

                }
        }

        private void findNearestBusStop() {

                showInternetStatus();
                if (!isInternetOn())            return;

                DatabaseReference routeDatabase = FirebaseDatabase.getInstance().getReference().child(USER).child(BUS).child("route");

                routeDatabase.addChildEventListener(new ChildEventListener() {
                                                            @Override
                                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//                                                                    Log.d(TAG, "onChildAdded findNearestBusStop() fired .. .. ");
                                                                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                                                    };
//                                                                    Log.d(TAG, " dataSnapshot.getValue(genericTypeIndicator) : " + dataSnapshot.getValue(genericTypeIndicator));
//                                                                    Log.d(TAG, "postDataSnapshot.getKey() = " + dataSnapshot.getKey());
                                                                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                                                                    assert map != null;
                                                                    String latitudeStr = map.get("latitude");
                                                                    String longitudeStr = map.get("longitude");

//                                            Log.d(TAG, "Latitude = " + latitudeStr);
//                                            Log.d(TAG, "Longitude = " + longitudeStr);

                                                                    double latitude = Double.parseDouble(latitudeStr);
                                                                    double longitude = Double.parseDouble(longitudeStr);
                                                                    LatLng busStop = new LatLng(latitude, longitude);
//                                        String busName = "BUS " + checkBusSelection;
//                                                                    double diff = SphericalUtil.computeDistanceBetween(mCurrentPosition,  busStop);
                                                                      double  diff = CalculationByDistance(mCurrentPosition, busStop)*1000;
//                                                                      Log.d(TAG, "diff = " + diff);
                                                                    if (diff < minDistance) {
                                                                            minDistance = diff;
                                                                            minLocation = new LatLng(latitude, longitude);
                                                                            minName = dataSnapshot.getKey();
                                                                    }
//                                                                    Log.d(TAG, "minLocation.toString() = " + minLocation.toString());
                                                            }

                                                            @Override
                                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                            }

                                                            @Override
                                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                            }

                                                            @Override
                                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                    }
                );

        }

        public void cancel(View view) {

                showInternetStatus();
                if (!isInternetOn() || !isWindowReady || !pickMeDone)            return;

                if(theKey == null || minName == null)      return;

                if (null != mCurrentLocation) {

                        if (checkBusSelection != 0) {
                                Log.d(TAG, "cancel fired...");

                                if (pickMeRadioButton != null || pickMeRadioButtonId != -1) {
                                        Log.e(TAG, "pickMeRadioButton is not null");
                                        pickMeRadioButton.setTypeface(Typeface.DEFAULT);
                                        if (checkBusSelection == pickMeRadioButton.getId()){
                                                pickMeRadioButton.setTextColor(getResources().getColor(R.color.primeColor));
                                        }
                                        else {
                                                pickMeRadioButton.setTextColor(Color.BLACK);
                                        }
                                        pickMeRadioButton = null;
                                        pickMeRadioButtonId = -1;
                                }
                                if(minMarker != null) {
                                        minMarker.remove();
                                        if(mMap != null) {
                                                minMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(minLocation)
                                                        .title(markerNameList.get(minLocation)));
                                                minMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red_stop_10));
                                        }
                                }
//                                floatingButton.setClickable(true);
//                                floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primeColor)));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                        floatingButton.setBackground(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                                        floatingButton.setImageResource(R.drawable.ic_send_white_24dp);
//                                        floatingButton.setBackgroundColor(getResources().getColor(R.color.primeColor));
                                        floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primeColor)));
                                }
                                //String BUS = "b" + checkBusSelection;
                                Log.d(TAG, "theKey in @cancel = " + theKey);
                                Log.d(TAG, "BUS = " + BUS);

                                final DatabaseReference userDatabase = mDatabase.child(VEHICLE).child(BUS).child(minName).child(theKey);
                                //String key = userDatabase.push().getKey();
                                Log.d(TAG, "AWESOME @ =  "+userDatabase.toString());
//                                userDatabase.removeValue();
                                Log.d(TAG, "AWESOME2 @ =  "+userDatabase.toString());
                                Toast.makeText(MapsActivity.this, "REQUEST ENDED", Toast.LENGTH_SHORT).show();

                               DatabaseReference dr = mDatabase.child(VEHICLE).child(BUS).child(minName);

                                 dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.e(TAG, "dataSnapshot.getChildrenCount() = "+dataSnapshot.getChildrenCount());
                                                if(dataSnapshot.getChildrenCount() == 4) {
                                                        String dataSnapshotStr = dataSnapshot.toString();
                                                        Log.d(TAG, dataSnapshotStr);
                                                        Log.d(TAG, "theKey = " + theKey);
                                                        if(dataSnapshotStr.contains(theKey))
                                                                mDatabase.child(VEHICLE).child(BUS).child(minName).removeValue();
                                                }
                                                else {
                                                        userDatabase.removeValue();
                                                }
                                                theKey = null;
                                                status = null;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                });

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
                        // to handle thease where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                }
                Log.d(TAG, "Location update started ..............: ");
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null  /*Looper*/ );
        }

        @Override
        public void onLocationChanged(Location location) {
                Log.e(TAG, "Firing onLocationChanged..............................................");
                mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                                for (Location location : locationResult.getLocations()) {
                                        // Update UI with location data
                                        // ...
                                        mCurrentLocation = location;
                                        Log.e(TAG, "@onLocationChanged--> mCurrentLocation");
                                }
                        }
                };
        }

        @Override
        public void onStop() {

                super.onStop();
                Log.d(TAG, "onStop fired ..............");

                if (mGoogleApiClient != null) {
                        mGoogleApiClient.disconnect();
                        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
                }

        }

        @Override
        protected void onPause() {
                super.onPause();
                activityVisible = false;
                Log.d(TAG, "onPause fired....");
                SharedPreferences prefs = getSharedPreferences("onStop", MODE_PRIVATE);
                SharedPreferences.Editor outState =  prefs.edit();

                Log.d(TAG, "@stop radiobutton-id = " + radiobuttonId);

                if (userDatabase != null && theKey != null) {
                        Log.d(TAG, "theKey = "+theKey);
                        outState.putString(keyString, theKey);
                }
                outState.putString(minNameStr, minName);
                outState.putInt("flagSendOrCancel", flagSendOrCancel);
                Log.d(TAG, "flagSendOrCancel = " + flagSendOrCancel);
                if (radiobutton != null) {
                        outState.putInt(radioButtonString, radiobuttonId);
                }
                else {
                        outState.putInt(radioButtonString, 0);
                }
                outState.putString("pickMeBus", BUS);
                if (pickMeRadioButton != null) {
                        Log.e(TAG, "pickMeRadioButton = "+pickMeRadioButton.getId());
                        outState.putInt("pickMeRadioButton", pickMeRadioButton.getId());
                }
                else {
                        if(pickMeRadioButtonId != -1) {
                                outState.putInt("pickMeRadioButton", pickMeRadioButtonId);
                        }
                        else {
                                outState.putInt("pickMeRadioButton", -1);
                        }
                }

                outState.putInt(whichBus, checkBusSelection);
                outState.apply();
                if(checkPermissions())                  stopLocationUpdates();
        }

        @Override
        public void onStart() {

                super.onStart();
                Log.d(TAG, "onStart fired ..............");
                if (!checkPermissions()) {
                        requestPermissions();
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser == null) {
                        Intent i = new Intent(MapsActivity.this, Login.class);
                        startActivity(i);
                        finish();
                }
                showInternetStatus();

                SharedPreferences loginPrefs = getSharedPreferences("userId", MODE_PRIVATE);
                userEmail = loginPrefs.getString("email", "User id");
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView tv = (TextView) headerView.findViewById(R.id.user_id);
                tv.setText(userEmail);

                SharedPreferences prefs = getSharedPreferences("onStop", MODE_PRIVATE);
//                boolean floatingClickable = prefs.getBoolean(floatingClickableState, Boolean.TRUE);
                radiobuttonId = prefs.getInt(radioButtonString, 0);
                flagSendOrCancel = prefs.getInt("flagSendOrCancel", 0);
                int bus = prefs.getInt(whichBus, 0);
                theKey = prefs.getString(keyString, null);
                pickMeRadioButtonId = prefs.getInt("pickMeRadioButton", -1);
                minName = prefs.getString(minNameStr, null);
                Log.e(TAG, "onStart-> pickMeRadioButtonId = " + pickMeRadioButtonId);

                Log.d(TAG, "flagSendOrCancel" + " = " + flagSendOrCancel);
                Log.d(TAG, "radiobuttonId" + " = "  + radiobuttonId);
                Log.d(TAG, whichBus + " = " +  bus);
                Log.d(TAG, "theKey = "+ theKey);
                Log.d(TAG, "minName = " + minName);

                checkBusSelection = radiobuttonId;
                BUS = prefs.getString("pickMeBus", null);
                if (BUS == null) {
                        BUS = "b"+ checkBusSelection;
                }
                if(flagSendOrCancel != 0) {
                         floatingButton.setImageResource(R.drawable.ic_cancel_white_24dp);
                        floatingButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.myRed)));
                        pickMeDone = true;
                }

                if(checkPermissions()) {
                        mGoogleApiClient.connect();
                        Log.e(TAG, "mGoogleApiClient is connected");
                }
                else {
                        Log.e(TAG, "mGoogleApiClient is not connected");
                }

                mDatabase.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "dataSnapshot.getKey()  = "+dataSnapshot.getKey() );
                                Log.d(TAG, "dataSnapshot.getChildrenCount() = " + dataSnapshot.getChildrenCount());
                                /*check whether onChildAdded has already run once**/
                                if(flagOnChild == 1)    return;

                                noOfBuses = dataSnapshot.getChildrenCount();
                                Log.d(TAG, "noOfBuses = " + noOfBuses);
//                                Log.d(TAG, "String s = " + s);
                                createRadioButtons();
                                if (radiobuttonId != 0) {
//                                        Log.d(TAG, "MAKING radiobutton setChecked(true)...");
                                        radiobutton = (RadioButton) findViewById(radiobuttonId);
                                        Log.d(TAG, "radiobutton @onStart = " + radiobutton.toString());
                                        radiobutton.setTextColor(Color.parseColor(primeColorString));

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                radiobutton.setBackground(getDrawable(R.drawable.underline));
                                        }
                                        else {
                                                radiobutton.setPaintFlags(radiobutton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                        }
                                        radiobutton.setClickable(true);
                                        radiobutton.setChecked(false);
                                        lastButton = radiobutton;
                                        Log.d(TAG, "checkbusSelection = " + checkBusSelection);

                                }

                                if (pickMeRadioButtonId != -1) {
                                        pickMeRadioButton = (RadioButton) findViewById(pickMeRadioButtonId);
                                        pickMeRadioButton.setTypeface(Typeface.DEFAULT_BOLD);
                                        pickMeRadioButton.setTextColor(getResources().getColor(R.color.primeColor));
                                }
                                flagOnChild = 1;
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });

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
                startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.d(TAG, "Connection failed: " + connectionResult.toString());
//                isInternetOn();
        }

        protected void stopLocationUpdates() {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Log.d(TAG, "Location update stopped .......................");
        }

        @Override
        protected void onResume() {
                super.onResume();
                activityVisible = true;
                Log.d(TAG, "onResume fired...");
                if (mRequestingLocationUpdates) {
                        startLocationUpdates();
                        showInternetStatus();
                }
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
        private String downloadUrl (String strUrl) throws IOException {
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

//                        double dist = SphericalUtil.computeDistanceBetween(mCurrentPosition,  new LatLng(latitudeBus, longitudeBus));
                        double  dist = CalculationByDistance(mCurrentPosition, new LatLng(latitudeBus, longitudeBus))*1000;
                        Log.d(TAG, "dist = "+ dist);
//                                        if (dist < minimumDistance) {
//                                                minimumDistance = dist;
                        long distLong = Math.round(dist);
                        String timeStr;
                        String distStr;
                        if (dist < 1000) {
                                distStr = String.valueOf(distLong) + " m";
                                Log.d(TAG, "distStr = " + distStr);
                                distance.setText(distStr);
                                distance.setPaintFlags(distance.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG);
//                                                long timeLong =Math.round(9 * distLong / 100);          //      40km/hr into m/s
                                long timeLong =Math.round(12 * distLong / 100);          //      30km/hr into m/s
                                if (timeLong > 60)            {
                                        timeLong = Math.round(timeLong / 60);
                                        timeStr = String.valueOf(timeLong) + " min";
                                }
                                else {
                                        timeStr = String.valueOf(timeLong) + " s";
                                }
                                Log.d(TAG, "timeStr = " +timeStr);
                                duration.setText(timeStr);
                                duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        }
                        else {
                                distLong = Math.round(distLong/1000);
                                distStr = String.valueOf(distLong + " km");
                                Log.d(TAG, "distStr = " + distStr);
                                distance.setText(distStr);
                                distance.setPaintFlags(distance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                                                long timeLong = Math.round(3 * distLong / 2);           //      40km/hr into km/min
                                long timeLong = Math.round(2 * distLong);           //      30km/hr into km/min
                                if (timeLong > 60) {
                                        timeLong = Math.round(timeLong/60);
                                        timeStr = String.valueOf(timeLong) + " hr";
                                }
                                else {
                                        timeStr = String.valueOf(timeLong) + " min";
                                }
                                Log.d(TAG, "timeStr = " +timeStr);
                                duration.setText(timeStr);
                                duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        }
//                                                minimumLocation = new LatLng(latdub, lngdub);
//                        minimumTime = timeStr;
//                        minDistStr = distStr;
                        if (keyStr.contains("b"))               radiobuttonId = Integer.parseInt(keyStr.split("b")[1]);
//                                        }

                        return data;

                } finally {
                        assert iStream != null;
                        iStream.close();
                        urlConnection.disconnect();
                }
                return data;
        }

        public boolean isInternetOn () {

//                Log.e(TAG, "isInternetOn fired");
                ConnectivityManager connec =
                        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                // Check for network connections
                if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {

                        // if connected with internet
                        return true;

                } else if (
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {

                        return false;
                }
                return  false;

        }
        public void showInternetStatus() {

//                Log.e(TAG, "showInternetStatus fired");
                ConnectivityManager connec =
                        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

                // Check for network connections
                if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {

                        // if connected with internet
//                        Log.e(TAG, "HERE I COME (flagInternet) = "+flagInternet);
                        if (!flagInternet)      return;
                        flagInternet = false;
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connected", Snackbar.LENGTH_SHORT);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.parseColor(primeColorString));
                        final float scale = getResources().getDisplayMetrics().density;
                        final float dps = 40;
                        int pixels = (int) (dps * scale + 0.5f);                //converting 40 dp into pixels

                        TextView tview = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tview.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                tview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
                        snackbar.show();

                } else if (
                        connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                                connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
                        if (flagInternet)       return;
//                        Log.e(TAG, "NOW I GO (flagInternet) = "+ flagInternet);
                        flagInternet = true;

                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE);

                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.parseColor("#cc0000"));
                        final float scale = getResources().getDisplayMetrics().density;
                        final float dps = 40;
                        int pixels = (int) (dps * scale + 0.5f);                //converting 40 dp into pixels

                        TextView tview = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tview.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                tview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }

                        snackbar.show();

                }

        }

        public void showNetworkState(int a) {

                Log.e(TAG, "showNetworkState fired");
                Log.e(TAG, "a="+a);
//                ConnectivityManager connec =
//                        (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
//                Log.d(TAG, "CONNEC="+connec.toString());
//                NetworkInfo networkInfo = connec
//                        .getActiveNetworkInfo();
//                Log.e(TAG, "NETWORKINFO = "+ networkInfo.toString());
//                Log.e(TAG, "NETWORKINFO.isConnected()" + networkInfo.isConnected());
                if (a == 1) {

                        // if connected with internet
                        Log.e(TAG,"YES");
                        Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                        Log.e(TAG,"COORDINATORLAYOUT");
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connected", Snackbar.LENGTH_LONG);
                        Log.e(TAG, "snackbar = "+snackbar.toString());
                        View sbView = snackbar.getView();
                        Log.e(TAG,"sbview="+sbView.toString());
                        sbView.setBackgroundColor(Color.parseColor(primeColorString));
                        TextView tview = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        Log.e(TAG,"TVIEW="+tview.toString());
                        tview.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                tview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
//                        snackbar.setActionTextColor(ColorStateList.valueOf(Color.parseColor(primeColorString)));
                        snackbar.show();



                } else {
                        Log.e(TAG,"NO");
                        Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.parseColor("#cc0000"));
                        TextView tview = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        tview.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                tview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }
//                        snackbar.setActionTextColor(ColorStateList.valueOf(Color.parseColor(primeColorString)));
                        snackbar.show();
                }

        }

        public void sendOrCancel(View view) {

                if(flagSendOrCancel == 0) {
                        pickMe(view);
                        flagSendOrCancel = 1;
                }
                else{
                        cancel(view);
                        flagSendOrCancel = 0;
                }

        }

        public void onClickNearestBusFloatingButton(View view) {
                if(!isWindowReady)        return;
                getNearestBus();
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
                        showInternetStatus();
                        if (!isInternetOn())            return;

                        ParserTask parserTask = new ParserTask();
                        if (result != null) {
                                Log.d(TAG, "result in Download Task = " + result);
//                                 Invokes the thread for parsing the JSON data
                                parserTask.execute(result);
                        }
                        else {
                                Log.e(TAG, "result is null.");
                        }

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
                                Log.d(TAG, "jsonData[0] = "+jsonData[0] );
                                Log.d(TAG, "jObject.toString() = " + jObject.toString());
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
                                Log.e(TAG, "JSONParser class didn't work properly");
                                return null;
                        }

                }

                // Executes in UI thread, after the parsing process
                @Override
                protected void onPostExecute(JSONObject result) {

                        showInternetStatus();
                        if (!isInternetOn())            return;

                        List<List<HashMap<String, String>>> routes;
                        DirectionsJSONParser parser = new DirectionsJSONParser();
                        Log.d(TAG, "parser.toString() = " + parser.toString());
                    //    Log.d(TAG, "SOMETHING IS HAPPENING");

                        // Starts parsing data
                        routes = parser.parse(result);
                        if (routes == null)     return;
                        Log.d(TAG, "Executing routes");
                        Log.d(TAG, routes.toString());
                        Log.d(TAG, "routes = " + routes);

                        ArrayList<LatLng> points;
                        //PolylineOptions lineOptions = null;
                        //MarkerOptions markerOptions = new MarkerOptions();
                        String thedistance = "";
                        String theduration = "";

                        Log.d(TAG, "result = " + routes.size());

                        try {
                                if (routes.size() < 1) {
//                                        Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "No Points");
//                                        double dist = SphericalUtil.computeDistanceBetween(mCurrentPosition,  new LatLng(latitudeBus, longitudeBus));
                                        double  dist = CalculationByDistance(mCurrentPosition, new LatLng(latitudeBus, longitudeBus))*1000;
//                                        Log.d(TAG, "dist = "+ dist);
//                                        if (dist < minimumDistance) {
//                                                minimumDistance = dist;
                                                long distLong = Math.round(dist);
                                                String timeStr;
                                                String distStr;
                                                if (dist < 1000) {
                                                        distStr = String.valueOf(distLong) + " m";
//                                                        Log.d(TAG, "distStr = " + distStr);
                                                        distance.setText(distStr);
                                                        distance.setPaintFlags(distance.getPaintFlags() & Paint.UNDERLINE_TEXT_FLAG);
//                                                long timeLong =Math.round(9 * distLong / 100);          //      40km/hr into m/s
                                                        long timeLong =Math.round(12 * distLong / 100);          //      30km/hr into m/s
                                                        if (timeLong > 60)            {
                                                                timeLong = Math.round(timeLong / 60);
                                                                timeStr = String.valueOf(timeLong) + " min";
                                                        }
                                                        else {
                                                                timeStr = String.valueOf(timeLong) + " s";
                                                        }
                                                        Log.d(TAG, "timeStr = " +timeStr);
                                                        duration.setText(timeStr);
                                                        duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                                }
                                                else {
                                                        distLong = Math.round(distLong/1000);
                                                        distStr = String.valueOf(distLong + " km");
//                                                        Log.d(TAG, "distStr = " + distStr);
                                                        distance.setText(distStr);
                                                        distance.setPaintFlags(distance.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//                                                long timeLong = Math.round(3 * distLong / 2);           //      40km/hr into km/min
                                                        long timeLong = Math.round(2 * distLong);           //      30km/hr into km/min
                                                        if (timeLong > 60) {
                                                                timeLong = Math.round(timeLong/60);
                                                                timeStr = String.valueOf(timeLong) + " hr";
                                                        }
                                                        else {
                                                                timeStr = String.valueOf(timeLong) + " min";
                                                        }
//                                                        Log.d(TAG, "timeStr = " +timeStr);
                                                        duration.setText(timeStr);
                                                        duration.setPaintFlags(duration.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                                }
//                                                minimumLocation = new LatLng(latdub, lngdub);
//                                                minimumTime = timeStr;
//                                                minDistStr = distStr;
                                                if (keyStr.contains("b"))               radiobuttonId = Integer.parseInt(keyStr.split("b")[1]);
//                                        }

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
//                                                Log.d(TAG, "DISTANCE = " + thedistance);
                                                distance.setText(thedistance);
                                                continue;
                                        } else if (j == 1) { // Get duration from the list
                                                theduration = point.get("duration");
//                                                Log.d(TAG, "DURATION = " + theduration);

                                                duration.setText(theduration);
                                                continue;
                                        }

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng position = new LatLng(lat, lng);
                                        points.add(position);
                                        break;                          //when no need to find the polyline of route
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

                private class UserRequest {

                        private String key;
                        private String latitude;
                        private String longitude;
                        private String status;

                        UserRequest(String key, String latitude, String longitude, String status) {
                                this.key = key;
                                this.latitude = latitude;
                                this.longitude = longitude;
                                this.status = status;
                        }

                        Map<String, String> toMap() {
                                Map<String, String> map = new HashMap<>();
                                map.put(key, "0");
                                map.put(LATITUDE, latitude);
                                map.put(LONGITUDE, longitude);
                                map.put("status", status);
                                return map;
                        }

                }
        @Override
        protected void onDestroy() {

                super.onDestroy();
                Log.d(TAG, "on Destroy fired ....");
                if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                }
                View view = new View(this);
                cancel(view);
                SharedPreferences prefs = getSharedPreferences("onStop", MODE_PRIVATE);
                SharedPreferences.Editor outState =  prefs.edit();
                if(pickMeRadioButton != null) {
                        Log.e(TAG, "pickMeRadioButton = "+pickMeRadioButton.getId());
                        outState.putInt("pickMeRadioButton", pickMeRadioButton.getId());
                }
                else {
                        if(pickMeRadioButtonId != -1) {
                                outState.putInt("pickMeRadioButton", pickMeRadioButtonId);
                        }
                        else {
                                outState.putInt("pickMeRadioButton", -1);
                        }
                }

                outState.putInt("flagSendOrCancel", 0);
                outState.putString(minNameStr, minName);
                outState.apply();
        }

        public static boolean isActivityVisible() {
                return activityVisible; // return true or false
        }

}

