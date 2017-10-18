package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static asplundh.sps.com.asplundhproductivity.R.id.scan_barcode;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText et_username , et_pin;
    ImageView togglePin;
    DBController mDB;
    SharedPreferences mPrefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        setupUI(findViewById(R.id.activity_login));
        mDB = new DBController(getApplicationContext());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
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
                    et_pin.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                    JSONObject postParams = getLoginPostParams();
                    Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
                    LoginRequest(postParams , et_username.getText().toString() , "PIN");
             //   }
                
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
}
