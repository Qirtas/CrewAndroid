package asplundh.sps.com.asplundhproductivity.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 9/12/2017.
 */


public class GeocodeAddressIntentService extends IntentService
{
    protected ResultReceiver resultReceiver;
    private static final String TAG = "GEO_ADDY_SERVICE";
    
    public GeocodeAddressIntentService() {
        super("GeocodeAddressIntentService");
    }
    
    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        String City_name = intent.getStringExtra(AppConstants.CITY_NAME);
        getGeocodeUpdated(City_name);
        
        resultReceiver = intent.getParcelableExtra(AppConstants.RECEIVER);
    }
    
    public void getGeocodeUpdated(String cityName)
    {
        // Log.v(AppConstants.TAG, "URL " + "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + lng + "&key=AIzaSyB1kTYkURwfYKN8hgUQh2OodkYGfPdrlMg");
        String address = "";
        //AIzaSyCsjHNjJnWvCLxQMYlq9BnIQCy5MO8bIXY
        
        Log.v(AppConstants.TAG , "getGeocodeUpdated: " + cityName);
        
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      "https://maps.googleapis.com/maps/api/geocode/json?address=" + cityName +"&key=AIzaSyCsjHNjJnWvCLxQMYlq9BnIQCy5MO8bIXY"
                ,
                                                      new Response.Listener<String>()
                                                      {
                                                          @Override
                                                          public void onResponse(String response)
                                                          {
                        
                                                              Log.w(AppConstants.TAG , "************************** geocode updated ressponse: " + response);
                        
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
                            
                                                                  JSONArray result = jsonObj.getJSONArray("results");
                                                                  JSONObject resultObj = result.getJSONObject(0);
                            
                                                                  JSONObject geometry = resultObj.getJSONObject("geometry");
                                                                  JSONObject bounds = geometry.getJSONObject("bounds");
                                                                  JSONObject location = geometry.getJSONObject("location");
    
    
                                                                  JSONObject northEast =  bounds.getJSONObject("northeast");
                                                                  JSONObject southWest =  bounds.getJSONObject("southwest");
                            
                                                                  Log.d(AppConstants.TAG , "northEast: " + northEast);
                                                                  Log.d(AppConstants.TAG , "southWest: " + southWest);
    
                                                                  deliverResultToReceiverUpdated(AppConstants.SUCCESS_RESULT, northEast.toString() , southWest.toString() ,location.toString() );
    
    
                                                              }
                                                              catch (JSONException e)
                                                              {
                                                                  Log.e("WatchOver" , "exception: "+ e.toString());
                                                              }
                        
                                                          }
                                                      }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //deliverResultToReceiverUpdated(Constants.SUCCESS_RESULT, "Unknown Location", index, iotf);
    
                deliverResultToReceiverUpdated(AppConstants.SUCCESS_RESULT, "Not found", "Not found" , "Not found");
                
                Log.e(AppConstants.TAG , "onErrorResponse geocoder: " + error.toString());
                
            }
            
        }) {
            
            public String getBodyContentType()
            {
                return "application/json";
            }
            
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        
        mRequestQueue.add(postRequest);
        
    }
    
    private void deliverResultToReceiverUpdated(int resultCode,  String northEast , String southWest, String location)
    {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.NORTH_EAST, northEast);
        bundle.putString(AppConstants.SOUTH_WEST, southWest);
        bundle.putString(AppConstants.CITY_LOCATION, location);
        resultReceiver.send(resultCode, bundle);
    }
}
