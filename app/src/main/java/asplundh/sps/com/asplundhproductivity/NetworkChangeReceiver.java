package asplundh.sps.com.asplundhproductivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/5/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(AppConstants.TAG, "onReceive");
    }
    
    
}
