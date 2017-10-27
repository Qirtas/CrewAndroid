package asplundh.sps.com.asplundhproductivity.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Malik Muhamad Qirtas on 9/12/2017.
 */

public class AppConstants
{
    public static final String TAG = "AsplundhProductivity";
    
    public static final String PACKAGE_NAME =
            "com.sample.foo.simplelocationapp";
    
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final int USE_ADDRESS_NAME = 1;
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String BASE_URL = "http://apabackend.mybluemix.net/mobile/";
    
    public static final String USER_ID = "user_id";
    public static final String BID_PLAN_ID = "bid_plan_id";
    public static final String CIRCUIT_TYPES = "circuit_types";
    
    
    public static final String EMP_NAME = "empName";
    public static final String EMP_ID = "employeeId";
    public static final String EMP_PIN = "pin";
    
    public static final String CITY_NAME = "cityName";
    
    
    public static  String BIDPLANS_JSONARRAY = "";
    
    public static final String NORTH_EAST = "north_east";
    public static final String SOUTH_WEST = "south_west";
    public static final String CITY_LOCATION = "city_loc";
    public static final String SUB_UNITS = "sub_units";
    public static final String UNIT = "unit";
    
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    
    public static String getISOCurrentTime()
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        Log.i(AppConstants.TAG , "nowAsISO: " + nowAsISO);
        
        return nowAsISO;
    }
    
    public static LatLng reverseGeocoding(Context context, String locationName){
        if(!Geocoder.isPresent()){
            Log.w(AppConstants.TAG , "Geocoder implementation not present !");
        }
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        
        try {
            List<Address> addresses = geoCoder.getFromLocationName(locationName, 5);
            int tentatives = 0;
            while (addresses.size()==0 && (tentatives < 10)) {
                addresses = geoCoder.getFromLocationName("<address goes here>", 1);
                tentatives ++;
            }
            
            if(addresses.size() > 0)
            {
                for(int i=0; i<addresses.size(); i++)
                {
                    Log.v(AppConstants.TAG , "Address:  " + addresses.get(i).getLatitude());
                    Log.v(AppConstants.TAG , "Address:  " + addresses.get(i).getLongitude());
                }
                
                Log.d(AppConstants.TAG , "reverse Geocoding : locationName " + locationName + "Latitude " + addresses.get(0).getLatitude() );
                return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }else{
                //use http api
            }
            
        } catch (IOException e) {
            Log.e(AppConstants.TAG, "not possible finding LatLng for Address : " + locationName);
        }
        return null;
    }
    String LATLNGS = "";
    
    
    public float getDistanceBwCoordinates(LatLng latlng1, LatLng latlng2)
    {
        Location loc1 = new Location("");
        loc1.setLatitude(latlng1.latitude);
        loc1.setLongitude(latlng1.longitude);
        
        Location loc2 = new Location("");
        loc2.setLatitude(latlng2.latitude);
        loc2.setLongitude(latlng2.longitude);
        
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters;
    }
    
    
   
    
}
