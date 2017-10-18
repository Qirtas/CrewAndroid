package asplundh.sps.com.asplundhproductivity.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import java.text.DateFormat;
import java.util.Date;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class CircuitEstimationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener
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
    PolylineOptions options;
    
    private MapView mapView;
    private MapboxMap map;
    ImageView btn_note;
    boolean markerAdded = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicWlydGFzIiwiYSI6ImNqOGZoajV2ODA0NDEycXMxNDJqbHIydnkifQ.HB3H8VxTc9hW_XR1DDSZJg");
    
        setContentView(R.layout.activity_circuit_estimation);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
         options = new PolylineOptions().width(7).color(Color.BLUE);
    
        btn_note = (ImageView) findViewById(R.id.btn_note);
        btn_note.setOnClickListener(this);
    
        mRequestingLocationUpdates = false;
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
    
        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                map = mapboxMap;
               // mapboxMap.setMyLocationEnabled(true);
               /* mapboxMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(33.684132, 73.045020))
                                            .title("Eiffel Tower")
                                   );*/
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(33.684132, 73.045020));
                CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
                mapboxMap.moveCamera(center);
                mapboxMap.animateCamera(zoom);
            
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
                        CircuitEstimationActivity.this.getResources().getDisplayMetrics().density);
            
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
            
            }
        });
    
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
        RelativeLayout btn_finish = (RelativeLayout) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case R.id.back_ic:
                finish();
                break;
            
            case R.id.logout_ic:
                Intent intent = new Intent(CircuitEstimationActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
    
            case R.id.btn_note:
                showAddNoteDialog();
                break;
    
            case R.id.btn_finish:
                showFinishDialog();
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
                        ActivityCompat.requestPermissions(CircuitEstimationActivity.this,
                                                          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("Do not\n", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CircuitEstimationActivity.this, "Location information permission is not allowed\n。", Toast.LENGTH_SHORT).show();
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
                        if (ContextCompat.checkSelfPermission(CircuitEstimationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, CircuitEstimationActivity.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(CircuitEstimationActivity.this, REQUEST_CHECK_SETTINGS);
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
    
    @Override
    public void onLocationChanged(Location location)
    {
        
        Log.d(AppConstants.TAG , "LAT: " + location.getLatitude());
        Log.d(AppConstants.TAG , "LNG: " + location.getLongitude());
        
        updatePath(location.getLatitude() , location.getLongitude());
        
       // map.clear();
        
        Toast.makeText(CircuitEstimationActivity.this , "LATT: " + location.getLatitude(),
                       Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        mGoogleApiClient.connect();
        isPlayServicesAvailable(this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
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
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
    }
    
    public void showAddNoteDialog()
    {
        final Dialog dialog = new Dialog(CircuitEstimationActivity.this,R.style.mapbox_AlertDialogStyle);
        dialog.setContentView(R.layout.dialog_add_note);
        dialog.setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    
        final EditText et_note = (EditText) dialog.findViewById(R.id.et_note);
        et_note.requestFocus();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    
        RelativeLayout btn_done = (RelativeLayout) dialog.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    
        dialog.show();
    }
    
    public void showFinishDialog()
    {
        final Dialog dialog = new Dialog(CircuitEstimationActivity.this,R.style.mapbox_AlertDialogStyle);
        dialog.setContentView(R.layout.dialog_submit);
        dialog.setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        RelativeLayout btn_yes = (RelativeLayout) dialog.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    
        RelativeLayout btn_no = (RelativeLayout) dialog.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    
        dialog.show();
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
                        Toast.makeText(CircuitEstimationActivity.this, "Permisiion not granted", Toast.LENGTH_SHORT).show();
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
    
    Marker mMarker = null;
    
    public void updatePath(double lat , double lng)
    {
        LatLng point = new LatLng(lat, lng);
        options.add(point);
    
        MarkerOptions markerOptions = new MarkerOptions().position(point).title("");
        
        if(!markerAdded)
        {
            mMarker = map.addMarker(markerOptions);
            markerAdded = true;
        }
        else
            mMarker.setPosition(point);
    
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(point);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(17);
        map.moveCamera(center);
        map.animateCamera(zoom);
        
        map.addPolyline(options);
    }
    
}
