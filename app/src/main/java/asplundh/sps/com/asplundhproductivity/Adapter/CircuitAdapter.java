package asplundh.sps.com.asplundhproductivity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import asplundh.sps.com.asplundhproductivity.Activity.LocationDemoActivity;
import asplundh.sps.com.asplundhproductivity.Model.Circuit;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/12/2017.
 */

public class CircuitAdapter extends RecyclerView.Adapter<CircuitAdapter.MyViewHolder>
{
    private List<Circuit> circuitsList;
    private Context context;
    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, area , type;
        ImageView isAssigned;
        
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            area = (TextView) view.findViewById(R.id.area);
            type = (TextView) view.findViewById(R.id.type);
            isAssigned = (ImageView) view.findViewById(R.id.isAssigned);
            
            context = view.getContext();
    
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.v(AppConstants.TAG , "CLICKEDDD: " + getAdapterPosition() + "   " + circuitsList.get(getAdapterPosition()).getId());
    
                    Log.v(AppConstants.TAG , "CLICKEDDD LINE path: " + getAdapterPosition() + "   " + circuitsList.get(getAdapterPosition()).getLinePath());
    
                    if(AppConstants.isNetworkAvailable(context))
                    {
                        Intent i = new Intent(context , LocationDemoActivity.class);
                        i.putExtra("CIRCUITID" , circuitsList.get(getAdapterPosition()).getId());
                        i.putExtra("BIDPLANID" , circuitsList.get(getAdapterPosition()).getBidPlanID());
                        i.putExtra("CIRCUITTITLE" , circuitsList.get(getAdapterPosition()).getName());
    
                        context.startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(context , "Network not available!",
                                       Toast.LENGTH_LONG).show();
                    }
                   
                }
            });
        }
    }
    
    
    public CircuitAdapter(List<Circuit> circuitsList) {
        this.circuitsList = circuitsList;
    }
    
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_circuit, parent, false);
        
        return new MyViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Circuit circuit = circuitsList.get(position);
        holder.name.setText(circuit.getName());
        
        holder.type.setText(circuit.getType());
        
        if(!circuit.getArea().equalsIgnoreCase(""))
        {
            double value = Double.parseDouble(circuit.getArea());
            double valueRounded = Math.round(value * 100D) / 100D;
            holder.area.setText(String.format("%.1f", valueRounded) + " Miles");
        }
        
        String isAssigned = circuit.getIsAssigned();
        if(isAssigned.equalsIgnoreCase("0"))
            holder.isAssigned.setVisibility(View.INVISIBLE);
        else if(isAssigned.equalsIgnoreCase("1"))
            holder.isAssigned.setVisibility(View.VISIBLE);
    
    }
    
    @Override
    public int getItemCount() {
        return circuitsList.size();
    }
}
