package asplundh.sps.com.asplundhproductivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/5/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver
{
    DBController mDB;
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(AppConstants.TAG, "onReceive");
    
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    
        final boolean isWifiConn = wifiNetworkInfo.isConnected();
        final boolean isMobileConn = mobileNetworkInfo.isConnected();
    
        /*if(isWifiConn || isMobileConn)
        {
            mDB = new DBController(context);
            
            //new circuits
            
            Cursor cursor = mDB.getAllData(DBController.DB_TABLE_NEW_CIRCUIT);
            Log.v(AppConstants.TAG, "cursor count for new circuits in DB: " + cursor.getCount());
               
            if (cursor.moveToFirst())
            {
                do
                {
                    String circuitID = cursor.getString(cursor.getColumnIndex(DBController.KEY_CIRCUIT_ID));
                    String post_params = cursor.getString(cursor.getColumnIndex(DBController.KEY_NEW_CIRCUIT_POST_PARAMS));
                    Log.v(AppConstants.TAG, "post_params: " + post_params);
            
                    try
                    {
                        JSONObject postParams = new JSONObject(post_params);
                        syncNewCircuitsWirhBackend(postParams , context , circuitID);
                    }
                    catch (JSONException e){}
            
                }
                while (cursor.moveToNext());
            }
            
            //new surveys
            
            Cursor cursor_surveys = mDB.getAllData(DBController.DB_TABLE_SURVEY);
            Log.i(AppConstants.TAG, "cursor count for new surveys in DB: " + cursor_surveys.getCount());
            
            if (cursor_surveys.moveToFirst())
            {
                do
                {
                    String post_params = cursor_surveys.getString(cursor_surveys.getColumnIndex(DBController.KEY_SURVEY_POSTPARAMS));
                    Log.v(AppConstants.TAG, "post_params survey: " + post_params);
            
                    try
                    {
                        JSONObject postParams = new JSONObject(post_params);
                        syncNewCircuitsWirhBackend(postParams , context);
                    }
                    catch (JSONException e){}
            
                }
                while (cursor.moveToNext());
            }
        }*/
        
    }
    
    private void syncNewCircuitsWirhBackend(final JSONObject postParams , Context context , final String circuitID)
    {
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(context).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      AppConstants.BASE_URL + "addCircuit"
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                        
                                                              Log.d(AppConstants.TAG , "getCircuits response: " + response);
                        
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
                            
                                                                  boolean success = jsonObj.getBoolean("success");
                                                                  String message = jsonObj.getString("message");
                            
                                                                  if(success)
                                                                  {
                                                                      JSONArray result = jsonObj.getJSONArray("result");
                                                                      JSONObject obj = result.getJSONObject(0);
                                                                      String circuit_ID_new = obj.opt("Id").toString();
                                                                      
                                                                      Log.w(AppConstants.TAG , "OLD CIRCUIT ID: " +  circuitID);
                                                                      boolean isSurveyExists = mDB.isSurveyForNewCircuitRecordExists(circuitID);
                                                                      
                                                                      //Updating survey table's circuits ID
                                                                      
                                                                      if(isSurveyExists)
                                                                      {
                                                                          mDB.updateCircuitIDInSurveyTable(circuitID , circuit_ID_new);
                                                                         // syncSurvey();
                                                                      }
                                                                      else
                                                                      {
                                                                          
                                                                      }
                                                                        
                                                                      
                                                                      mDB.deleteCircuitRecord(circuitID);
                                                                  }
                                                                  
                                                                  Cursor c = mDB.getAllData(DBController.DB_TABLE_NEW_CIRCUIT);
                                                                  Log.w(AppConstants.TAG , "new circuit size: " + c.getCount());
                                                                  
                                                              }
                                                              catch (JSONException e)
                                                              {
                            
                                                              }
                                                              
                                                          }
                                                      }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(AppConstants.TAG , "onErrorResponse: " + error.toString());
                
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
    
    private void syncSurvey(Context context , final String circuitID)
    {
        
    }
    
}
