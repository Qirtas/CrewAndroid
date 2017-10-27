package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import asplundh.sps.com.asplundhproductivity.Expandable.ChildModel;
import asplundh.sps.com.asplundhproductivity.Expandable.ParentModel;
import asplundh.sps.com.asplundhproductivity.Expandable.RecipeAdapter;
import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Model.BitPlan;
import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Singleton.MySingleton;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static asplundh.sps.com.asplundhproductivity.Utils.AppConstants.BASE_URL;

public class BidPlanActivity extends AppCompatActivity implements View.OnClickListener
{
    private List<BitPlan> bidPlanListList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter mAdapter;
    public static String bidPlansJson = "";
    
    ArrayList<ParentModel> parentList = new ArrayList<>();
    DBController mDB;
    SharedPreferences mPrefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_plan);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        mDB = new DBController(getApplicationContext());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        
        RelativeLayout lay_search = (RelativeLayout) findViewById(R.id.lay_search);
        lay_search.setOnClickListener(this);
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
    
        mAdapter = new RecipeAdapter(this, parentList);
        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                ParentModel expandedRecipe = parentList.get(parentPosition);
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
                ParentModel collapsedRecipe = parentList.get(parentPosition);
                Log.v(AppConstants.TAG , "Here");
               /* String toastMsg = getResources().getString(R.string.collapsed, collapsedRecipe.getName());
                Toast.makeText(VerticalLinearRecyclerViewSampleActivity.this,
                               toastMsg,
                               Toast.LENGTH_SHORT)
                        .show();*/
            }
        });
    
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        /*if(AppConstants.isNetworkAvailable(BidPlanActivity.this))
        {
            getBidPlans();
        }
        else
            getBidPlansLocally();*/
        
        //prepareData();
    }
    
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        
        switch (id)
        {
            case R.id.lay_search:
                Intent i = new Intent(BidPlanActivity.this , SearchSuggestionActivity.class);
                i.putExtra("TYPE" , "BIDPLANS");
                startActivity(i);
                break;
    
            case R.id.back_ic:
               finish();
                break;
    
            case R.id.logout_ic:
                Intent intent = new Intent(BidPlanActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }
    
    String KEY_BIDPLANID_SYNCED = "";
    
    private void getBidPlans()
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.getting_data), true);
        
        Cursor cursor = mDB.getAllData(DBController.DB_TABLE_BIDPLANS);
        
        if(cursor.moveToFirst())
        {
            KEY_BIDPLANID_SYNCED = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLANID));
            Log.d(AppConstants.TAG , "KEY_BIDPLANID_SYNCED: " + KEY_BIDPLANID_SYNCED);
        }
        
        Log.i(AppConstants.TAG , "getBidPlans URL: " + BASE_URL + "getAllBidPlansByUser?id=1");
    
        RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.GET,
                                                      BASE_URL + "getAllBidPlansByUser?id=" +mPrefs.getString(AppConstants.USER_ID , "")
            
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                    
                                                              Log.d(AppConstants.TAG , "getBidPlans ressponse: " + response);
                                                              bidPlansJson = response;
                                                              AppConstants.BIDPLANS_JSONARRAY = response;
    
                                                              try
                                                              {
                                                                  JSONObject jsonObj = new JSONObject(response);
    
                                                                  boolean success = jsonObj.getBoolean("success");
                                                                  String message = jsonObj.getString("message");
    
                                                                  if(!success)
                                                                  {
                                                                      Toast.makeText(BidPlanActivity.this , message,
                                                                                     Toast.LENGTH_LONG).show();
                                                                  }
                                                                  else
                                                                  {
                                                                    //  mDB.clearTable(DBController.DB_TABLE_BIDPLANS_JSON);
                                                                      JSONArray array = jsonObj.getJSONArray("result");
    
                                                                   //   mDB.addBidPlanJSON(mPrefs.getString(AppConstants.USER_ID , "") , array.toString());
                                                                      
                                                                      for(int i=0; i<array.length(); i++)
                                                                      {
                                                                          boolean isSynced = false;
                                                                          JSONObject obj = array.getJSONObject(i);
                                                                          String id = obj.opt("Id").toString();
                                                                          String CustomerBidId = obj.opt("CustomerBidId").toString();
                                                                          String Title = obj.opt("Title").toString();
                                                                          String VersionNumber = obj.opt("VersionNumber").toString();
                                                                         
                                                                          String City = obj.opt("City").toString();
                                                                          String Country = obj.opt("Country").toString();
                                                                          String LocationDescription = obj.opt("LocationDescription").toString();
                                                                          String CustomerName = obj.opt("CustomerName").toString();
                                                                          String Metric = obj.opt("Metric").toString();
    
                                                                          if(id.equalsIgnoreCase(KEY_BIDPLANID_SYNCED))
                                                                              isSynced = true;
                                                                          else
                                                                              isSynced = false;
                                                                          
                                                                          JSONArray subUnits = obj.optJSONArray("SubUnits");
                                                                          
                                                                          ArrayList<SubUnit> subunits_list = new ArrayList<>();
                                                                          
                                                                          try
                                                                          {
                                                                              for(int j=0; j<subUnits.length(); j++)
                                                                              {
                                                                                  JSONObject subunit = subUnits.getJSONObject(j);
                                                                                  String Id_subunit = subunit.opt("Id").toString();
                                                                                  String Title_subunit = subunit.opt("Title").toString();
        
                                                                                  SubUnit subUnit = new SubUnit(Id_subunit , Title_subunit , id);
                                                                                  subunits_list.add(subUnit);
        
                                                                                  Log.v(AppConstants.TAG , "subunit: " + subunit);
    
                                                                                 // mDB.addBidPlanEntry(mPrefs.getString(AppConstants.USER_ID , "") , id  ,Title , Metric , subUnits.toString() , CustomerName , City+","+Country , LocationDescription , VersionNumber);
    
                                                                              }
                                                                          }
                                                                          catch (NullPointerException e)
                                                                          {
                                                                              Log.e(AppConstants.TAG , "NullPointerException in getting SubuNits");
                                                                          }
                                                                          
                                                                          //VERSIONS
                                                                          JSONArray Versions = obj.getJSONArray("Versions");
                                                                          for(int k=0; k<Versions.length(); k++)
                                                                          {
                                                                              boolean isSynced_version = false;
                                                                              JSONObject version = Versions.getJSONObject(k);
                                                                              String id_version = version.opt("Id").toString();
                                                                              String Title_version = version.opt("Title").toString();
                                                                              String VersionNumber_version = version.opt("VersionNumber").toString();
                                                                              String City_version = version.opt("City").toString();
                                                                              String Country_version = version.opt("Country").toString();
                                                                              String LocDescription_version = version.opt("LocationDescription").toString();
                                                                              String CustomerBidId_version = version.opt("CustomerBidId").toString();
    
                                                                              String CustomerName_version = version.opt("CustomerName").toString();
                                                                              String Metric_version = version.opt("Metric").toString();
    
                                                                              JSONArray subUnits_version = version.optJSONArray("SubUnits");
                                                                              ArrayList<SubUnit> subunits_list_version = new ArrayList<>();
                                                                              
                                                                              if(id_version.equalsIgnoreCase(KEY_BIDPLANID_SYNCED))
                                                                                  isSynced_version = true;
                                                                              else
                                                                                  isSynced_version = false;
                                                                              
                                                                              Log.d(AppConstants.TAG , "subUnits_version: " + subUnits_version);
    
                                                                              for(int j=0; j<subUnits_version.length(); j++)
                                                                              {
                                                                                  JSONObject subunit = subUnits_version.getJSONObject(j);
                                                                                  String Id_subunit = subunit.opt("Id").toString();
                                                                                  String Title_subunit = subunit.opt("Title").toString();
        
                                                                                  SubUnit subUnit = new SubUnit(Id_subunit , Title_subunit , id_version);
                                                                                  subunits_list_version.add(subUnit);
        
                                                                                  Log.w(AppConstants.TAG , "subunit: " + subunit);
                                                                              }
    
                                                                            //  mDB.addBidPlanEntry(mPrefs.getString(AppConstants.USER_ID , "") , id_version  ,Title_version , Metric_version , subUnits_version.toString() , CustomerName_version , City_version+","+Country_version , LocDescription_version , VersionNumber_version);
                                                                              
                                                                              Log.w(AppConstants.TAG , "version: " + version);
    
                                                                              ArrayList<ChildModel> childlist =  new ArrayList<>();
                                                                              
                                                                              ChildModel childitem = new ChildModel(VersionNumber_version , Metric_version , CustomerName_version ,  City_version ,  Country_version, subunits_list_version, true , LocDescription_version , subUnits_version.toString());
                                                                              childlist.add(childitem);
    
                                                                              ParentModel parentitem = new ParentModel(id_version ,  CustomerBidId_version , Title_version,VersionNumber_version , childlist, true, isSynced_version , subUnits_version.toString() , Metric_version);
                                                                              parentList.add(parentitem);
                                                                          }
    
                                                                          Log.i(AppConstants.TAG , "id: " + id);
                                                                          Log.i(AppConstants.TAG , "CustomerBidId: " + CustomerBidId);
                                                                          Log.i(AppConstants.TAG , "Title: " + Title);
                                                                          Log.i(AppConstants.TAG , "VersionNumber: " + VersionNumber);
                                                                        
                                                                          Log.i(AppConstants.TAG , "City: " + City);
                                                                          Log.i(AppConstants.TAG , "Country: " + Country);
                                                                          Log.i(AppConstants.TAG , "LocationDescription: " + LocationDescription);
                                                                          Log.i(AppConstants.TAG , "Metric: " + Metric);
    
                                                                          ArrayList<ChildModel> childlist =  new ArrayList<>();
    
                                                                          ChildModel childitem = new ChildModel(VersionNumber , Metric , CustomerName ,  City ,  Country, subunits_list , true , LocationDescription , subUnits.toString());
                                                                          childlist.add(childitem);
    
                                                                          ParentModel parentitem = new ParentModel(id ,  CustomerBidId , Title,VersionNumber , childlist , true , isSynced , subUnits.toString() , Metric);
                                                                          parentList.add(parentitem);
    
                                                                          mAdapter.notifyParentDataSetChanged(true);
                                                                          mAdapter.notifyDataSetChanged();
                                                                          recyclerView.setLayoutManager(new LinearLayoutManager(BidPlanActivity.this));
                                                                      }
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
    
                Toast.makeText(BidPlanActivity.this , "Error ocuured! Please try again",
                               Toast.LENGTH_LONG).show();
                
            }
        
        }) {
        
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                80000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    
        mRequestQueue.add(postRequest);
    }
    
    public void getBidPlansLocally()
    {
        /*Cursor cursor = mDB.getBidPlanJson(mPrefs.getString(AppConstants.USER_ID , ""));
        cursor.moveToFirst();
        String bidPlansJson = cursor.getString(cursor.getColumnIndex(DBController.KEY_BIDPLAN_JSON));
     
        Log.v(AppConstants.TAG , "AppConstants.BIDPLANS_JSONARRAY: " + bidPlansJson);*/
        
        /////////////////////////////////////////////////////////////
    
        parentList.clear();
        
        Cursor cursor = mDB.getAllData(DBController.DB_TABLE_BIDPLANS);
        Log.d(AppConstants.TAG , "getBidPlansLocally size: " + cursor.getCount());
    
        if (cursor.moveToFirst())
        {
            do
            {
                String emp_id = cursor.getString(cursor.getColumnIndex(mDB.KEY_EMPID));
                String KEY_BIDPLANID = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLANID));
                String KEY_BIDPLAN_TITLE = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_TITLE));
                String KEY_BIDPLAN_UNIT = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_UNIT));
                String KEY_BIDPLAN_SUBUNIT = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_SUBUNIT));
                String KEY_BIDPLAN_CUSTOMER_NAME = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_CUSTOMER_NAME));
                String KEY_BIDPLAN_LOCATION = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_LOCATION));
                String KEY_BIDPLAN_LOCATION_DESC = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_LOCATION_DESC));
                String KEY_BIDPLAN_VERSION = cursor.getString(cursor.getColumnIndex(mDB.KEY_BIDPLAN_VERSION));
    
                Log.v(AppConstants.TAG , "subUnits_version: " + KEY_BIDPLAN_SUBUNIT);
    
                ArrayList<SubUnit> subUnitsArray = new ArrayList<>();
    
                String[] subUnitParts = KEY_BIDPLAN_SUBUNIT.split(",");
    
                for(int i=0; i < subUnitParts.length; i++)
                {
                    SubUnit subUnitInst = new SubUnit("" , subUnitParts[i] , "");
                    subUnitsArray.add(subUnitInst);
                }
                
                Log.i(AppConstants.TAG , "emp_id: " + emp_id);
                Log.i(AppConstants.TAG , "KEY_BIDPLANID: " + KEY_BIDPLANID);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_TITLE: " + KEY_BIDPLAN_TITLE);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_UNIT: " + KEY_BIDPLAN_UNIT);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_SUBUNIT: " + KEY_BIDPLAN_SUBUNIT);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_CUSTOMER_NAME: " + KEY_BIDPLAN_CUSTOMER_NAME);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_LOCATION: " + KEY_BIDPLAN_LOCATION);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_LOCATION_DESC: " + KEY_BIDPLAN_LOCATION_DESC);
                Log.i(AppConstants.TAG , "KEY_BIDPLAN_VERSION: " + KEY_BIDPLAN_VERSION);
    
                ArrayList<ChildModel> childlist =  new ArrayList<>();
                ChildModel childitem = new ChildModel(KEY_BIDPLAN_VERSION , KEY_BIDPLAN_UNIT , KEY_BIDPLAN_CUSTOMER_NAME ,  KEY_BIDPLAN_LOCATION ,  KEY_BIDPLAN_LOCATION, subUnitsArray , true , KEY_BIDPLAN_LOCATION_DESC , KEY_BIDPLAN_SUBUNIT);
                childlist.add(childitem);
    
                ParentModel parentitem = new ParentModel(KEY_BIDPLANID ,  "" , KEY_BIDPLAN_TITLE , KEY_BIDPLAN_VERSION , childlist, false, true , KEY_BIDPLAN_SUBUNIT , "metricc");
                parentList.add(parentitem);
    
            }while(cursor.moveToNext());
    
            mAdapter.notifyParentDataSetChanged(true);
            mAdapter.notifyDataSetChanged();
            recyclerView.setLayoutManager(new LinearLayoutManager(BidPlanActivity.this));
    
            cursor.close();
        }
       
    }
    
   /* private void prepareData()
    {
        ArrayList<ChildModel> childlist =  new ArrayList<>();
        
        ChildModel childitem = new ChildModel("CHILDD"  , true);
        childlist.add(childitem);
        
        ParentModel parentitem = new ParentModel("NAMEEE" ,  childlist);
        parentList.add(parentitem);
        parentitem = new ParentModel("NAMEEE" ,  childlist);
        parentList.add(parentitem); parentitem = new ParentModel("NAMEEE" ,  childlist);
        parentList.add(parentitem); parentitem = new ParentModel("ONEE" ,  childlist);
        parentList.add(parentitem); parentitem = new ParentModel("sdsda" ,  childlist);
        parentList.add(parentitem); parentitem = new ParentModel("NAMEasgsdgEE" ,  childlist);
        parentList.add(parentitem); parentitem = new ParentModel("aaaaa" ,  childlist);
        parentList.add(parentitem);
            
        mAdapter.notifyParentDataSetChanged(true);
        mAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(BidPlanActivity.this));
    
    }*/
   
   public static void syncBid(Context mContext , final String bidPlanID , String title , String unit , String subUnit , String customerName , String location, String loca_desc , String version, final JSONObject postParams)
   {
        final DBController mDB = new DBController(mContext);
       final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    
       Log.i(AppConstants.TAG , "syncBid getCircuitsOffline postPata=rams:: " + postParams.toString());
       Log.v(AppConstants.TAG , "SubUnits in syncBid:: " + subUnit);
       
       mDB.clearTable(DBController.DB_TABLE_BIDPLANS);
       mDB.addBidPlanEntry(mPrefs.getString(AppConstants.USER_ID , "") , bidPlanID ,title, unit , subUnit , customerName ,location ,  loca_desc , version);
       
       Log.v(AppConstants.TAG , "bidPlanID: " + bidPlanID);
    
       final ProgressDialog dialog = ProgressDialog.show(mContext, "Processing",
                                                         "Syncing bid!", true);
    
       final RequestQueue mRequestQueue;
       mRequestQueue = MySingleton.getInstance(mContext).getRequestQueue();
       StringRequest postRequest = new StringRequest(Request.Method.POST,
                                                     AppConstants.BASE_URL + "getCircuitsOffline"
               ,
                                                     new Response.Listener<String>() {
                                                         @Override
                                                         public void onResponse(String response) {
    
                                                             try
                                                             {
                                                                 JSONObject jsonObj = new JSONObject(response);
    
                                                                 boolean success = jsonObj.getBoolean("success");
                                                                 String message = jsonObj.getString("message");
    
                                                                 if(!success)
                                                                 {
                                                                     
                                                                 }
                                                                 else
                                                                 {
                                                                    // mDB.clearTable(DBController.DB_TABLE_CIRCUITS_JSON);
                                                                     Log.d(AppConstants.TAG , "getCircuitsOffline response while syncing: " + response);
    
                                                                     JSONObject result = jsonObj.getJSONObject("result");
                                                                     JSONArray circuitsArray = result.getJSONArray("Circuits");
                                                                     JSONArray circuit_subUnits = result.optJSONArray("Subunits");
    
                                                                     Log.v(AppConstants.TAG , "circuitsArray szie: " + circuitsArray.length());
                                                                     
                                                                     mDB.clearTable(DBController.DB_TABLE_CIRCUIT_PATH);
                                                                     
                                                                     for(int i=0; i<circuitsArray.length(); i++)
                                                                     {
                                                                         JSONObject circuitObj = circuitsArray.getJSONObject(i);
                                                                         String circuit_id = circuitObj.opt("Id").toString();
                                                                         String circuit_title = circuitObj.opt("Title").toString();
                                                                         String circuit_milage = circuitObj.opt("Milage").toString();
                                                                         String circuit_linePath = circuitObj.optJSONArray("CircuitSurveyPath").toString();
                                                                         String circuit_lineType = circuitObj.opt("LineType").toString();
                                                                         String circuit_isDelegated = circuitObj.opt("IsDelegated").toString();
                                                                         String circuit_SurveysCount = circuitObj.opt("SurveysCount").toString();
                                                                         String circuit_EquipmentNotes = circuitObj.opt("EquipmentNotes").toString();
                                                                         
                                                                         mDB.addCircuitPath(mPrefs.getString(AppConstants.USER_ID , "") , bidPlanID , circuit_id , circuit_title , circuit_milage , circuit_linePath , circuit_lineType , circuit_isDelegated , circuit_subUnits.toString() , circuit_SurveysCount , circuit_EquipmentNotes);
                                                                     }
    
                                                                     JSONArray circuitTypesArray = result.getJSONArray("CircuitTypes");
                                                                     mPrefs.edit().putString(AppConstants.CIRCUIT_TYPES , circuitTypesArray.toString()).commit();
                                                                     
                                                                    /* mDB.clearTable(DBController.DB_TABLE_CIRCUITS_JSON);
                                                                     mDB.addCircuitsJSON(mPrefs.getString(AppConstants.USER_ID , "") , bidPlanID , response);*/
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
    
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(AppConstants.TAG , "onResume");
    
        parentList.clear();
        if(AppConstants.isNetworkAvailable(BidPlanActivity.this))
        {
            getBidPlans();
        }
        else
            getBidPlansLocally();
    }
}
