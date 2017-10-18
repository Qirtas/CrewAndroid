package asplundh.sps.com.asplundhproductivity.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;
import io.fabric.sdk.android.Fabric;

import static asplundh.sps.com.asplundhproductivity.Activity.LocationActivity.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static asplundh.sps.com.asplundhproductivity.Activity.LocationActivity.UPDATE_INTERVAL_IN_MILLISECONDS;
import static asplundh.sps.com.asplundhproductivity.Activity.LocationActivity.isPlayServicesAvailable;


public class HomeActivity extends AppCompatActivity implements OnMenuItemClickListener, View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    
    Context mContext;
    EditText et_counter_one , et_counter_two , et_counter_three;
    static int counter_one = 0 , counter_two = 0 , counter_three = 0;
    ImageView iv_hours_radio , iv_units_radio , iv_distance_radio;
    String selectedRadioCounter1 = "Hours" , selectedRadioCounter2 = "Units" ,  selectedRadioCounter3 = "Distance";
    TextView tv_counter_one_title , tv_counter_two_title , tv_counter_three_title;
    static int hours=0 , units=0 , distance=0;
    static String counter1 = "Hours" ,counter2 = "Units"  ,counter3 = "Distance" ;
    
    String hours_selected_one = "Bucket" , units_selected_one = "" , distance_selected_one = "";
    String hours_selected_two = "" , units_selected_two = "" , distance_selected_two = "";
    String hours_selected_three = "" , units_selected_three = "" , distance_selected_three = "";
    
    Vibrator vibrator;
    MediaPlayer mp;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationManager locationManager;
    boolean isLocationEnabled = false;
    public static ArrayList<LatLng> points;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);
        setupUI(findViewById(R.id.home));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        points = new ArrayList<LatLng>();
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        mRequestingLocationUpdates = false;
        buildGoogleApiClient();
        
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(this, R.raw.beep1);
        
        mContext = HomeActivity.this;
        et_counter_one = (EditText) findViewById(R.id.et_counter_one);
        et_counter_one.setCursorVisible(false);
        et_counter_one.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
            }
        });
        
        LinearLayout btn_backward = (LinearLayout) findViewById(R.id.btn_backward);
        btn_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        et_counter_two = (EditText) findViewById(R.id.et_counter_two);
        et_counter_two.setCursorVisible(false);
        et_counter_three = (EditText) findViewById(R.id.et_counter_three);
        et_counter_three.setCursorVisible(false);
        
        tv_counter_one_title = (TextView) findViewById(R.id.tv_counter_one_title);
        tv_counter_two_title = (TextView) findViewById(R.id.tv_counter_two_title);
        tv_counter_three_title = (TextView) findViewById(R.id.tv_counter_three_title);
        
        ImageView btn_menu = (ImageView) findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
                popupMenu.setOnMenuItemClickListener(HomeActivity.this);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
            }
        });
        
        ImageView iv_counter_one_settings = (ImageView) findViewById(R.id.iv_counter_one_settings);
        iv_counter_one_settings.setOnClickListener(this);
        ImageView iv_counter_two_settings = (ImageView) findViewById(R.id.iv_counter_two_settings);
        iv_counter_two_settings.setOnClickListener(this);
        ImageView iv_counter_three_settings = (ImageView) findViewById(R.id.iv_counter_three_settings);
        iv_counter_three_settings.setOnClickListener(this);
        
        RelativeLayout lay_counter_one = (RelativeLayout) findViewById(R.id.lay_counter_one);
        lay_counter_one.setOnClickListener(this);
        RelativeLayout lay_counter_inc_two = (RelativeLayout) findViewById(R.id.lay_counter_inc_two);
        lay_counter_inc_two.setOnClickListener(this);
        RelativeLayout lay_counter_inc_three = (RelativeLayout) findViewById(R.id.lay_counter_inc_three);
        lay_counter_inc_three.setOnClickListener(this);
        
        RelativeLayout btn_apply_to_area = (RelativeLayout) findViewById(R.id.btn_apply_to_area);
        btn_apply_to_area.setOnClickListener(this);
        
    }
    
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_maps:
                if(AppConstants.isNetworkAvailable(HomeActivity.this))
                {
                    Intent i = new Intent(HomeActivity.this , LocationActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(HomeActivity.this , "Network not available!",
                                   Toast.LENGTH_LONG).show();
                }
                
                return true;
            
        }
        return true;
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case R.id.iv_counter_one_settings:
                
                final Dialog dialog_counter_one = new Dialog(mContext);
                dialog_counter_one.setContentView(R.layout.dialog_counter_one);
                dialog_counter_one.setCanceledOnTouchOutside(false);
                dialog_counter_one.setTitle("Title...");
    
                // set the custom dialog components - text, image and button
                
                final ImageView iv_hours_radio = (ImageView) dialog_counter_one.findViewById(R.id.iv_hours_radio);
                final ImageView iv_units_radio = (ImageView) dialog_counter_one.findViewById(R.id.iv_units_radio);
                final ImageView iv_distance_radio = (ImageView) dialog_counter_one.findViewById(R.id.iv_distance_radio);
    
                Spinner spinner_hours_one = (Spinner) dialog_counter_one.findViewById(R.id.spinner_hours);
                Spinner spinner_units_one = (Spinner) dialog_counter_one.findViewById(R.id.spinner_units);
                Spinner spinner_distance_one = (Spinner) dialog_counter_one.findViewById(R.id.spinner_distance);
                
                if(hours_selected_one.equalsIgnoreCase("Bucket"))
                    spinner_hours_one.setSelection(0);
                else if(hours_selected_one.equalsIgnoreCase("Manual"))
                    spinner_hours_one.setSelection(1);
                else if(hours_selected_one.equalsIgnoreCase("Mow"))
                    spinner_hours_one.setSelection(2);
    
                if(units_selected_one.equalsIgnoreCase("Spans"))
                    spinner_units_one.setSelection(0);
                else if(units_selected_one.equalsIgnoreCase("Trees"))
                    spinner_units_one.setSelection(1);
                else if(units_selected_one.equalsIgnoreCase("Trims"))
                    spinner_units_one.setSelection(2);
                else if(units_selected_one.equalsIgnoreCase("Removals"))
                    spinner_units_one.setSelection(3);
    
                if(distance_selected_one.equalsIgnoreCase("Feet"))
                    spinner_distance_one.setSelection(0);
                else if(distance_selected_one.equalsIgnoreCase("Miles"))
                    spinner_distance_one.setSelection(1);
    
                spinner_hours_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        hours_selected_one = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "hours_selected_one: " + hours_selected_one);
                    }
    
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
        
                    }
                });
    
                spinner_units_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        units_selected_one = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "units_selected_one: " + units_selected_one);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                spinner_distance_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        distance_selected_one = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "distance_selected_one: " + distance_selected_one);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
                
                iv_hours_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_units_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
            
                        selectedRadioCounter1 = "Hours";
                    }
                });
    
                iv_units_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_distance_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        selectedRadioCounter1 = "Units";
                    }
                });
    
                iv_distance_radio.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        selectedRadioCounter1 = "Distance";
                    }
                });
    
                if(selectedRadioCounter1.equalsIgnoreCase("Hours"))
                {
                    iv_hours_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_units_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
        
                }
                else if(selectedRadioCounter1.equalsIgnoreCase("Units"))
                {
                    iv_hours_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_distance_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                }
                else if(selectedRadioCounter1.equalsIgnoreCase("Distance"))
                {
                    iv_hours_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                }
    
                RelativeLayout btn_cancel  = (RelativeLayout) dialog_counter_one.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog_counter_one.dismiss();
                    }
                });
    
                RelativeLayout btn_save  = (RelativeLayout) dialog_counter_one.findViewById(R.id.btn_save);
                btn_save.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.v(AppConstants.TAG , "selectedRadio for counter1: " + selectedRadioCounter1);
                        Log.w(AppConstants.TAG , "hours_selected_one for counter1: " + hours_selected_one);
                        
                        if(selectedRadioCounter1.equalsIgnoreCase("Hours"))
                            tv_counter_one_title.setText(selectedRadioCounter1 + "(" + hours_selected_one + ")");
                        else if(selectedRadioCounter1.equalsIgnoreCase("Units"))
                            tv_counter_one_title.setText(selectedRadioCounter1 + "(" + units_selected_one + ")");
                        else if(selectedRadioCounter1.equalsIgnoreCase("Distance"))
                            tv_counter_one_title.setText(selectedRadioCounter1 + "(" + distance_selected_one + ")");
    
                        counter1 = selectedRadioCounter1;
                        dialog_counter_one.cancel();
                    }
                });
    
                dialog_counter_one.show();
                
                break;
    
            case R.id.iv_counter_two_settings:
    
                final Dialog dialog_counter_two = new Dialog(mContext);
                dialog_counter_two.setContentView(R.layout.dialog_counter_one);
                dialog_counter_two.setCanceledOnTouchOutside(false);
                dialog_counter_two.setTitle("Title...");
    
                final ImageView iv_hours_radio_counter_two = (ImageView) dialog_counter_two.findViewById(R.id.iv_hours_radio);
                final ImageView iv_units_radio_two = (ImageView) dialog_counter_two.findViewById(R.id.iv_units_radio);
                final ImageView iv_distance_radio_two = (ImageView) dialog_counter_two.findViewById(R.id.iv_distance_radio);
                
                Spinner spinner_hours_two = (Spinner) dialog_counter_two.findViewById(R.id.spinner_hours);
                Spinner spinner_units_two = (Spinner) dialog_counter_two.findViewById(R.id.spinner_units);
                Spinner spinner_distance_two = (Spinner) dialog_counter_two.findViewById(R.id.spinner_distance);
    
                if(hours_selected_two.equalsIgnoreCase("Bucket"))
                    spinner_hours_two.setSelection(0);
                else if(hours_selected_two.equalsIgnoreCase("Manual"))
                    spinner_hours_two.setSelection(1);
                else if(hours_selected_two.equalsIgnoreCase("Mow"))
                    spinner_hours_two.setSelection(2);
    
                if(units_selected_two.equalsIgnoreCase("Spans"))
                    spinner_units_two.setSelection(0);
                else if(units_selected_two.equalsIgnoreCase("Trees"))
                    spinner_units_two.setSelection(1);
                else if(units_selected_two.equalsIgnoreCase("Trims"))
                    spinner_units_two.setSelection(2);
                else if(units_selected_two.equalsIgnoreCase("Removals"))
                    spinner_units_two.setSelection(3);
    
                if(distance_selected_two.equalsIgnoreCase("Feet"))
                    spinner_distance_two.setSelection(0);
                else if(distance_selected_two.equalsIgnoreCase("Miles"))
                    spinner_distance_two.setSelection(1);
                
                spinner_hours_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        hours_selected_two = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "hours_selected_two: " + hours_selected_two);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                spinner_units_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        units_selected_two = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "units_selected_two: " + units_selected_two);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                spinner_distance_two.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        distance_selected_two = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "distance_selected_two: " + distance_selected_two);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                iv_hours_radio_counter_two.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_two.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_units_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
            
                        selectedRadioCounter2 = "Hours";
                    }
                });
    
                iv_units_radio_two.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_distance_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        selectedRadioCounter2 = "Units";
                    }
                });
    
                iv_distance_radio_two.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio_two.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        selectedRadioCounter2 = "Distance";
                    }
                });
    
                if(selectedRadioCounter2.equalsIgnoreCase("Hours"))
                {
                    iv_hours_radio_counter_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_units_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
        
                }
                else if(selectedRadioCounter2.equalsIgnoreCase("Units"))
                {
                    iv_hours_radio_counter_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_distance_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                }
                else if(selectedRadioCounter2.equalsIgnoreCase("Distance"))
                {
                    iv_hours_radio_counter_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio_two.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                }
    
                RelativeLayout btn_cancel_two  = (RelativeLayout) dialog_counter_two.findViewById(R.id.btn_cancel);
                btn_cancel_two.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog_counter_two.dismiss();
                    }
                });
    
                RelativeLayout btn_save_two  = (RelativeLayout) dialog_counter_two.findViewById(R.id.btn_save);
                btn_save_two.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.v(AppConstants.TAG , "selectedRadio for counter2: " + selectedRadioCounter2);
                        
                        if(selectedRadioCounter2.equalsIgnoreCase("Hours"))
                            tv_counter_two_title.setText(selectedRadioCounter2 + "(" + hours_selected_two + ")");
                        else if(selectedRadioCounter2.equalsIgnoreCase("Units"))
                            tv_counter_two_title.setText(selectedRadioCounter2 + "(" + units_selected_two + ")");
                        else if(selectedRadioCounter2.equalsIgnoreCase("Distance"))
                            tv_counter_two_title.setText(selectedRadioCounter2 + "(" + distance_selected_two + ")");
                        
                        counter2 = selectedRadioCounter2;
                        
                        dialog_counter_two.cancel();
                    }
                });
    
                dialog_counter_two.show();
                
                break;
    
            case R.id.iv_counter_three_settings:
    
                final Dialog dialog_counter_three = new Dialog(mContext);
                dialog_counter_three.setContentView(R.layout.dialog_counter_one);
                dialog_counter_three.setCanceledOnTouchOutside(false);
                dialog_counter_three.setTitle("Title...");
    
                final ImageView iv_hours_radio_counter_three = (ImageView) dialog_counter_three.findViewById(R.id.iv_hours_radio);
                final ImageView iv_units_radio_three = (ImageView) dialog_counter_three.findViewById(R.id.iv_units_radio);
                final ImageView iv_distance_radio_three = (ImageView) dialog_counter_three.findViewById(R.id.iv_distance_radio);
    
                Spinner spinner_hours_three = (Spinner) dialog_counter_three.findViewById(R.id.spinner_hours);
                Spinner spinner_units_three = (Spinner) dialog_counter_three.findViewById(R.id.spinner_units);
                Spinner spinner_distance_three = (Spinner) dialog_counter_three.findViewById(R.id.spinner_distance);
    
                if(hours_selected_three.equalsIgnoreCase("Bucket"))
                    spinner_hours_three.setSelection(0);
                else if(hours_selected_three.equalsIgnoreCase("Manual"))
                    spinner_hours_three.setSelection(1);
                else if(hours_selected_three.equalsIgnoreCase("Mow"))
                    spinner_hours_three.setSelection(2);
    
                if(units_selected_three.equalsIgnoreCase("Spans"))
                    spinner_units_three.setSelection(0);
                else if(units_selected_three.equalsIgnoreCase("Trees"))
                    spinner_units_three.setSelection(1);
                else if(units_selected_three.equalsIgnoreCase("Trims"))
                    spinner_units_three.setSelection(2);
                else if(units_selected_three.equalsIgnoreCase("Removals"))
                    spinner_units_three.setSelection(3);
    
                if(distance_selected_three.equalsIgnoreCase("Feet"))
                    spinner_distance_three.setSelection(0);
                else if(distance_selected_three.equalsIgnoreCase("Miles"))
                    spinner_distance_three.setSelection(1);
                
                spinner_hours_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        hours_selected_three = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "hours_selected_three: " + hours_selected_three);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                spinner_units_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        units_selected_three = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "units_selected_three: " + units_selected_three);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
    
                spinner_distance_three.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        distance_selected_three = parent.getItemAtPosition(position).toString();
                        Log.i(AppConstants.TAG , "distance_selected_three: " + distance_selected_three);
                    }
        
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
            
                    }
                });
                
                iv_hours_radio_counter_three.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_three.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_units_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
            
                        selectedRadioCounter3 = "Hours";
                    }
                });
    
                iv_units_radio_three.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        iv_distance_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        selectedRadioCounter3 = "Units";
                    }
                });
    
                iv_distance_radio_three.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        iv_hours_radio_counter_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_units_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_off));
                        iv_distance_radio_three.setBackground(getResources().getDrawable(R.drawable.radio_on));
                        selectedRadioCounter3 = "Distance";
                    }
                });
    
                if(selectedRadioCounter3.equalsIgnoreCase("Hours"))
                {
                    iv_hours_radio_counter_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_units_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
        
                }
                else if(selectedRadioCounter3.equalsIgnoreCase("Units"))
                {
                    iv_hours_radio_counter_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                    iv_distance_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                }
                else if(selectedRadioCounter3.equalsIgnoreCase("Distance"))
                {
                    iv_hours_radio_counter_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_units_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_off));
                    iv_distance_radio_three.setBackgroundDrawable(getResources().getDrawable(R.drawable.radio_on));
                }
    
                RelativeLayout btn_cancel_three  = (RelativeLayout) dialog_counter_three.findViewById(R.id.btn_cancel);
                btn_cancel_three.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog_counter_three.dismiss();
                    }
                });
    
                RelativeLayout btn_save_three  = (RelativeLayout) dialog_counter_three.findViewById(R.id.btn_save);
                btn_save_three.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.v(AppConstants.TAG , "selectedRadio for counter3: " + selectedRadioCounter3);
                        
                        if(selectedRadioCounter3.equalsIgnoreCase("Hours"))
                            tv_counter_three_title.setText(selectedRadioCounter3 + "(" + hours_selected_three + ")");
                        else if(selectedRadioCounter3.equalsIgnoreCase("Units"))
                            tv_counter_three_title.setText(selectedRadioCounter3 + "(" + units_selected_three + ")");
                        else if(selectedRadioCounter3.equalsIgnoreCase("Distance"))
                            tv_counter_three_title.setText(selectedRadioCounter3 + "(" + distance_selected_three + ")");
                        
                        
                        counter3 = selectedRadioCounter3;
                        dialog_counter_three.cancel();
                    }
                });
    
                dialog_counter_three.show();
                
                break;
            
            case R.id.lay_counter_one:
                vibrator.vibrate(200);
                mp.start();
    
                String countervalueOne = et_counter_one.getText().toString();
                counter_one = Integer.parseInt(countervalueOne);
                counter_one++;
                et_counter_one.setText(counter_one + "");
                break;
    
            case R.id.lay_counter_inc_two:
                vibrator.vibrate(200);
                mp.start();
                String countervalueTwo = et_counter_two.getText().toString();
                counter_two = Integer.parseInt(countervalueTwo);
                counter_two++;
                et_counter_two.setText(counter_two + "");
                break;
    
            case R.id.lay_counter_inc_three:
                vibrator.vibrate(200);
                mp.start();
                
                String countervalueThree = et_counter_three.getText().toString();
                counter_three = Integer.parseInt(countervalueThree);
                counter_three++;
                et_counter_three.setText(counter_three + "");
                break;
    
            case R.id.btn_apply_to_area:
             if(AppConstants.isNetworkAvailable(HomeActivity.this))
             {
                
                 counter_one = Integer.parseInt(et_counter_one.getText().toString());
                 counter_two = Integer.parseInt(et_counter_two.getText().toString());
                 counter_three = Integer.parseInt(et_counter_three.getText().toString());
                 
                 Intent i = new Intent(HomeActivity.this , LocationActivity.class);
                 startActivity(i);
             }
             else
             {
                Toast.makeText(HomeActivity.this , "Network not available!",
                               Toast.LENGTH_LONG).show();
             }
             
             break;
        }
    }
    
    public void setupUI(View view) {
        
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    
                    hideSoftKeyboard(HomeActivity.this);
                    
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
    
    protected synchronized void buildGoogleApiClient() {
        Log.i(AppConstants.TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    
    public void startUpdatesButtonHandler()
    {
    
        if (!isPlayServicesAvailable(this)) return;
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
        } else {
            // return;
        }
    
        startLocationUpdates();
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
    
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    
    private void startLocationUpdates() {
        Log.v(AppConstants.TAG, "startLocationUpdates");
    
        if(isLocationEnabled())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, HomeActivity.this);
        }
        else
        {
            showLocationAlert();
            Log.e(AppConstants.TAG , "LOC SETTINSG NOT ENABLED");
        }
    }
    
    @Override
    public void onLocationChanged(Location location)
    {
      //  Log.i(AppConstants.TAG, "onLocationChanged");
        mCurrentLocation = location;
        
      //  Log.v(AppConstants.TAG , "LAT: " + location.getLatitude());
    
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        points.add(latLng);
    
      //  Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
        
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
            
        }
        startUpdatesButtonHandler();
        if (mRequestingLocationUpdates) {
          //  startLocationUpdates();
            
        }
    }
    
    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(AppConstants.TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.i(AppConstants.TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    
    }
    
    @Override
    public void onResume() {
        super.onResume();
        isPlayServicesAvailable(this);
    
        if(isLocationEnabled())
        {
            isLocationEnabled = true;
        }
        
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        
       /* if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
               startLocationUpdates();
        }*/
    }
    
    private boolean isLocationEnabled()
    {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    private void showLocationAlert()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_location);
        dialog.setTitle("");
        LinearLayout main = (LinearLayout) dialog.findViewById(R.id.maindialog);
        main.setBackgroundColor(Color.TRANSPARENT);
        
        LinearLayout btn_cancel = (LinearLayout) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                finish();
                
            }
        });
        
        LinearLayout btn_locationsettings = (LinearLayout) dialog.findViewById(R.id.btn_locationsettings);
        btn_locationsettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(AppConstants.TAG , "STARTING LOCATION SETTINGS");
                
                
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //startActivity(myIntent);
                startActivityForResult(myIntent , 000);
                
                dialog.dismiss();
            }
        });
        
        TextView tv_settings = (TextView) dialog.findViewById(R.id.tv_settings);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        
        // set the custom dialog components - text, image and button
        dialog.show();
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            super.onActivityResult(requestCode, resultCode, data);
        
            if(requestCode == 000) // For location dialog
            {
                Log.v(AppConstants.TAG , "requestCode == 000");
            
                if(isLocationEnabled())
                {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, HomeActivity.this);
                }
                else
                {
                    finish();
                }
                        
            }
            
        } catch (Exception ex) {
            Log.e(AppConstants.TAG , "Exception " + ex.toString());
        }
    
    }
    
    public void addSpinnerHistory()
    {
        Spinner hours_spinner_one_counter = (Spinner) findViewById(R.id.spinner_hours);
        
        if(selectedRadioCounter1.equalsIgnoreCase("Hours"))
            hours_spinner_one_counter.setSelection(0);
        else if(selectedRadioCounter1.equalsIgnoreCase("Units"))
            hours_spinner_one_counter.setSelection(1);
        else if(selectedRadioCounter1.equalsIgnoreCase("Distance"))
            hours_spinner_one_counter.setSelection(2);
        
        final Spinner units_spinner_one_counter = (Spinner) findViewById(R.id.spinner_units);
        
        if(selectedRadioCounter2.equalsIgnoreCase("Hours"))
            units_spinner_one_counter.setSelection(0);
        else if(selectedRadioCounter2.equalsIgnoreCase("Units"))
            units_spinner_one_counter.setSelection(1);
        else if(selectedRadioCounter2.equalsIgnoreCase("Distance"))
            units_spinner_one_counter.setSelection(2);
        else if(selectedRadioCounter2.equalsIgnoreCase("Feet"))
            units_spinner_one_counter.setSelection(3);
        
         final Spinner distance_spinner_one_counter = (Spinner) findViewById(R.id.spinner_distance);
         if(selectedRadioCounter3.equalsIgnoreCase("Hours"))
             distance_spinner_one_counter.setSelection(0);
         else if(selectedRadioCounter2.equalsIgnoreCase("Units"))
             distance_spinner_one_counter.setSelection(1);
        else if(selectedRadioCounter2.equalsIgnoreCase("Distance"))
             distance_spinner_one_counter.setSelection(2);
        
        
        distance_spinner_one_counter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0)
                    units_selected_three = "Hours";
                else if(position == 1)
                    units_selected_two = "Units";
                else if(position == 3)
                    units_selected_two = "Distance";
                
            }
        });
        
        units_spinner_one_counter.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0)
                    distance_selected_one = "Hours";
                else if(position == 1)
                    distance_selected_one = "Units";
                else if(position == 2)
                    distance_selected_one = "Distance";
            }
        });
        
    }
}
