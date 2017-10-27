package asplundh.sps.com.asplundhproductivity.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Service.GeocodeAddressIntentService;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static asplundh.sps.com.asplundhproductivity.R.id.scan_barcode;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText et_username , et_pin;
    ImageView togglePin;
    DBController mDB;
    SharedPreferences mPrefs;
    BoundsResultReceiver mResultReceiver;
    TextView tv_see_more;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        mResultReceiver = new BoundsResultReceiver(null);
    
       // AppConstants.reverseGeocoding(LoginActivity.this , "Islamabad");
       // getCityBounds("Karachi");
        
        AppConstants.getISOCurrentTime();
    
        setupUI(findViewById(R.id.activity_login));
        mDB = new DBController(getApplicationContext());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    
       // mDB.addBidPlanEntry("111" , "1"  ,"aaa" , "jdh" , "sdfndk" , "sdfjk" ,"j" , "sdfjh" , "000");
        
        et_username = (EditText) findViewById(R.id.et_username);
        et_pin = (EditText) findViewById(R.id.et_pin);
        togglePin = (ImageView) findViewById(R.id.toggle_password_visibility);
        final Typeface tf = Typeface.create("sans-serif",Typeface.BOLD);
        
        togglePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = et_pin.getInputType();
                //129
                if(type == 18){
                    // if(et_password.getText().length() > 0) {
//                        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_pin.setInputType(InputType.TYPE_CLASS_TEXT);
                    et_pin.setTypeface(tf, Typeface.BOLD);
                    togglePin.setImageDrawable(getResources().getDrawable(R.drawable.eye_slash));
                
                    //   }
                }
                else{
                    et_pin.setInputType(18);
                    et_pin.setTypeface(tf, Typeface.BOLD);
                    togglePin.setImageDrawable(getResources().getDrawable(R.drawable.toggle_visibility));
//                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            
            }
        });
        
        RelativeLayout scan_barcode = (RelativeLayout) findViewById(R.id.scan_barcode);
        scan_barcode.setOnClickListener(this);
    
        RelativeLayout btn_login = (RelativeLayout) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }
    
    public void getCityBounds(String cityName)
    {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(AppConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppConstants.CITY_NAME , cityName);
        startService(intent);
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
                    
                    hideSoftKeyboard(LoginActivity.this);
                    
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
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case R.id.btn_login:
               /* if(validateForm())
                {*/
                    if(AppConstants.isNetworkAvailable(LoginActivity.this))
                    {
                        JSONObject postParams = getLoginPostParams();
                        Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
                        LoginRequest(postParams , et_username.getText().toString() , "PIN");
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this , "Network not available!",
                                       Toast.LENGTH_LONG).show();
                      //  doOfflineLogin();
                    }
              //  }
                
                break;
    
            case scan_barcode:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setPrompt(getResources().getString(R.string.scan_a_barcode));
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
                break;
        }
    }
    
    private void LoginRequest(final JSONObject postParams , final String userID , final String loginType)
    {
        Log.v(AppConstants.TAG, "LoginRequest postParams " + postParams);
    
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                      getResources().getString(R.string.please_wait_aunthenticating_msg), true);
        
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                       "http://apabackend.mybluemix.net/users/login"
                                                      // "http://192.168.100.49:4000/auth/mobileLogin"
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                        
                                                              Log.d(AppConstants.TAG , "LoginRequest response: " + response);
    
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
    
                                                                  boolean success = jsonObj.getBoolean("success");
                                                                  String message = jsonObj.getString("message");
    
                                                                  if(!success)
                                                                  {
                                                                      Toast.makeText(LoginActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                  {
                                                                      JSONArray jsonArray = jsonObj.getJSONArray("result");
                                                                      JSONObject resultObj = jsonArray.getJSONObject(0);
    
                                                                      String id = resultObj.opt("Id").toString();
                                                                      String UserName = resultObj.opt("UserName").toString();
                                                                      String FirstName = resultObj.opt("FirstName").toString();
                                                                      String LastName = resultObj.opt("LastName").toString();
    
                                                                      mPrefs.edit().putString(AppConstants.USER_ID , id).commit();
    
                                                                      boolean isRecordExists = mDB.isLoginRecordExists(id);
                                                                      Log.i(AppConstants.TAG , "isRecordExists: " + isRecordExists + " for id: " + id);
    
                                                                      if(isRecordExists)
                                                                      {
                                                                          mDB.updateLoginEntry(id , et_username.getText().toString()   , et_pin.getText().toString());
                                                                      }
                                                                      else
                                                                      {
                                                                          mDB.addLoginEntry(id , et_username.getText().toString() , et_pin.getText().toString());
                                                                      }
                                                                      
                                                                      Intent i = new Intent(LoginActivity.this , BidPlanActivity.class);
                                                                      startActivity(i);
                                                                  }
                                                              }
                                                              catch (JSONException e)
                                                              {
                                                                  Log.e(AppConstants.TAG , "JSONException: " + e.toString());
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
                Toast.makeText(LoginActivity.this , "Error ocuured! Please try again",
                               Toast.LENGTH_LONG).show();
                
                
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
    
    public JSONObject getLoginPostParams()
    {
        JSONObject obj = new JSONObject();
        JSONObject credentialsObj = new JSONObject();
        
        try
        {
            
            credentialsObj.put("username" , et_username.getText().toString());
            credentialsObj.put("password" , et_pin.getText().toString());
            
        }
        catch (JSONException e)
        {
            
        }
        
        return  credentialsObj;
    }
    
    private boolean validateForm()
    {
        boolean valid = true;
        if (et_username.getText().toString().length() == 0)
        {
            et_username.setError(getString(R.string.enter_valid_employee_id));
          //  clearId.setVisibility(View.GONE);
            valid = false;
        }
        else if(et_username.getText().toString().length() != 10)
        {
            et_username.setError(getString(R.string.enter_valid_employee_id));
         //   clearId.setVisibility(View.GONE);
            valid = false;
        }
        else if (et_pin.getText().toString().length() == 0)
        {
            et_pin.setError(getString(R.string.enter_four_digit_pin));
          //  togglePin.setVisibility(View.GONE);
            
            valid = false;
        }
        else if(et_pin.getText().toString().length() != 4)
        {
            et_pin.setError(getString(R.string.enter_valid_pin));
         //   togglePin.setVisibility(View.GONE);
            
            valid = false;
        }
        return valid;
        
    }
    
    public void doOfflineLogin()
    {
        boolean isDataFound = false;
    
        String emp_id = et_username.getText().toString();
        String emp_pin = et_pin.getText().toString();
    
        Cursor cursor = mDB.getAllData(mDB.DB_TABLE_LOGINS);
        Log.i(AppConstants.TAG , "cursor size: " + cursor.getColumnCount());
    
        if (cursor.moveToFirst())
        {
            do
            {
                String empdb_id = cursor.getString(cursor.getColumnIndex(DBController.KEY_EMPNAME));
                Log.v(AppConstants.TAG , "empdb_id: " + empdb_id);
        
                if(empdb_id.equalsIgnoreCase(emp_id))
                {
                    Log.i(AppConstants.TAG , "IDS MATCHED");
            
                    String empdb_pin = cursor.getString(cursor.getColumnIndex(DBController.KEY_EMPPIN));
                    String emp_name = cursor.getString(cursor.getColumnIndex(DBController.KEY_EMPNAME));
            
                    if(empdb_pin.equalsIgnoreCase(emp_pin))
                    {
                        isDataFound = true;
                        boolean isForePerson = false;
                        
                        mPrefs.edit().putString(AppConstants.EMP_NAME , emp_name).commit();
                        mPrefs.edit().putString(AppConstants.EMP_ID , empdb_id).commit();
                        mPrefs.edit().putString(AppConstants.EMP_PIN , empdb_pin).commit();
                        
                        Log.d(AppConstants.TAG , "DO LOGIN");
                        Intent i = new Intent(LoginActivity.this , BidPlanActivity.class);
                        startActivity(i);
                
                    }
                    else
                    {
                        Log.e(AppConstants.TAG , "PIN IS WRONG");
                    }
            
                    Log.w(AppConstants.TAG , "//////////////////////");
                    Log.v(AppConstants.TAG , "empdb_id: " + empdb_id);
                    Log.v(AppConstants.TAG , "empname: " + emp_name);
                    Log.v(AppConstants.TAG , "empdb_pin: " + empdb_pin);
                    
                }
                else
                {
                    //Log.e(AppConstants.TAG , "No record found with enterd ID");
            
                }
        
                // do what ever you want here
            }while(cursor.moveToNext());
    
            cursor.close();
        }
    }
    
    
    @SuppressLint("ParcelCreator")
    class BoundsResultReceiver extends ResultReceiver
    {
        public BoundsResultReceiver(Handler handler)
        {
            super(handler);
        }
        
        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData)
        {
            if (resultCode == AppConstants.SUCCESS_RESULT)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        
                        String northEastBounds = resultData.getString(AppConstants.NORTH_EAST);
                        String southWestBounds = resultData.getString(AppConstants.SOUTH_WEST);
                        String cityLocation = resultData.getString(AppConstants.CITY_LOCATION);
    
    
                        Log.i(AppConstants.TAG , "Bounds in BoundsResultReceiver: " + northEastBounds +  "  " + southWestBounds);
                        Log.i(AppConstants.TAG , "City loc in BoundsResultReceiver: " + cityLocation);
    
    
                        Intent i = new Intent(LoginActivity.this , MapDownloadActivity.class);
                        i.putExtra(AppConstants.NORTH_EAST , northEastBounds);
                        i.putExtra(AppConstants.SOUTH_WEST , southWestBounds);
                        i.putExtra(AppConstants.CITY_LOCATION , cityLocation);
                        startActivity(i);
                        
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
    
    ///////////////////////////////////////////
   
}
