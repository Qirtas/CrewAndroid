package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import asplundh.sps.com.asplundhproductivity.Adapter.CircuitAdapter;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitChildModel;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitExpandAdapter;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitParentModel;
import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Model.CircuitType;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class CircuitsActivity extends AppCompatActivity implements View.OnClickListener , Serializable
{
    private ArrayList<CircuitType> circuitTypesList = new ArrayList<CircuitType>();
    
    private RecyclerView recyclerView;
    private CircuitAdapter mAdapter;
    public static String circuitsJson = "";
    SharedPreferences mPrefs;
    String bidPlanID = "";
    DBController mDB;
    private CircuitExpandAdapter mAdapterExpand;
    ArrayList<CircuitParentModel> parentList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuits);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        mDB = new DBController(getApplicationContext());
        
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
    
        mAdapterExpand = new CircuitExpandAdapter(this, parentList);
        mAdapterExpand.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                CircuitParentModel expandedRecipe = parentList.get(parentPosition);
                Log.v(AppConstants.TAG , "Here");
               /* String toastMsg = getResources().getString(R.string.expanded, expandedRecipe.getName());
                Toast.makeText(RecyclerActivity.this,
                               toastMsg,
                               Toast.LENGTH_SHORT)
                        .show();*/
            }
        
            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                CircuitParentModel collapsedRecipe = parentList.get(parentPosition);
                Log.v(AppConstants.TAG , "Here");
               /* String toastMsg = getResources().getString(R.string.collapsed, collapsedRecipe.getName());
                Toast.makeText(VerticalLinearRecyclerViewSampleActivity.this,
                               toastMsg,
                               Toast.LENGTH_SHORT)
                        .show();*/
            }
        });
        recyclerView.setAdapter(mAdapterExpand);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        
        /*mAdapter = new CircuitAdapter(circuitsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);*/
        
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
                circuit.putExtra("BIDPLANID" , bidPlanID);
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
        parentList.clear();
        
        final RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                      AppConstants.BASE_URL + "getCircuits"
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                    
                                                              Log.d(AppConstants.TAG , "getCircuits response: " + response);
                                                              circuitsJson = response;
                                                             /* mDB.clearTable(DBController.DB_TABLE_CIRCUITS_JSON);
                                                              mDB.addCircuitsJSON(mPrefs.getString(AppConstants.USER_ID , "") , bidPlanID , response);
    */
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
                                                                      
                                                                      /*for(int i=0; i<circuitsArray.length(); i++)
                                                                      {*/
                                                                      for(int i=circuitsArray.length()-1; i>=0; i--)
                                                                      {
                                                                          JSONObject object = circuitsArray.getJSONObject(i);
                                                                          String circuitID =  object.opt("Id").toString();
                                                                          String circuitTitle =  object.opt("Title").toString();
                                                                          String circuitMilage =  object.opt("Milage").toString();
                                                                          String EquipmentNotes =  object.opt("EquipmentNotes").toString();
                                                                          String SurveysCount =  object.opt("SurveysCount").toString();
                                                                          String circuitLineType =  object.opt("LineType").toString();
                                                                          String circuitIsDelegated =  object.opt("IsDelegated").toString();
                                                                          String circuitLinePath =  object.opt("LinePath").toString();
    
                                                                          ArrayList<CircuitChildModel> childlist =  new ArrayList<>();
                                                                          CircuitChildModel childitem = new CircuitChildModel(circuitLineType , circuitMilage , SurveysCount , EquipmentNotes , true);
                                                                          childlist.add(childitem);
                                                                          
                                                                          CircuitParentModel parentitem = new CircuitParentModel(circuitID , circuitTitle , circuitIsDelegated, childlist , bidPlanID);
                                                                          parentList.add(parentitem);
    
                                                                          mAdapterExpand.notifyParentDataSetChanged(true);
                                                                          mAdapterExpand.notifyDataSetChanged();
                                                                          recyclerView.setLayoutManager(new LinearLayoutManager(CircuitsActivity.this));
                                                                          /*Circuit circuit = new Circuit(circuitID ,circuitTitle , circuitMilage , circuitLineType , circuitIsDelegated , circuitLinePath , bidPlanID);
                                                                          circuitsList.add(circuit);
                                                                          mAdapter.notifyDataSetChanged();*/
                                                                      }
    
                                                                      JSONArray circuitTypesArray = result.getJSONArray("CircuitTypes");
                                                                      mPrefs.edit().putString(AppConstants.CIRCUIT_TYPES , circuitTypesArray.toString()).commit();
    
    
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
                
                Toast.makeText(CircuitsActivity.this , "Error ocuured! Please try again",
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
    
    public void getCircuitsLocally()
    {
        Log.i(AppConstants.TAG , "getCircuitsLocally");
        parentList.clear();
    
        //Loading new circuits
        
        Cursor cursor_new_circuit = mDB.getAllData(DBController.DB_TABLE_NEW_CIRCUIT);
    
        if (cursor_new_circuit.moveToFirst())
        {
            do
            {
                String circuit_JSON = cursor_new_circuit.getString(cursor_new_circuit.getColumnIndex(DBController.KEY_NEW_CIRCUIT_POST_PARAMS));
                String circuit_new_id = cursor_new_circuit.getString(cursor_new_circuit.getColumnIndex(DBController.KEY_CIRCUIT_ID));
    
                Log.i(AppConstants.TAG, "circuit_JSON: " + circuit_JSON);
                Log.w(AppConstants.TAG, "circuit_new_id: " + circuit_new_id);
            
                try
                {
                    JSONObject circuit_json_obj = new JSONObject(circuit_JSON);
                    String circuitID = circuit_new_id;
                    String circuitTitle = circuit_json_obj.get("Title").toString();
                    String circuitLineType = circuit_json_obj.get("LineTypeId").toString();
                    String circuitMilage = circuit_json_obj.get("Milage").toString();
                    String circuitLinePath = circuit_json_obj.get("LinePath").toString();
                    
                    ArrayList<CircuitChildModel> childlist =  new ArrayList<>();
                    CircuitChildModel childitem = new CircuitChildModel(circuitLineType , circuitMilage , "3" , "..." , true);
                    childlist.add(childitem);
                    
                    CircuitParentModel parentitem = new CircuitParentModel(circuitID , circuitTitle , "0" , childlist , bidPlanID);
                    parentList.add(parentitem);
    
                    mAdapterExpand.notifyParentDataSetChanged(true);
                    mAdapterExpand.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(CircuitsActivity.this));
                    
                }
                catch (JSONException e)
                {
                    Log.e(AppConstants.TAG , "JSONException in cursor_new_circuit.moveToFirst(): " + e.toString());
                }
            
            
            }
            while (cursor_new_circuit.moveToNext());
        }
    
        //Loading synced circuits (saved circuits)
    
        Cursor cursor_all_circuits =  mDB.getAllCircuit(mPrefs.getString(AppConstants.USER_ID , "") , mPrefs.getString(AppConstants.BID_PLAN_ID , ""));
        Log.i(AppConstants.TAG , "cursor_all_circuits cursor size : " + cursor_all_circuits.getCount());
    
        if (cursor_all_circuits.moveToFirst())
        {
            do
            {
                String circuit_id = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_CIRCUIT_ID));
                String circuit_title = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_CIRCUIT_TITLE));
                String circuit_milage = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_CIRCUIT_MILAGE));
                String circuit_path = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_PATH_JSON));
                String circuit_type = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_LINE_TYPE));
                String circuit_isDelegated = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_IS_DELEGATED));
                String circuit_sruvey_done = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_SURVEYS_COMPLETED));
                String circuit_equipment_note = cursor_all_circuits.getString(cursor_all_circuits.getColumnIndex(DBController.KEY_EQUIPMENT_NOTE));
                
                ArrayList<CircuitChildModel> childlist =  new ArrayList<>();
                CircuitChildModel childitem = new CircuitChildModel(circuit_type , circuit_milage , circuit_sruvey_done , circuit_equipment_note , true);
                childlist.add(childitem);
    
                CircuitParentModel parentitem = new CircuitParentModel(circuit_id , circuit_title , circuit_isDelegated , childlist , bidPlanID);
                parentList.add(parentitem);
    
                mAdapterExpand.notifyParentDataSetChanged(true);
                mAdapterExpand.notifyDataSetChanged();
                recyclerView.setLayoutManager(new LinearLayoutManager(CircuitsActivity.this));
                
            }while(cursor_all_circuits.moveToNext());
        }
    
        mAdapterExpand.notifyParentDataSetChanged(true);
        mAdapterExpand.notifyDataSetChanged();
        
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
    
        bidPlanID = getIntent().getStringExtra("BIDPLANID");
    
        JSONObject postParams = getPostParams();
        Log.v(AppConstants.TAG , "POST PARAMS: " + postParams);
    
        if(AppConstants.isNetworkAvailable(CircuitsActivity.this))
        {
            getCircuits(postParams);
        }
        else
        {
            getCircuitsLocally();
        }
    }
}
