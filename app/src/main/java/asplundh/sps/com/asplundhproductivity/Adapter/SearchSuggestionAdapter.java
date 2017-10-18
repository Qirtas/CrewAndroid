package asplundh.sps.com.asplundhproductivity.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import asplundh.sps.com.asplundhproductivity.Model.BitPlan;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/12/2017.
 */

public class SearchSuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<BitPlan> bitPlanList;
    Activity activity;
    
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title, unit;
        LinearLayout row_lay;
        
        public ItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            row_lay = (LinearLayout) view.findViewById(R.id.lay);
            row_lay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int position = (int) view.getTag();
                    
                    Log.i(AppConstants.TAG, "ITEMMM: " + view.getTag() + " " + bitPlanList.get(position).getTitle());
                    
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",bitPlanList.get(position).getTitle());
                    activity.setResult(Activity.RESULT_OK, returnIntent);
                    activity.finish();
                }
            });
        }
        
    }
    
    public SearchSuggestionAdapter(List<BitPlan> List, Activity activity) {
        this.bitPlanList = List;
        this.activity = activity;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        
      
        View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_bid_plans, parent, false);
                
        return new ItemViewHolder(itemView);
            
        
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ItemViewHolder viewHolder0 = (ItemViewHolder)holder;
        BitPlan item = bitPlanList.get(position);
        ((ItemViewHolder) holder).title.setText(item.getTitle());
        ((ItemViewHolder) holder).row_lay.setTag(position);
        
    }
    
   
    
    @Override
    public int getItemCount() {
        return bitPlanList.size();
    }
}
