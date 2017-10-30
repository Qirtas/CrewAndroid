package asplundh.sps.com.asplundhproductivity.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Model.Point;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static asplundh.sps.com.asplundhproductivity.R.id.loc_cuurent_ic;
import static asplundh.sps.com.asplundhproductivity.Utils.AppConstants.BASE_URL;

public class LocationDemoActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,View.OnClickListener
{
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    
    boolean isGotAddress = false;
    private String mLastUpdateTime;
    
    private MapView mapView;
    private MapboxMap map;
    public static double Lat = 0.0 , Lng = 0.0 , lastAccuracy = 21;
    String circuitID = "";
    ArrayList<Point> pointsArrayList = new ArrayList<Point>();
    PolylineOptions options;
    boolean markerAdded = false;
    JSONArray SubunitsArray;
    String bidPlanID = "" , circuitName = "";
    boolean isMapReady = false;
    JSONArray surveyPathArray;
    OfflineManager offlineManager;
    public static LatLngBounds.Builder builder = new LatLngBounds.Builder();
    int builderCounter = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicWlydGFzIiwiYSI6ImNqOGZoajV2ODA0NDEycXMxNDJqbHIydnkifQ.HB3H8VxTc9hW_XR1DDSZJg");
        setContentView(R.layout.activity_map);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        TextView tv_circuit_name = (TextView) findViewById(R.id.tv_circuit_name);
        
        circuitID = getIntent().getStringExtra("CIRCUITID");
        bidPlanID = getIntent().getStringExtra("BIDPLANID");
        circuitName = getIntent().getStringExtra("CIRCUITTITLE");
        tv_circuit_name.setText(circuitName);
        
        /*if(AppConstants.isNetworkAvailable(LocationDemoActivity.this))
            getCircuitPath();
        else
            getCircuitPathLocally();*/
        
        RelativeLayout loc_cuurent_ic = (RelativeLayout) findViewById(R.id.loc_cuurent_ic);
        loc_cuurent_ic.setOnClickListener(this);
        RelativeLayout btn_start = (RelativeLayout) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
        
        RelativeLayout lay_map = (RelativeLayout) findViewById(R.id.lay_map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        
        params.setMargins(7, 160, 7, 7);
       // lay_map.setLayoutParams(params);
        
        mapView.onCreate(savedInstanceState);
    
        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                map = mapboxMap;
                isMapReady = true;
    
                if(AppConstants.isNetworkAvailable(LocationDemoActivity.this))
                    getCircuitPath();
                /*else
                    getCircuitPathLocally();*/
                
                offlineManager = OfflineManager.getInstance(LocationDemoActivity.this);
                
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(33.684132, 73.045020));
                CameraUpdate zoom= CameraUpdateFactory.zoomTo(18);
                
              /*  mapboxMap.moveCamera(center);
                mapboxMap.animateCamera(zoom);*/
    
                /*map.moveCamera(center);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, Lng), 16.0f));*/
            
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(33.587444, 73.092093)) // Northeast
                        .include(new LatLng(33.577789, 73.091201)) // Southwest
                        .build();
            
                // Define the offline region
                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        mapboxMap.getStyleUrl(),
                        latLngBounds,
                        10,
                        20,
                        LocationDemoActivity.this.getResources().getDisplayMetrics().density);
            
                byte[] metadata = null;
            
                // OfflineManager.
                
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
            
                LatLng point = new LatLng(33.681439, 73.039654);
                LatLng point1 = new LatLng(33.689258, 73.054586);
                LatLng point2 = new LatLng(33.676029, 73.046414);
            
                options.add(point);
                options.add(point1);options.add(point2);
            
                //  updateMarker(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());
                //  mapboxMap.addPolyline(options);
    
                offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback()
                {
                    @Override
                    public void onList(OfflineRegion[] offlineRegions)
                    {
                        Log.v(AppConstants.TAG , "offlineRegions: " + offlineRegions.length);
                        
                        if(offlineRegions.length > 0)
                        {
                            Log.v(AppConstants.TAG , "offlineRegions id: " + offlineRegions[0].getID());
                            Log.v(AppConstants.TAG , "offlineRegions metadata: " + offlineRegions[0].getMetadata().toString());
                            Log.v(AppConstants.TAG , "offlineRegions definition: " + offlineRegions[0].getDefinition());
                        }
            
                    }
        
                    @Override
                    public void onError(String error)
                    {
                        Log.e(AppConstants.TAG , "offlineRegions onError: ");
                    }
                });
                
    
                /*ArrayList<OfflineMapDatabase> offlineMapDatabases = offlineMapDownloader.getMutableOfflineMapDatabases();
                OfflineMapDatabase db = offlineMapDatabases.get(0);
                OfflineMapTileProvider tp = new OfflineMapTileProvider(getActivity(), db);
                offlineMapOverlay = new TilesOverlay(tp);
                mapView.addOverlay(offlineMapOverlay);*/
    
                map.setOnMapClickListener(new MapboxMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick(@NonNull LatLng point)
                    {
                        Log.v(AppConstants.TAG , "onMapClick: " + point.getLatitude());
                    }
                });
            
            }
        });
        
        mRequestingLocationUpdates = false;
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case loc_cuurent_ic:
                moveMapToCurrentLocation();
                break;
            
            case R.id.back_ic:
                finish();
                break;
            
            case R.id.logout_ic:
                Intent intent = new Intent(LocationDemoActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            
            case R.id.btn_start:
                
               /* if(lastAccuracy < 21)
                {*/
                    try
                    {
                        String startTime = AppConstants.getISOCurrentTime();
        
                        Intent intentt = new Intent(LocationDemoActivity.this, CircuitEstimationActivity.class);
                        intentt.putExtra("POINTSARRAY" , pointsArrayList);
                        intentt.putExtra("CircuitSurveyPath" , surveyPathArray.toString());
                        intentt.putExtra("SUBUNITSARRAY" , SubunitsArray.toString());
                        intentt.putExtra("CIRCUITID" , circuitID);
                        intentt.putExtra("CIRCUITNAME" , circuitName);
                        intentt.putExtra("BIDPLANID" , bidPlanID);
                        intentt.putExtra("STARTTIME" , startTime);
        
                        startActivity(intentt);
                    }
                    catch (NullPointerException e)
                    {
                        Log.e(AppConstants.TAG , "NullPointerException:  R.id.btn_start: " + e.toString());
        
                        String startTime = AppConstants.getISOCurrentTime();
                        Intent intentt = new Intent(LocationDemoActivity.this, CircuitEstimationActivity.class);
                        intentt.putExtra("CIRCUITID" , circuitID);
                        intentt.putExtra("BIDPLANID" , bidPlanID);
                        intentt.putExtra("STARTTIME" , startTime);
                        intentt.putExtra("CircuitSurveyPath" , "");
                        startActivity(intentt);
                    }
               /* }
                else
                {
                    Toast.makeText(LocationDemoActivity.this , "Please try again after few sec! Accuracy is not better now",
                                   Toast.LENGTH_LONG).show();
                }*/
                
                break;
        }
    }
    
    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        Log.i(AppConstants.TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }
            
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }
    
    protected synchronized void buildGoogleApiClient()
    {
        Log.i(AppConstants.TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        startUpdatesButtonHandler();
    }
    
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    
    public void startUpdatesButtonHandler() {
        
        if (!isPlayServicesAvailable(this)) return;
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
        } else {
            return;
        }
        
        if (Build.VERSION.SDK_INT < 23) {
            setButtonsEnabledState();
            startLocationUpdates();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setButtonsEnabledState();
            startLocationUpdates();
            Log.w(AppConstants.TAG , "startLocationUpdates startUpdatesButtonHandler");
            //  mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.v(AppConstants.TAG , "showRationaleDialog startUpdatesButtonHandler");
                showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }
    
    private void showRationaleDialog() {
        
        Log.i(AppConstants.TAG , "showRationaleDialog");
        
        new AlertDialog.Builder(this)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LocationDemoActivity.this,
                                                          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("Do not\n", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LocationDemoActivity.this, "Location information permission is not allowed\n。", Toast.LENGTH_SHORT).show();
                        mRequestingLocationUpdates = false;
                    }
                })
                .setCancelable(false)
                .setMessage("This app must allow location information to be used.")
                .show();
    }
    
   
    
    private void startLocationUpdates() {
        Log.v(AppConstants.TAG, "startLocationUpdates");
        
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        // 現在位置の取得の前に位置情報の設定が有効になっているか確認する
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.w(AppConstants.TAG, "SUCCESS");
                        // 設定が有効になっているので現在位置を取得する
                        if (ContextCompat.checkSelfPermission(LocationDemoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationDemoActivity.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(LocationDemoActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
           /* mBinding.startUpdatesButton.setEnabled(false);
            mBinding.stopUpdatesButton.setEnabled(true);*/
        } else {
           /* mBinding.startUpdatesButton.setEnabled(true);
            mBinding.stopUpdatesButton.setEnabled(false);*/
        }
    }
    
    private void updateUI() {
        if (mCurrentLocation == null) return;
        
        Log.v(AppConstants.TAG , "LATTTT: " + mCurrentLocation.getLatitude());
        
        
        /*mBinding.latitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                                                    mCurrentLocation.getLatitude()));
        mBinding.longitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                                                     mCurrentLocation.getLongitude()));
        tv_accuracy.setText(mCurrentLocation.getAccuracy() + "");
        mBinding.lastUpdateTimeText.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                                                          mLastUpdateTime));*/
    }
    
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(AppConstants.TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
    
        if (mRequestingLocationUpdates) {
            // Log.w(AppConstants.TAG , "startLocationUpdates onConnected");
        
            //  startLocationUpdates();
        }
    }
    
    @Override
    public void onConnectionSuspended(int i)
    {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(AppConstants.TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i(AppConstants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    
    }
    
    Marker mMarker = null;
    
    @Override
    public void onLocationChanged(Location location)
    {
        
       // Log.w(AppConstants.TAG , "LAT: " + location.getLatitude());
        Log.w(AppConstants.TAG , "ACCURACY: " + location.getAccuracy());
        lastAccuracy = location.getAccuracy();
        
        Lat = location.getLatitude();
        Lng = location.getLongitude();
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        
        builder.include(new LatLng(Lat , Lng));
        builderCounter ++;
    
        //map.clear();
        MarkerOptions markerOptions = new MarkerOptions().position(point).title("");
    
        try
        {
            if(isMapReady)
            {
                if(!markerAdded)
                {
                    mMarker = map.addMarker(markerOptions);
                    markerAdded = true;
                }
                else
                    mMarker.setPosition(point);
            }
            
        }
        catch (NullPointerException e)
        {
            Log.e(AppConstants.TAG , "NullPointerException while adding marker: " + e.toString());
        }
        
        
      //  map.addMarker(new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title(""));
        
      /*  Toast.makeText(LocationDemoActivity.this , "Accuracy demo: " + location.getAccuracy(),
                       Toast.LENGTH_SHORT).show();*/
    }
    
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        
        /*if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }*/
        
    }
    
    protected void stopLocationUpdates() {
        Log.i(AppConstants.TAG, "stopLocationUpdates");
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        try
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        catch (RuntimeException e)
        {
            
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setButtonsEnabledState();
                    startLocationUpdates();
                    Log.w(AppConstants.TAG , "startLocationUpdates onRequestPermissionsResult");
                    //mMap.setMyLocationEnabled(true);
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        mRequestingLocationUpdates = false;
                        Toast.makeText(LocationDemoActivity.this, "Permisiion not granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v(AppConstants.TAG , "showRationaleDialog onRequestPermissionsResult");
                        
                        showRationaleDialog();
                    }
                }
                break;
            }
        }
    }
    
    public static boolean isPlayServicesAvailable(Context context) {
        // Google Play Service APKが有効かどうかチェックする
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, resultCode, 2).show();
            return false;
        }
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        
        isPlayServicesAvailable(this);
        
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            //   startLocationUpdates();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    
    public void moveMapToCurrentLocation()
    {
        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(Lat, Lng));
                CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
    
                // map.animateCamera(zoom , 4000);
                map.moveCamera(center);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat, Lng), 16.0f));
            }
        });
    }
    
    private void getCircuitPath()
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.getting_data), true);
        
        Log.i(AppConstants.TAG , "getBidPlans URL: " + BASE_URL + "getCircuitPath?Id="  + circuitID);
        
        RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.GET,
                                                      AppConstants.BASE_URL + "getCircuitPath?Id=" + circuitID
                                                      //+ circuitID
                
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                        
                                                              Log.d(AppConstants.TAG , "getCircuitPath ressponse: " + response);
                                                              // bidPlansJson = response;
                    
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
                        
                                                                  boolean success = jsonObj.getBoolean("success");
                                                                  String message = jsonObj.getString("message");
                        
                                                                  if(!success)
                                                                  {
                                                                      Toast.makeText(LocationDemoActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                  {
                                                                      
                                                                      boolean isPathExist = false , isSurveyExist = false;
                                                                      JSONObject result = jsonObj.getJSONObject("result");
                                                                      JSONArray pointsArray = result.getJSONArray("CircuitPath");
                                                                      
                                                                      if(pointsArray.length() > 0)
                                                                      {
                                                                          isPathExist = true;
                                                                          
                                                                          for(int i=0; i<pointsArray.length(); i++)
                                                                          {
                                                                              JSONObject obj = pointsArray.getJSONObject(i);
                                                                              String LAT = obj.opt("x").toString();
                                                                              String LNG = obj.opt("y").toString();
        
                                                                              Point point = new Point(LAT ,LNG);
                                                                              pointsArrayList.add(point);
                                                                              
                                                                                builder.include(new LatLng(Double.parseDouble(LAT) , Double.parseDouble(LNG)));
                                                                              builderCounter ++;
                                                                          }
    
                                                                          drawCircuit(pointsArrayList , "DELEGATED");
                                                                      }
                                                                      
                                                                      SubunitsArray = result.getJSONArray("Subunits");
    
                                                                      surveyPathArray = result.getJSONArray("CircuitSurveyPath");
                                                                      
                                                                      if(surveyPathArray.length() > 0)
                                                                      {
                                                                          isSurveyExist = false;
                                                                          
                                                                          for(int i=0;i<surveyPathArray.length(); i++)
                                                                          {
                                                                              JSONObject surveyPathObj = surveyPathArray.getJSONObject(i);
                                                                              JSONObject pathObject = surveyPathObj.getJSONObject("Path");
        
                                                                              JSONArray surveyPointsArray = pathObject.getJSONArray("points");
        
                                                                              ArrayList<Point> surveyPointsList = new ArrayList<Point>();
        
                                                                              for(int j=0; j<surveyPointsArray.length(); j++)
                                                                              {
                                                                                  JSONObject pointObj = surveyPointsArray.getJSONObject(j);
                                                                                  String LAT = pointObj.opt("x").toString();
                                                                                  String LNG = pointObj.opt("y").toString();
            
                                                                                  Point point = new Point(LAT ,LNG);
                                                                                  surveyPointsList.add(point);
                                                                                  drawCircuit(surveyPointsList , "SURVERY");
            
                                                                                  Log.w(AppConstants.TAG , "LAT SUREVY POINT: " + LAT);
                                                                              }
                                                                          }
                                                                      }
                                                                      
                                                                      if(!isPathExist && !isSurveyExist)
                                                                      {
                                                                          Log.e(AppConstants.TAG  , "No path or survey exists");
                                                                          mapView.getMapAsync(new OnMapReadyCallback()
                                                                          {
                                                                              @Override
                                                                              public void onMapReady(MapboxMap mapboxMap)
                                                                              {
                                                                                  CameraUpdate center=
                                                                                              CameraUpdateFactory.newLatLng(new LatLng(Lat  ,Lng));
                                                                                      CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
                                                                                      map.moveCamera(center);
                                                                                      map.animateCamera(zoom);
                                                                                  
                                                                              }
                                                                          });
                                                                      }
                                                                  }
                                                              }
                                                              catch (JSONException e)
                                                              {
                                                                  Log.e(AppConstants.TAG , "JSONException: " + e.toString());
                                                              }
                        
                                                              dialog.dismiss();
                                                          }
                                                      }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(AppConstants.TAG , "onErrorResponse: " + error.toString());
                dialog.dismiss();
                
            }
            
        }) {
            
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                80000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        mRequestQueue.add(postRequest);
    }
    
    public void drawCircuit(final ArrayList<Point> pointsArrayList , String Type)
    {
        if(Type.equalsIgnoreCase("DELEGATED"))
            options = new PolylineOptions().width(7).color(Color.BLACK);
        else
            options = new PolylineOptions().width(7).color(Color.YELLOW);
    
        try
        {
            for(int i=0; i< pointsArrayList.size(); i++)
            {
                LatLng latlng = new LatLng(Double.parseDouble(pointsArrayList.get(i).getLAT()) , Double.parseDouble(pointsArrayList.get(i).getLNG()));
                options.add(latlng);
                
                builder.include(latlng);
                builderCounter++;
                
            }
    
            mapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(MapboxMap mapboxMap)
                {
                    if(pointsArrayList.size() != 0)
                    {
                        Log.v(AppConstants.TAG , "builderCounter: " + builderCounter);
    
                        try
                        {
                            if(builderCounter > 1)
                            {
                                LatLngBounds bounds = builder.build();
                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                            }
                            else
                            {
                                CameraUpdate center=  CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(pointsArrayList.get(0).getLAT()) , Double.parseDouble(pointsArrayList.get(0).getLNG())));
                                CameraUpdate zoom= CameraUpdateFactory.zoomTo(13);
                                map.moveCamera(center);
                                map.animateCamera(zoom);
        
                            }
                        }
                        catch (InvalidLatLngBoundsException e)
                        {
                            Log.e(AppConstants.TAG , "InvalidLatLngBoundsException: " + e.toString());
                        }
                        
                        
                       /* CameraUpdate zoom= CameraUpdateFactory.zoomTo(13);
                        map.moveCamera(center);*/
                       // map.animateCamera(zoom);
                        
                        map.addPolyline(options);
                    }
                }
            });
            
        }
        catch (NullPointerException e)
        {
            Log.e(AppConstants.TAG , "NullPointerException in drawCircuit: " + e.toString());
        }
    }
    
    public void getCircuitPathLocally()
    {
        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(LocationDemoActivity.this);
        DBController mDB = new DBController(getApplicationContext());
        
        if(!circuitID.equalsIgnoreCase("NewCircuit"))
        {
            Cursor cursor_path = mDB.getCircuitPath(mPrefs.getString(AppConstants.USER_ID , "") , bidPlanID , circuitID);
            Log.i(AppConstants.TAG , "cursor_path size: " + cursor_path.getCount());
    
            if(cursor_path.moveToFirst())
            {
                String circuit_path_JSON = cursor_path.getString(cursor_path.getColumnIndex(DBController.KEY_PATH_JSON));
                Log.i(AppConstants.TAG , "circuit_path_JSON: " + circuit_path_JSON);
        
                try
                {
                    JSONArray path_array = new JSONArray(circuit_path_JSON);
            
                    ArrayList<Point> surveyPointsList = new ArrayList<Point>();
            
                    for(int i=0; i<path_array.length(); i++)
                    {
                        JSONObject obj = path_array.getJSONObject(i);
                        String LAT = obj.get("x").toString();
                        String LNG = obj.get("y").toString();
                
                        Point point = new Point(LAT ,LNG);
                        surveyPointsList.add(point);
                        drawCircuit(surveyPointsList , "SURVERY");
                    }
                }
                catch (JSONException e)
                {
                    Log.e(AppConstants.TAG , "JSONException while parsing cursor_path" + e.toString());
                }
        
            }
            else
            {
                moveMapToCurrentLocation();
            }
        }
        else // Setting camera to current position
        {
            Log.w(AppConstants.TAG , "Setting camera to current position");
            
            mapView.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(MapboxMap mapboxMap)
                {
                        CameraUpdate center=
                                CameraUpdateFactory.newLatLng(new LatLng(Lat , Lng));
                        CameraUpdate zoom= CameraUpdateFactory.zoomTo(18);
                        map.moveCamera(center);
                        map.animateCamera(zoom);
                }
            });
        }
    
    }
    
}
