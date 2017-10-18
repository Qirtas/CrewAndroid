package asplundh.sps.com.asplundhproductivity.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

/**
 * Created by Malik Muhamad Qirtas on 9/12/2017.
 */

public class AppConstants
{
    public static final String TAG = "AsplundhProductivity";
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
}
