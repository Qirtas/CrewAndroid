package asplundh.sps.com.asplundhproductivity.ViewHolder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import asplundh.sps.com.asplundhproductivity.Expandable.ChildModel;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class InnerViewHolder extends ChildViewHolder
{
    private TextView versionNo , totalMiles , circuitType , isDelegated , customerName , location , locationDescription;
    
    public InnerViewHolder(@NonNull View itemView)
    {
        super(itemView);
    }
    
    public void bind(@NonNull ChildModel ingredient) {
        
      
        
    }
}
