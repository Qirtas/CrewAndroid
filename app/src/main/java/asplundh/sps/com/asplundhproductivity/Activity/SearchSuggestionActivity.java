package asplundh.sps.com.asplundhproductivity.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asplundh.sps.com.asplundhproductivity.Expandable.ChildModel;
import asplundh.sps.com.asplundhproductivity.Expandable.ParentModel;
import asplundh.sps.com.asplundhproductivity.Expandable.RecipeAdapter;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitChildModel;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitExpandAdapter;
import asplundh.sps.com.asplundhproductivity.ExpandableCircuit.CircuitParentModel;
import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class SearchSuggestionActivity extends AppCompatActivity implements View.OnClickListener
{
    ArrayList<ParentModel> parentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter mAdapter;
    EditText et_search;
    List<ParentModel> filteredList;
    
   // private List<Circuit> circuitsList = new ArrayList<>();
   // private CircuitAdapter mCircuitAdapter;
    
    List<CircuitParentModel> circuitsFilteredList;
    SharedPreferences mPrefs;
    
    private CircuitExpandAdapter mAdapterExpand;
    ArrayList<CircuitParentModel> parentListCircuit = new ArrayList<>();
    String bidplanid = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_suggestion);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        ImageView back_ic = (ImageView) findViewById(R.id.back_ic);
        back_ic.setOnClickListener(this);
        ImageView logout_ic = (ImageView) findViewById(R.id.logout_ic);
        logout_ic.setOnClickListener(this);
        
        et_search = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.suggestions_recycler_view);
        
        String searchType = getIntent().getStringExtra("TYPE");
        bidplanid = getIntent().getStringExtra("BIDPLANID");
    
    
        if(searchType.equalsIgnoreCase("BIDPLANS"))
        {
            mAdapter = new RecipeAdapter( SearchSuggestionActivity.this , parentList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            getBidsListLocally();
            addBitPlanTextListener();
        }
        else
        {
            Log.i(AppConstants.TAG , "CIRCUITSS");
    
            mAdapterExpand = new CircuitExpandAdapter(SearchSuggestionActivity.this  , parentListCircuit);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapterExpand);
            getCircuitsLocally();
            addCircuitsTextListener();
        }
        
    }
    
    public void addCircuitsTextListener()
    {
        et_search.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {}
        
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
            public void onTextChanged(CharSequence query, int start, int before, int count) {
            
                query = query.toString().toLowerCase();
    
                circuitsFilteredList = new ArrayList<>();
            
                for (int i = 0; i < parentListCircuit.size(); i++)
                {
                    final String text = parentListCircuit.get(i).getTitle().toLowerCase();
                    if (text.contains(query)) {
    
                        circuitsFilteredList.add(parentListCircuit.get(i));
                    }
                }
            
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchSuggestionActivity.this));
                mAdapterExpand = new CircuitExpandAdapter(SearchSuggestionActivity.this , circuitsFilteredList);
            
                recyclerView.setAdapter(mAdapterExpand);
                mAdapterExpand.notifyDataSetChanged();  // data set changed
            }
        });
    }
    
    public void addBitPlanTextListener()
    {
        et_search.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s) {}
            
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                
                query = query.toString().toLowerCase();
                
                filteredList = new ArrayList<>();
                
                for (int i = 0; i < parentList.size(); i++) {
                    
                    final String text = parentList.get(i).getTitle().toLowerCase();
                    if (text.contains(query)) {
    
                        filteredList.add(parentList.get(i));
                    }
                }
                
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchSuggestionActivity.this));
                mAdapter = new RecipeAdapter(SearchSuggestionActivity.this , filteredList);
                
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
   
    
    private void getBidsListLocally()
    {
        try
        {
            JSONObject jsonObj = new JSONObject(BidPlanActivity.bidPlansJson);
        
            boolean success = jsonObj.getBoolean("success");
            String message = jsonObj.getString("message");
        
            if(!success)
            {
                Toast.makeText(SearchSuggestionActivity.this , message,
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
                    
                        SubUnit subUnit = new SubUnit(Id_subunit , Title_subunit , id );
                        subunits_list.add(subUnit);
                    
                        Log.v(AppConstants.TAG , "subunit: " + subunit);
                    }
                
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
                    
                        JSONArray subUnits_version = obj.getJSONArray("SubUnits");
                        ArrayList<SubUnit> subunits_list_version = new ArrayList<>();
                    
                        for(int j=0; j<subUnits_version.length(); j++)
                        {
                            JSONObject subunit = subUnits_version.getJSONObject(j);
                            String Id_subunit = subunit.opt("Id").toString();
                            String Title_subunit = subunit.opt("Title").toString();
                        
                            SubUnit subUnit = new SubUnit(Id_subunit , Title_subunit , id_version);
                            subunits_list.add(subUnit);
                        
                            Log.v(AppConstants.TAG , "subunit: " + subunit);
                        }
                    
                        Log.w(AppConstants.TAG , "version: " + version);
                    
                        ArrayList<ChildModel> childlist =  new ArrayList<>();
                    
                        ChildModel childitem = new ChildModel(VersionNumber_version , Metric_version , CustomerName_version ,  City_version ,  Country_version, subunits_list_version, true , LocDescription_version , subUnits_version.toString());
                        childlist.add(childitem);
                    
                        ParentModel parentitem = new ParentModel(id_version ,  CustomerBidId_version , Title_version,VersionNumber_version , childlist , true , false , subUnits_version.toString() , Metric_version);
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
                
                    ChildModel childitem = new ChildModel(VersionNumber , Metric , CustomerName ,  City ,  Country, subunits_list,true , LocationDescription , subUnits.toString());
                    childlist.add(childitem);
                
                    ParentModel parentitem = new ParentModel(id ,  CustomerBidId , Title,VersionNumber , childlist , true , false , subUnits.toString() , Metric);
                    parentList.add(parentitem);
                
                    mAdapter.notifyParentDataSetChanged(true);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchSuggestionActivity.this));
                }
            }
        }
        catch (JSONException e)
        {
            Log.e(AppConstants.TAG , "JSONException: " + e.toString());
        }
    }
    
    public void getCircuitsLocally()
    {
        try
        {
            JSONObject jsonObj = new JSONObject(CircuitsActivity.circuitsJson);
        
            boolean success = jsonObj.getBoolean("success");
            String message = jsonObj.getString("message");
        
            
            JSONObject result = jsonObj.getJSONObject("result");
            JSONArray circuitsArray = result.getJSONArray("Circuits");
            
                for(int i=0; i<circuitsArray.length(); i++)
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
    
                    CircuitParentModel parentitem = new CircuitParentModel(circuitID , circuitTitle , circuitIsDelegated, childlist , bidplanid);
                    parentListCircuit.add(parentitem);
                    
                    mAdapterExpand.notifyParentDataSetChanged(true);
                    mAdapterExpand.notifyDataSetChanged();
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchSuggestionActivity.this));
                }
            
                /*JSONArray circuitTypesArray = result.getJSONArray("CircuitTypes");
            
                for(int i=0; i<circuitTypesArray.length(); i++)
                {
                    JSONObject object = circuitTypesArray.getJSONObject(i);
                    String circuitTypeID =  object.opt("Id").toString();
                    String circuitTypeTitle =  object.opt("Title").toString();
                
                    CircuitType circuitType = new CircuitType(circuitTypeID , circuitTypeTitle);
                    circuitTypesList.add(circuitType);
                }*/
            
        }
        catch (JSONException e)
        {
        
        }
      
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
            
            case R.id.logout_ic:
                Intent intent = new Intent(SearchSuggestionActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }
}
