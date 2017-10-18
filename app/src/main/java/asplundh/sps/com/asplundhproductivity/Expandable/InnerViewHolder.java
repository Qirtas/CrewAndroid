package asplundh.sps.com.asplundhproductivity.Expandable;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import java.util.ArrayList;

import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
import asplundh.sps.com.asplundhproductivity.R;

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
        tv_location.setText(ingredient.getCity() + " " + ingredient.getCountry());
        tv_location_description.setText(ingredient.getLocation_description());
    
        ArrayList<SubUnit> subunitsList = new ArrayList<>();
        subunitsList = ingredient.getSubunitsList();
        String subunits = "";
        
        for(int i=0; i<subunitsList.size(); i++)
        {
            if(i == 0)
                subunits = subunitsList.get(i).getTitle().toString();
            else
                subunits = subunits + " , " + subunitsList.get(i).getTitle().toString();
            
        }
        tv_sub_unit.setText(subunits);
    }
    
}
