package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Model.CircuitType;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class CreateNewCircuitActivity extends AppCompatActivity implements View.OnClickListener
{
    RadioGroup typeGroup;
    RelativeLayout btn_create;
    SharedPreferences mPrefs;
    String BIDPLANID = "";
    EditText et_circuit_title;
    int lineTypeID = 0;
    LinearLayout lay_main;
    ArrayList<CircuitType> CircuitTypesList = new ArrayList<CircuitType>();
    DBController mDB;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_circuit);
        setupUI(findViewById(R.id.create_new_circuit));
        mDB = new DBController(getApplicationContext());
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        BIDPLANID = mPrefs.getString(AppConstants.BID_PLAN_ID , "");
        Log.i(AppConstants.TAG , "BIDPLANID: " + BIDPLANID);
    
        lay_main = (LinearLayout) findViewById(R.id.lay_main);
        
     //   CircuitTypesList = (ArrayList<CircuitType>) getIntent().getSerializableExtra("CIRCUITTYPES");
        
        String circuit_types = mPrefs.getString(AppConstants.CIRCUIT_TYPES , "");
        try
        {
            JSONArray circuitTypesArray = new JSONArray(circuit_types);
    
            for(int i=0; i<circuitTypesArray.length(); i++)
            {
                JSONObject object = circuitTypesArray.getJSONObject(i);
                String circuitTypeID =  object.opt("Id").toString();
                String circuitTypeTitle =  object.opt("Title").toString();
        
                CircuitType circuitType = new CircuitType(circuitTypeID , circuitTypeTitle);
                CircuitTypesList.add(circuitType);
            }
        }
        catch (JSONException e)
        {
            Log.e(AppConstants.TAG , "JSONException converting circuit types: " + e.toString());
        }
        
        
        et_circuit_title = (EditText) findViewById(R.id.et_circuit_title);
        typeGroup = (RadioGroup) findViewById(R.id.radioType);
        btn_create = (RelativeLayout) findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);
    
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
        createRadioButton();
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case R.id.btn_create:
                JSONObject postParams = getPostParams();
                Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
                
                if(AppConstants.isNetworkAvailable(CreateNewCircuitActivity.this))
                    CreateCircuit(postParams);
                else
                    CreateCircuitOffline(postParams);
                break;
    
            case R.id.back_ic:
                finish();
                break;
    
            case R.id.logout_ic:
                Intent intent = new Intent(CreateNewCircuitActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }
    
    public JSONObject getPostParams()
    {
        JSONObject obj = new JSONObject();
        JSONObject credentialsObj = new JSONObject();
    
        JSONArray linePathArray = new JSONArray();
        
        try
        {
            credentialsObj.put("LoginUserId" , Integer.parseInt(mPrefs.getString(AppConstants.USER_ID , "")));
            credentialsObj.put("BidPlanId" , Integer.parseInt(BIDPLANID));
            credentialsObj.put("Title" , et_circuit_title.getText().toString());
            credentialsObj.put("LineTypeId" , lineTypeID);
            credentialsObj.put("Milage" , "");
            credentialsObj.put("EstimatedHours" , "");
            credentialsObj.put("AverageDensity" , "");
            credentialsObj.put("EquipmentNotes" , "");
            credentialsObj.put("Source" , "");
            credentialsObj.put("LinePath" , linePathArray);
        }
        catch (JSONException e)
        {
            
        }
        
        return  credentialsObj;
    }
    
    private void CreateCircuit(final JSONObject postParams)
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.getting_data), true);
        
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
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
                                                                  
                                                                  Toast.makeText(CreateNewCircuitActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
    
                                                                  Intent intent = new Intent(CreateNewCircuitActivity.this, CircuitsActivity.class);
                                                                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                  startActivity(intent);
                                                                  finish();
                                                                  
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
                    
                    hideSoftKeyboard(CreateNewCircuitActivity.this);
                    
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
    
    private void createRadioButton() {
    
        RadioGroup.LayoutParams params_soiled = new RadioGroup.LayoutParams(getBaseContext(), null);
        params_soiled.setMargins(10, 70, 10, 0);
        
        final RadioButton[] rb = new RadioButton[CircuitTypesList.size()];
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioType); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        for(int i=0; i<CircuitTypesList.size(); i++){
            rb[i]  = new RadioButton(this);
            rb[i].setTextSize(22);
            rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
            rb[i].setText(CircuitTypesList.get(i).getTitle());
    
            rb[i].setLayoutParams(params_soiled);
            
            final int j = i;
    
            rb[i].setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d(AppConstants.TAG , "SELECTED IS: " + CircuitTypesList.get(j).getTitle() + "  "  + CircuitTypesList.get(j).getId());
                    lineTypeID = Integer.parseInt(CircuitTypesList.get(j).getId());
                }
            });
        }
        
       // lay_main.addView(rg);//you add the whole RadioGroup to the layout
        /*ll.addView(submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i = 0; i < 5; i++) {
                    rg.removeView(rb[i]);//now the RadioButtons are in the RadioGroup
                }
                ll.removeView(submit);
                Questions();
            }
        });*/
    }
    
    public void GetLocalDB(LatLng loc)
    {
        String locationString = loc.longitude + " " + loc.longitude;
        Log.v(AppConstants.TAG , "locationString: " + locationString);
    }
    
    public void CreateCircuitOffline(JSONObject postParams)
    {
        Random generator = new Random();
        int randCircuitID = generator.nextInt(20000) + 1;
        Log.i(AppConstants.TAG , "randCircuitID: " + randCircuitID);
        
        mDB.addNewCircuitEntry(mPrefs.getString(AppConstants.USER_ID , "") , BIDPLANID , randCircuitID +"" , postParams.toString());
        finish();
    }
}
