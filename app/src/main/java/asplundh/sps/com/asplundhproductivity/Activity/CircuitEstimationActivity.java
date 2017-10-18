package asplundh.sps.com.asplundhproductivity.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import asplundh.sps.com.asplundhproductivity.Model.CounterPoint;
import asplundh.sps.com.asplundhproductivity.Model.Note;
import asplundh.sps.com.asplundhproductivity.Model.Point;
import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
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
    PolylineOptions optionsBLACK , optionsGREEN;
    TextView tv_pauseresume;
    
    Vibrator vibrator;
    MediaPlayer mp;
    private MapView mapView;
    private MapboxMap map;
    ImageView btn_note;
    boolean markerAdded = false;
    int counterCount = 3;
     ArrayList<Point> pointsArray= new ArrayList<Point>();
    ArrayList<SubUnit> subUnitsArray = new ArrayList<SubUnit>();
    ArrayList<Note> notesArray= new ArrayList<Note>();
    ArrayList<Point> userPathPointsArray= new ArrayList<Point>();
    ArrayList<CounterPoint> counterPointsArray= new ArrayList<CounterPoint>();
    
    int counter_one = 0 , counter_two = 0, counter_three = 0;
    public String CircuitID = "" , bidPlanID = "" , startTime = "";
    String LAT_Current = "" , LNG_Current = "";
    
    SharedPreferences mPrefs;
    boolean isPause = false;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicWlydGFzIiwiYSI6ImNqOGZoajV2ODA0NDEycXMxNDJqbHIydnkifQ.HB3H8VxTc9hW_XR1DDSZJg");
        
        setContentView(R.layout.activity_circuit_estimation);
        //setupUI(findViewById(R.id.circuit_lay));
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        optionsBLACK = new PolylineOptions().width(7).color(Color.BLACK);
        optionsGREEN = new PolylineOptions().width(7).color(Color.GREEN);
    
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.beep1);
    
        CircuitID = getIntent().getStringExtra("CIRCUITID");
        bidPlanID = getIntent().getStringExtra("BIDPLANID");
        startTime = getIntent().getStringExtra("STARTTIME");
        
        pointsArray = (ArrayList<Point>) getIntent().getSerializableExtra("POINTSARRAY");
        String subunits = getIntent().getStringExtra("SUBUNITSARRAY");
        
        try
        {
            JSONArray array = new JSONArray(subunits);
            for(int i=0; i< array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                String ID = obj.opt("Id").toString();
                String Title = obj.opt("Title").toString();
                
                Log.i(AppConstants.TAG , "SUBUNIT ID: " + ID);
                Log.i(AppConstants.TAG , "SUBUNIT Title: " + Title);
    
                SubUnit subUnit = new SubUnit(ID , Title , "");
                subUnitsArray.add(subUnit);
            }
        }
        catch (JSONException e)
        {
            Log.e(AppConstants.TAG , "JSONException while parsing subunits: " + e.toString());
        }
        
        for(int j=0;j <subUnitsArray.size();j++)
        {
            Log.w(AppConstants.TAG , "subUnitsArray: " + subUnitsArray.get(j).getId());
        }
        
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
    
                drawCircuit(pointsArray);
            
                //  updateMarker(mCurrentLocation.getLatitude() , mCurrentLocation.getLongitude());
                //  mapboxMap.addPolyline(options);
            
            }
        });
    
        TextView tv_unit_one = (TextView) findViewById(R.id.tv_unit_one);
        TextView tv_unit_two = (TextView) findViewById(R.id.tv_unit_two);
        TextView tv_unit_three = (TextView) findViewById(R.id.tv_unit_three);
        TextView tv_unit_four = (TextView) findViewById(R.id.tv_unit_four);
        TextView tv_unit_five = (TextView) findViewById(R.id.tv_unit_five);
        TextView tv_unit_six = (TextView) findViewById(R.id.tv_unit_six);
        
         tv_pauseresume = (TextView) findViewById(R.id.tv_pauseresume);
        
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
        RelativeLayout btn_finish = (RelativeLayout) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(this);
        
        RelativeLayout lay_top_row = (RelativeLayout) findViewById(R.id.lay_top_row);
        RelativeLayout lay_bottom_row = (RelativeLayout) findViewById(R.id.lay_bottom_row);
        RelativeLayout btn_pause = (RelativeLayout) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(this);
    
        LinearLayout btn_one = (LinearLayout) findViewById(R.id.btn_one);
        LinearLayout btn_two = (LinearLayout) findViewById(R.id.btn_two);
        LinearLayout btn_three = (LinearLayout) findViewById(R.id.btn_three);
        LinearLayout btn_four = (LinearLayout) findViewById(R.id.btn_four);
        LinearLayout btn_five = (LinearLayout) findViewById(R.id.btn_five);
        LinearLayout btn_six = (LinearLayout) findViewById(R.id.btn_six);
        
        final EditText et_counter_one = (EditText) findViewById(R.id.et_counter_one);
        et_counter_one.setText(counter_one + "");
    
        final EditText et_counter_two = (EditText) findViewById(R.id.et_counter_two);
        et_counter_two.setText(counter_two + "");
    
        final EditText et_counter_three = (EditText) findViewById(R.id.et_counter_three);
        et_counter_three.setText(counter_three + "");
    
        btn_one.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CounterPoint counterPoint = new CounterPoint(subUnitsArray.get(0).getId() , "1" , LAT_Current , LNG_Current);
                counterPointsArray.add(counterPoint);
                
               Log.w(AppConstants.TAG , "onClick");
                vibrator.vibrate(200);
                mp.start();
    
                String countervalueOne = et_counter_one.getText().toString();
                counter_one = Integer.parseInt(countervalueOne);
                
                counter_one++;
                et_counter_one.setText(counter_one + "");
                et_counter_one.setEnabled(false);
               
            }
        });
        
        btn_one.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                
                
                et_counter_one.setEnabled(true);
                et_counter_one.setText("");
    
                et_counter_one.requestFocus();
                et_counter_one.setFocusableInTouchMode(true);
    
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_one, InputMethodManager.SHOW_FORCED);
    
                /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_one, InputMethodManager.SHOW_IMPLICIT);*/
                
                return true;
            }
        });
    
        btn_two.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CounterPoint counterPoint = new CounterPoint(subUnitsArray.get(1).getId() , "1" , LAT_Current , LNG_Current);
                counterPointsArray.add(counterPoint);
                
                Log.w(AppConstants.TAG , "onClick" +subUnitsArray.get(1).getId());
                
                vibrator.vibrate(200);
                mp.start();
            
                String countervalueTwo = et_counter_two.getText().toString();
                counter_two = Integer.parseInt(countervalueTwo);
            
                counter_two++;
                et_counter_two.setText(counter_two + "");
                et_counter_two.setEnabled(false);
            
            }
        });
    
        btn_two.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Log.i(AppConstants.TAG , "onLongClick");
    
                et_counter_two.setEnabled(true);
                et_counter_two.setText("");
    
                et_counter_two.requestFocus();
                et_counter_two.setFocusableInTouchMode(true);
            
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_two, InputMethodManager.SHOW_FORCED);
    
                /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_one, InputMethodManager.SHOW_IMPLICIT);*/
                return true;
            }
        });
    
    
        btn_three.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CounterPoint counterPoint = new CounterPoint(subUnitsArray.get(2).getId() , "1" , LAT_Current , LNG_Current);
                counterPointsArray.add(counterPoint);
                
                Log.w(AppConstants.TAG , "onClick");
                vibrator.vibrate(200);
                mp.start();
            
                String countervalueTHree = et_counter_three.getText().toString();
                counter_three = Integer.parseInt(countervalueTHree);
            
                counter_three++;
                et_counter_three.setText(counter_three + "");
                et_counter_three.setEnabled(false);
            
            }
        });
    
        btn_three.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                Log.i(AppConstants.TAG , "onLongClick");
            
                et_counter_three.setEnabled(true);
                et_counter_three.setText("");
    
                et_counter_three.requestFocus();
                et_counter_three.setFocusableInTouchMode(true);
            
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_three, InputMethodManager.SHOW_FORCED);
    
                /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et_counter_one, InputMethodManager.SHOW_IMPLICIT);*/
                
                return true;
            }
        });
        
        /////////////////////////////////////////////////
        
        Log.d(AppConstants.TAG , "subUnitsArray size: " + subUnitsArray.size());
        counterCount = subUnitsArray.size();
                
        if(counterCount == 3)
        {
            lay_bottom_row.setVisibility(View.GONE);
            
            tv_unit_one.setText(subUnitsArray.get(0).getTitle());
            tv_unit_two.setText(subUnitsArray.get(1).getTitle());
            tv_unit_three.setText(subUnitsArray.get(2).getTitle());
    
        }
        
        else if(counterCount == 4)
        {
            btn_two.setVisibility(View.GONE);
            btn_five.setVisibility(View.GONE);
    
           /* final float scale = getResources().getDisplayMetrics().density;
            int dpWidthInPx  = (int) (120 * scale);
            int dpHeightInPx = (int) (120 * scale);
            
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    dpWidthInPx, dpHeightInPx);
            layoutParams.setMargins(130, 0, 30, 20);
            btn_one.setLayoutParams(layoutParams);*/
            
        }
        
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
    
            case R.id.btn_pause:
                if(!isPause)
                {
                    tv_pauseresume.setText("Resume");
                    isPause = true;
                }
                else
                {
                    tv_pauseresume.setText("Pause");
                    isPause = false;
                }
                
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
         LAT_Current = location.getLatitude() + "";
         LNG_Current = location.getLongitude() + "";
    
        if(!isPause)
            userPathPointsArray.add(new Point(location.getLatitude()+ "" , location.getLongitude() + ""));
        
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
                Log.w(AppConstants.TAG , "LAT on done: " + LAT_Current);
                Log.w(AppConstants.TAG , "LNG on done: " + LNG_Current);
                
                String text = et_note.getText().toString();
                String time = AppConstants.getISOCurrentTime();
                String lat = LAT_Current;
                String lng = LNG_Current;
    
                Note note = new Note(text , time , lat , lng);
                notesArray.add(note);
                
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
                JSONObject postParams = getPostParams();
                Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
                SubmitSurvey(postParams);
                
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
        optionsGREEN.add(point);
    
        MarkerOptions markerOptions = new MarkerOptions().position(point).title("");
        
        try
        {
            if(!markerAdded)
            {
                mMarker = map.addMarker(markerOptions);
                markerAdded = true;
            }
            else
            {
                mMarker.setPosition(point);
            }
        }
        catch (ClassCastException e)
        {
            Log.e(AppConstants.TAG , "ClassCastException: " + e.toString());
        }
       
       /* else
            mMarker.setPosition(point);*/
    
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(point);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(17);
        map.moveCamera(center);
        map.animateCamera(zoom);
        
        map.addPolyline(optionsGREEN);
    }
    
    public void drawCircuit(ArrayList<Point> pointsArrayList)
    {
        for(int i=0; i< pointsArrayList.size(); i++)
        {
            LatLng latlng = new LatLng(Double.parseDouble(pointsArrayList.get(i).getLAT()) , Double.parseDouble(pointsArrayList.get(i).getLNG()));
            optionsBLACK.add(latlng);
        }
        
        if(pointsArrayList.size() != 0)
        {
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(pointsArrayList.get(0).getLAT()) , Double.parseDouble(pointsArrayList.get(0).getLNG())));
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(13);
            map.moveCamera(center);
            map.animateCamera(zoom);
            
            map.addPolyline(optionsBLACK);
        }
        
    }
    
    
    public void setupUI(View view) {
        
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    
                    //  Log.v(Constants.TAG , "onTouch");
                   /* top_ic_layout.setVisibility(View.VISIBLE);
                    or_layout.setVisibility(View.VISIBLE);
                    scan_barcode.setVisibility(View.VISIBLE);
                    top_ic_layout_openkey.setVisibility(View.GONE);*/
                    
                    
                    hideSoftKeyboard(CircuitEstimationActivity.this);
                    
                    return false;
                }
            });
        }
        
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                
                View innerView = ((ViewGroup) view).getChildAt(i);
                
                //   Log.v(Constants.TAG , "view instanceof ViewGroup");
                
                setupUI(innerView);
            }
        }
    }
    
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    
    public JSONObject getPostParams()
    {
        JSONObject obj = new JSONObject();
        JSONObject credentialsObj = new JSONObject();
        
        try
        {
            credentialsObj.put("CircuitId" , Integer.parseInt(CircuitID));
            credentialsObj.put("UserId" , mPrefs.getString(AppConstants.USER_ID , ""));
            credentialsObj.put("SubmitTime" , AppConstants.getISOCurrentTime());
            credentialsObj.put("StartTime" , startTime);
            credentialsObj.put("EndTime" , AppConstants.getISOCurrentTime());
            credentialsObj.put("SyncTime" , AppConstants.getISOCurrentTime());
            credentialsObj.put("Milage" , 2);
            
            JSONArray notesJSONArray = new JSONArray();
            
            for(int i=0; i<notesArray.size(); i++)
            {
                JSONObject notesObj = new JSONObject();
                notesObj.put("Text" ,notesArray.get(i).getText());
                notesObj.put("Time" ,notesArray.get(i).getTime());
                notesObj.put("lat" , Double.parseDouble(notesArray.get(i).getLat()));
                notesObj.put("lng" , Double.parseDouble(notesArray.get(i).getLat()));
    
                notesJSONArray.put(notesObj);
    
            }
    
            credentialsObj.put("Notes" , notesJSONArray);
    
            JSONArray pathJSONArray = new JSONArray();
            
            for(int j=0; j<userPathPointsArray.size(); j++)
            {
                JSONObject pointObj = new JSONObject();
                pointObj.put("lat" , Double.parseDouble(userPathPointsArray.get(j).getLAT()));
                pointObj.put("lng" , Double.parseDouble(userPathPointsArray.get(j).getLNG()));
                pathJSONArray.put(pointObj);
            }
    
            credentialsObj.put("Path" , pathJSONArray);
    
            JSONArray counterPointsJSONArray = new JSONArray();
    
            for(int j=0; j<counterPointsArray.size(); j++)
            {
                JSONObject pointObj = new JSONObject();
                pointObj.put("bidSubunitId" , Integer.parseInt(counterPointsArray.get(j).getBidSubunitID()));
                pointObj.put("weight" , Integer.parseInt(counterPointsArray.get(j).getWeight()));
                pointObj.put("lat" , Double.parseDouble(counterPointsArray.get(j).getLat()));
                pointObj.put("lng" , Double.parseDouble(counterPointsArray.get(j).getLng()));
                counterPointsJSONArray.put(pointObj);
            }
    
            credentialsObj.put("Points" , counterPointsJSONArray);
            
        }
        catch (JSONException e)
        {
            
        }
        
        return  credentialsObj;
    }
    
    private void SubmitSurvey(final JSONObject postParams)
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.submitting), true);
    
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      AppConstants.BASE_URL + "addSurveyData"
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                    
                                                              Log.d(AppConstants.TAG , "SubmitSurvey response: " + response);
                    
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
                        
                                                                  boolean success = jsonObj.getBoolean("success");
                                                                  String message = jsonObj.getString("message");
                        
                                                                  if(!success)
                                                                  {
                                                                      Toast.makeText(CircuitEstimationActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                  {
                                                                      Toast.makeText(CircuitEstimationActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                      
                                                                      Intent intent = new Intent(CircuitEstimationActivity.this, CircuitsActivity.class);
                                                                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                      startActivity(intent);
                                                                      finish();
                                                                  }
                                                              }
                                                              catch (JSONException e)
                                                              {
                        
                                                              }
                    
                                                              dialog.dismiss();
                    
                                                          }
                                                      }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(AppConstants.TAG , "onErrorResponse: " + error.toString());
            
                dialog.dismiss();
                String json = null;
            
                NetworkResponse response = error.networkResponse;
                Log.e(AppConstants.TAG , "response.statusCode: " + response.statusCode);
            
            }
        
        }) {
        
            @Override
            public byte[] getBody()  {
                //  String str = "{\"login\":\""+login+"\",\"password\":\""+pass+"\"}";
                return postParams.toString().getBytes();
            }
        
            public String getBodyContentType()
            {
                return "application/json";
            }
        };
    
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    
        mRequestQueue.add(postRequest);
    }
    
    
}
