package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_plan);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
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
        getBidPlans();
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
    
    private void getBidPlans()
    {
        final ProgressDialog dialog = ProgressDialog.show(this, "Processing",
                                                          getResources().getString(R.string.getting_data), true);
        
        Log.i(AppConstants.TAG , "getBidPlans URL: " + BASE_URL + "getAllBidPlansByUser?id=1");
    
        RequestQueue mRequestQueue;
        mRequestQueue = MySingleton.getInstance(this).getRequestQueue();
        StringRequest postRequest = new StringRequest(Request.Method.GET,
                                                      BASE_URL + "getAllBidPlansByUser?id=1"
            
                ,
                                                      new Response.Listener<String>() {
                                                          @Override
                                                          public void onResponse(String response) {
                    
                                                              Log.d(AppConstants.TAG , "getBidPlans ressponse: " + response);
                                                              bidPlansJson = response;
    
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
                                                                      JSONArray array = jsonObj.getJSONArray("result");
                                                                      
                                                                      for(int i=0; i<array.length(); i++)
                                                                      {
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
                                                                          
                                                                          JSONArray subUnits = obj.getJSONArray("SubUnits");
                                                                          ArrayList<SubUnit> subunits_list = new ArrayList<>();
                                                                          
                                                                          for(int j=0; j<subUnits.length(); j++)
                                                                          {
                                                                              JSONObject subunit = subUnits.getJSONObject(j);
                                                                              String Id_subunit = subunit.opt("Id").toString();
                                                                              String Title_subunit = subunit.opt("Title").toString();
    
                                                                              SubUnit subUnit = new SubUnit(Id_subunit , Title_subunit , id);
                                                                              subunits_list.add(subUnit);
    
                                                                              Log.v(AppConstants.TAG , "subunit: " + subunit);
                                                                          }
    
                                                                          //VERSIONS
                                                                          JSONArray Versions = obj.getJSONArray("Versions");
                                                                          for(int k=0; k<Versions.length(); k++)
                                                                          {
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
    
                                                                              JSONArray subUnits_version = version.getJSONArray("SubUnits");
                                                                              ArrayList<SubUnit> subunits_list_version = new ArrayList<>();
                                                                              
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
    
                                                                              Log.w(AppConstants.TAG , "version: " + version);
    
                                                                              ArrayList<ChildModel> childlist =  new ArrayList<>();
    
                                                                              ChildModel childitem = new ChildModel(VersionNumber_version , Metric_version , CustomerName_version ,  City_version ,  Country_version, subunits_list_version, true , LocDescription_version);
                                                                              childlist.add(childitem);
    
                                                                              ParentModel parentitem = new ParentModel(id_version ,  CustomerBidId_version , Title_version,VersionNumber_version , childlist );
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
    
                                                                          ChildModel childitem = new ChildModel(VersionNumber , Metric , CustomerName ,  City ,  Country, subunits_list , true , LocationDescription);
                                                                          childlist.add(childitem);
    
                                                                          ParentModel parentitem = new ParentModel(id ,  CustomerBidId , Title,VersionNumber , childlist );
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
            
            }
        
        }) {
        
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                80000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    
        mRequestQueue.add(postRequest);
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
}
