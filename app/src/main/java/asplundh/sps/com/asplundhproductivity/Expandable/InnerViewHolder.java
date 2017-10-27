package asplundh.sps.com.asplundhproductivity.Expandable;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class InnerViewHolder extends ChildViewHolder
{
    private TextView tv_versionNo , tv_unit , tv_sub_unit , tv_customer_name , tv_location , tv_location_description ;
    
    public InnerViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_versionNo = (TextView) itemView.findViewById(R.id.tv_versionNo);
        tv_unit = (TextView) itemView.findViewById(R.id.tv_unit);
        tv_sub_unit = (TextView) itemView.findViewById(R.id.tv_sub_unit);
        tv_customer_name = (TextView) itemView.findViewById(R.id.tv_customer_name);
        tv_location = (TextView) itemView.findViewById(R.id.tv_location);
        tv_location_description = (TextView) itemView.findViewById(R.id.tv_location_description);
    }
    
    public void bind(@NonNull ChildModel ingredient)
    {
        tv_versionNo.setText(ingredient.getVersionNumber());
        tv_unit.setText(ingredient.getUnit());
        
        tv_customer_name.setText(ingredient.getCustomerName());
        tv_location.setText(ingredient.getCity() + "," + ingredient.getCountry());
        tv_location_description.setText(ingredient.getLocation_description());
    
        ArrayList<SubUnit> subunitsList = new ArrayList<>();
        subunitsList = ingredient.getSubunitsList();
    
        Log.v(AppConstants.TAG , "subunitsList in child: " + subunitsList);
        
        String subunits = "";
        
        /*for(int i=0; i<subunitsList.size(); i++)
        {
            if(i == 0)
            {
                subunits = subunitsList.get(i).getTitle().toString();
                Log.i(AppConstants.TAG , "subunits: " + subunits);
            }
                
            else
            {
                subunits = subunits + " , " + subunitsList.get(i).getTitle().toString();
                Log.i(AppConstants.TAG , "subunits: " + subunits);
            }
            
        }*/
        
        String subUnitsJson = ingredient.getSubUnit_json();
        
        try
        {
            JSONArray subUnits_array = new JSONArray(subUnitsJson);
            
            for(int i=0; i<subUnits_array.length(); i++)
            {
                JSONObject obj = subUnits_array.getJSONObject(i);
    
                String title = obj.opt("Title").toString();
    
                if(i == 0)
                {
                    subunits = title;
                }
                else
                {
                    subunits = subunits + " , " + title;
                }
            }
        }
        catch (JSONException e)
        {
            Log.e(AppConstants.TAG , "JSONException while subunits json:" + e.toString());
        }
       
        
        tv_sub_unit.setText(subunits);
    }
    
}
