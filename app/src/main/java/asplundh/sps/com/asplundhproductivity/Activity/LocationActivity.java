package asplundh.sps.com.asplundhproductivity.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.text.DateFormat;
import java.util.Date;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Service.GeocodeAddressIntentService;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static asplundh.sps.com.asplundhproductivity.Activity.HomeActivity.points;
import static asplundh.sps.com.asplundhproductivity.R.id.map;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback
{
    protected static final String TAG = "location-updates-sample";
    /**
     * 10秒間隔で位置情報を更新。実際には多少頻度が多くなるかもしれない。
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    
    /**
     * 最速の更新間隔。この値より頻繁に更新されることはない。
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    
    
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    
   // private ActivityMainBinding mBinding;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mAccuracyLabel;
    TextView tv_accuracy;
    private String mLastUpdateTimeLabel;
    
    public static GoogleMap mMap;
    SupportMapFragment mapFragment;
    TextView tv_address , tv_lat , tv_lng;
    AddressResultReceiver mResultReceiver;
    boolean isGotAddress = false;
    int areaInMiles = 0;
    String LAT = "" , LNG = "" , radius = "0" , Address = "";
    ProgressDialog progress;
    Marker mMarker;
    Polyline line;
    String selectedRadio = "Specific Point";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        
      /*  for(int i=0; i< points.size(); i++)
        {
            Log.i(AppConstants.TAG , "POINTS:" + points.get(i).toString());
        }*/
        
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_lat = (TextView) findViewById(R.id.tv_lat);
        tv_lng = (TextView) findViewById(R.id.tv_lng);
        
        
        LinearLayout btn_backward = (LinearLayout) findViewById(R.id.btn_backward);
        btn_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        LinearLayout btn_forward = (LinearLayout) findViewById(R.id.btn_forward);
        btn_forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog= new Dialog(LocationActivity.this);
                dialog.setContentView(R.layout.dialog_specific_point);
                dialog.setCanceledOnTouchOutside(false);
                
                RelativeLayout btn_save  = (RelativeLayout) dialog.findViewById(R.id.btn_save);
                final EditText et_area_in_miles = (EditText) dialog.findViewById(R.id.et_area_in_miles);
                final EditText et_select_line_circuit = (EditText) dialog.findViewById(R.id.et_select_line_circuit);
                
                
                btn_save.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String area_in_miles = et_area_in_miles.getText().toString();
                        radius = area_in_miles;
                        
                      if(selectedRadio.equalsIgnoreCase("Radius"))
                          selectedRadio = selectedRadio + "  " + et_area_in_miles.getText().toString();
                      else if(selectedRadio.equalsIgnoreCase("Line/Circuit"))
                          selectedRadio = selectedRadio + "  " + et_select_line_circuit.getText().toString();
    
                            Intent i = new Intent(LocationActivity.this , ConfirmationActivity.class);
                            i.putExtra("LAT" , LAT);
                            i.putExtra("LNG" , LNG);
                            i.putExtra("ADDRESS" , Address);
                            i.putExtra("RADIUS" , radius);
                            i.putExtra("SELECTEDRADIO" , selectedRadio);
    
                            startActivity(i);
    
                            dialog.dismiss();
                        
                    }
                });
                
                RelativeLayout btn_cancel  = (RelativeLayout) dialog.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                });
    
                final ImageView iv_specific_point_radio = (ImageView) dialog.findViewById(R.id.iv_specific_point_radio);
                final ImageView iv_radius_radio = (ImageView) dialog.findViewById(R.id.iv_radius_radio);
                final ImageView iv_line_circuit_radio = (ImageView) dialog.findViewById(R.id.iv_line_circuit_radio);
    
                iv_specific_point_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        selectedRadio = "Specific Point";
                        iv_specific_point_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                        iv_radius_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                        iv_line_circuit_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    }
                });
    
                iv_radius_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        selectedRadio = "Radius";
                        iv_specific_point_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                        iv_radius_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                        iv_line_circuit_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    }
                });
    
                iv_line_circuit_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        selectedRadio = "Line/Circuit";
                        iv_specific_point_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                        iv_radius_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                        iv_line_circuit_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    }
                });
                
    
                dialog.show();
            }
        });
        
        final RelativeLayout btn_add_note = (RelativeLayout) findViewById(R.id.btn_add_note);
        btn_add_note.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popup = new PopupMenu(LocationActivity.this, btn_add_note);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.add_note_menu, popup.getMenu());
    
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        
                        if(item.getTitle().toString().equalsIgnoreCase("Add Text"))
                        {
                            Intent add_text = new Intent(LocationActivity.this , AddTextActivity.class);
                            startActivity(add_text);
                        }
                        else if(item.getTitle().toString().equalsIgnoreCase("Add Voice"))
                        {
                            Intent add_voice = new Intent(LocationActivity.this , VoiceRecordingActivity.class);
                            startActivity(add_voice);
                        }
                        
                        return true;
                    }
                });
    
                popup.show();
            }
        });
    
        progress = ProgressDialog.show(this, "Wait",
                                       "Working", true);
    
        mResultReceiver = new AddressResultReceiver(null);
        
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        mMap = mapFragment.getMap();
    
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        
        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
    }
    
    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        Log.i(TAG, "Updating values from bundle");
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
        Log.i(TAG, "Building GoogleApiClient");
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
        clearUI();
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
    
    
    public void stopUpdatesButtonHandler(View view)
    {
        if (mRequestingLocationUpdates)
        {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
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
                        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationActivity.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
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
    
    private void clearUI() {
        /*mBinding.latitudeText.setText("");
        mBinding.longitudeText.setText("");
        mBinding.lastUpdateTimeText.setText("");*/
    }
    
    private void updateUI() {
        if (mCurrentLocation == null) return;
        
        Log.v(AppConstants.TAG , "LAT: " + mCurrentLocation.getLatitude());
        tv_lat.setText("Latitude: " + mCurrentLocation.getLatitude());
        tv_lng.setText("Longitude: " + mCurrentLocation.getLongitude());
        
        /*mBinding.latitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                                                    mCurrentLocation.getLatitude()));
        mBinding.longitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                                                     mCurrentLocation.getLongitude()));
        tv_accuracy.setText(mCurrentLocation.getAccuracy() + "");
        mBinding.lastUpdateTimeText.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                                                          mLastUpdateTime));*/
    }
    
    protected void stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates");
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
                        Toast.makeText(LocationActivity.this, "Permisiion not granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v(AppConstants.TAG , "showRationaleDialog onRequestPermissionsResult");
    
                        showRationaleDialog();
                    }
                }
                break;
            }
        }
    }
    
    private void showRationaleDialog() {
        
        Log.i(AppConstants.TAG , "showRationaleDialog");
        
        new AlertDialog.Builder(this)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(LocationActivity.this,
                                                          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("Do not\n", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LocationActivity.this, "Location information permission is not allowed\n。", Toast.LENGTH_SHORT).show();
                        mRequestingLocationUpdates = false;
                    }
                })
                .setCancelable(false)
                .setMessage("This app must allow location information to be used.")
                .show();
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
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onResume() {
        super.onResume();
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
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }
    
    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        
        Log.w(AppConstants.TAG , "LAT: " + location.getLatitude());
        Log.w(AppConstants.TAG , "LNG: " + location.getLongitude());
        
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
      //  updateUI();
        
      //  mMarker.setPosition(new LatLng(location.getLatitude() , location.getLongitude()));
    
       /* mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));
        progress.dismiss();*/
    
        /*if(!isGotAddress)
            getGeoCodeAddress(location.getLatitude() , location.getLongitude());*/
    
        LAT = mCurrentLocation.getLatitude() + "";
        LNG = mCurrentLocation.getLongitude()+ "";
    
       /* LatLng latLng = new LatLng(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());
        points.add(latLng);*/
       
      //  Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
    }
    
    private void drawLine()
    {
        mMap.clear();
        
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++)
        {
            LatLng point = points.get(i);
            options.add(point);
        }
        int lastIndex = points.size() -1;
        LatLng lastLatLng = points.get(lastIndex);
    
         mMarker = mMap.addMarker(new MarkerOptions().position(lastLatLng).title("Your Location"));
       //  updateMarker(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());
        line = mMap.addPolyline(options); //add Polyline
    }
    
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(AppConstants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
    
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.d(AppConstants.TAG , "onMapReady");
        mMap = googleMap;
        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.add_note_ic);
        TileOverlayOptions opts = new TileOverlayOptions();


      //  drawLine();
    }
    
    public void updateMarker(double lat , double Long)
    {
       // mMap.clear();
        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, Long)).title("Your Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, Long), 18.0f));
        
        progress.dismiss();
    
        if(!isGotAddress)
            getGeoCodeAddress(lat , Long);
    }
    
    public void getGeoCodeAddress(double Lat, double Long)
    {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(AppConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppConstants.FETCH_TYPE_EXTRA, AppConstants.USE_ADDRESS_NAME);
        
        intent.putExtra(AppConstants.LOCATION_LATITUDE_DATA_EXTRA, Lat);
        intent.putExtra(AppConstants.LOCATION_LONGITUDE_DATA_EXTRA, Long);
        
        startService(intent);
    }
    
    
    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver
    {
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }
        
        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData)
        {
            if (resultCode == AppConstants.SUCCESS_RESULT)
            {
                String address = resultData.getString(AppConstants.RESULT_ADDRESS);
                
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        
                        String address = resultData.getString(AppConstants.RESULT_ADDRESS);
                        Log.v(AppConstants.TAG , "address is: " + address);
                        tv_address.setText(address);
                        isGotAddress = true;
                        Address = address;
                        
                        try
                        {
                            /*devicesArrayList.get(resultData.getInt("Index")).setAddress(address);
                            devicesAdapter.notifyItemChanged(resultData.getInt("Index"));*/
                            
                        }
                        catch (IndexOutOfBoundsException e)
                        {
                            Log.e(AppConstants.TAG, "IndexOutOfBoundsException ");
                        }
                        
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.v(AppConstants.TAG, " " + resultData.getString(AppConstants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }
    
}
