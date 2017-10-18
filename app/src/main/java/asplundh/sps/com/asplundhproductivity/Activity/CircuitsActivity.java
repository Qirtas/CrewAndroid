package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import asplundh.sps.com.asplundhproductivity.Adapter.CircuitAdapter;
import asplundh.sps.com.asplundhproductivity.Model.Circuit;
import asplundh.sps.com.asplundhproductivity.Model.CircuitType;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class CircuitsActivity extends AppCompatActivity implements View.OnClickListener , Serializable
{
    private List<Circuit> circuitsList = new ArrayList<>();
    private ArrayList<CircuitType> circuitTypesList = new ArrayList<CircuitType>();
    
    private RecyclerView recyclerView;
    private CircuitAdapter mAdapter;
    public static String circuitsJson = "";
    SharedPreferences mPrefs;
    String bidPlanID = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuits);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        RelativeLayout lay_search = (RelativeLayout) findViewById(R.id.lay_search);
        lay_search.setOnClickListener(this);
        RelativeLayout lay_create_new = (RelativeLayout) findViewById(R.id.lay_create_new);
        lay_create_new.setOnClickListener(this);
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
    
        mAdapter = new CircuitAdapter(circuitsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        
         bidPlanID = getIntent().getStringExtra("BIDPLANID");
        
    
        JSONObject postParams = getPostParams();
        Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
        getCircuits(postParams);
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
    
        switch (id)
        {
            case R.id.back_ic:
                finish();
                break;
    
            case R.id.lay_search:
                Intent circuit = new Intent(CircuitsActivity.this , SearchSuggestionActivity.class);
                circuit.putExtra("TYPE" , "CIRCUIT");
                startActivity(circuit);
                break;
    
            case R.id.lay_create_new:
                Intent i = new Intent(CircuitsActivity.this , CreateNewCircuitActivity.class);
                i.putExtra("BIDPLANID" , bidPlanID);
                i.putExtra("CIRCUITTYPES" , circuitTypesList);
                startActivity(i);
                break;
    
            case R.id.logout_ic:
                Intent intent = new Intent(CircuitsActivity.this, LoginActivity.class);
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
        
        try
        {
            
            credentialsObj.put("userId" , mPrefs.getString(AppConstants.USER_ID , ""));
            credentialsObj.put("bidPlanId" , mPrefs.getString(AppConstants.BID_PLAN_ID , ""));
            
        }
        catch (JSONException e)
        {
            
        }
        
        return  credentialsObj;
    }
    
    private void getCircuits(final JSONObject postParams)
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.getting_data), true);
    
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      AppConstants.BASE_URL + "getCircuits"
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
    
                                                                  if(!success)
                                                                  {
                                                                      Toast.makeText(CircuitsActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                  {
                                                                      JSONObject result = jsonObj.getJSONObject("result");
                                                                      JSONArray circuitsArray = result.getJSONArray("Circuits");
                                                                      
                                                                      for(int i=0; i<circuitsArray.length(); i++)
                                                                      {
                                                                          JSONObject object = circuitsArray.getJSONObject(i);
                                                                          String circuitID =  object.opt("Id").toString();
                                                                          String circuitTitle =  object.opt("Title").toString();
                                                                          String circuitMilage =  object.opt("Milage").toString();
                                                                          String circuitLineType =  object.opt("LineType").toString();
                                                                          String circuitIsDelegated =  object.opt("IsDelegated").toString();
                                                                          String circuitLinePath =  object.opt("LinePath").toString();
    
    
                                                                          Circuit circuit = new Circuit(circuitID ,circuitTitle , circuitMilage , circuitLineType , circuitIsDelegated , circuitLinePath , bidPlanID);
                                                                          circuitsList.add(circuit);
                                                                          mAdapter.notifyDataSetChanged();
                                                                      }
    
                                                                      JSONArray circuitTypesArray = result.getJSONArray("CircuitTypes");
                                                                      
                                                                      for(int i=0; i<circuitTypesArray.length(); i++)
                                                                      {
                                                                          JSONObject object = circuitTypesArray.getJSONObject(i);
                                                                          String circuitTypeID =  object.opt("Id").toString();
                                                                          String circuitTypeTitle =  object.opt("Title").toString();
    
                                                                          CircuitType circuitType = new CircuitType(circuitTypeID , circuitTypeTitle);
                                                                          circuitTypesList.add(circuitType);
                                                                      }
                                                                  }
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
    
  
    }
