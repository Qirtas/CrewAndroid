package asplundh.sps.com.asplundhproductivity.Service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import java.util.List;
import java.util.Locale;
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
        double latitude = intent.getDoubleExtra(AppConstants.LOCATION_LATITUDE_DATA_EXTRA, 0);
        double longitude = intent.getDoubleExtra(AppConstants.LOCATION_LONGITUDE_DATA_EXTRA, 0);
        int Index = intent.getIntExtra("INDEX", -1);
        String IOTF_DEVID = intent.getStringExtra("IOTF_DEVID");
    
        getGeocodeUpdated(latitude, longitude, Index, IOTF_DEVID);
    
        Locale locale = new Locale("en", "us");
        Geocoder geocoder = new Geocoder(this, Locale.US);
        String errorMessage = "";
        List<Address> addresses = null;
    
        int fetchType = intent.getIntExtra(AppConstants.FETCH_TYPE_EXTRA, 0);
        //   Log.e(AppConstants.TAG, "fetchType == " + fetchType);

           /* double latitude = intent.getDoubleExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA, 0);
            double longitude = intent.getDoubleExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA, 0);
            int Index = intent.getIntExtra("INDEX", -1);
            String IOTF_DEVID = intent.getStringExtra("IOTF_DEVID");*/

          /*  try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {
                errorMessage = "Service Not Available";
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }*/
    
        resultReceiver = intent.getParcelableExtra(AppConstants.RECEIVER);
      /*  if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Not Found";
                Log.e(AppConstants.TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, null, Index, IOTF_DEVID);
        } else {
            for(Address address : addresses) {
                String outputAddress = "";
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " --- " + address.getAddressLine(i);
                }
            //    Log.e(AppConstants.TAG, outputAddress);
            }
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++)
            {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(AppConstants.TAG, "Address Found: " + address);

            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                                    TextUtils.join(System.getProperty("line.separator"),
                                                   addressFragments), address, Index, IOTF_DEVID);
        }*/
    }
    
    
    public  void getGeocodeUpdated(double lat, double lng, final int index, final String iotf)
    {
        // Log.v(AppConstants.TAG, "URL " + "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + lng + "&key=AIzaSyB1kTYkURwfYKN8hgUQh2OodkYGfPdrlMg");
        String address = "";
        //AIzaSyCsjHNjJnWvCLxQMYlq9BnIQCy5MO8bIXY
        
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +lat + "," + lng + "&key=AIzaSyCsjHNjJnWvCLxQMYlq9BnIQCy5MO8bIXY"
                ,
                                                      new Response.Listener<String>()
                                                      {
                    
                    
                                                          @Override
                                                          public void onResponse(String response)
                                                          {
                        
                                                             // Log.w(AppConstants.TAG , "************************** geocode updated ressponse: " + response);
                        
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
                            
                                                                  String status = jsonObj.opt("status").toString();
                                                                 //  Log.i(AppConstants.TAG , "status: "+ status);
                            
                                                                  if(status.equalsIgnoreCase("OK"))
                                                                  {
                                                                      JSONArray resultsData = jsonObj.getJSONArray("results");
                                                                      JSONObject obj = resultsData.getJSONObject(0);
                                                                      String formatted_add = obj.opt("formatted_address").toString();
                                
                                                                      // Log.d("WatchOver" , "formatted_add: "+ formatted_add);
                                
                                                                      deliverResultToReceiverUpdated(AppConstants.SUCCESS_RESULT, formatted_add);
                                                                  }
                                                                  else
                                                                  {
                                                                      deliverResultToReceiverUpdated(AppConstants.SUCCESS_RESULT, "Unknown Location");
                                                                  }
                            
                                                              }
                                                              catch (JSONException e)
                                                              {
                                                                  Log.e(AppConstants.TAG , "exception: " + e.toString());
                                                              }
                                                              
                                                          }
                                                      }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
               // deliverResultToReceiverUpdated(Constants.SUCCESS_RESULT, "Unknown Location", index, iotf);
                
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
    
    private void deliverResultToReceiverUpdated(int resultCode,  String address)
    {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.RESULT_ADDRESS, address);
        resultReceiver.send(resultCode, bundle);
    }
}
