package asplundh.sps.com.asplundhproductivity.ExpandableCircuit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import asplundh.sps.com.asplundhproductivity.Activity.LocationDemoActivity;
import asplundh.sps.com.asplundhproductivity.R;

/**
 * Created by Malik Muhamad Qirtas on 10/25/2017.
 */

public class CircuitOuterViewHolder extends ParentViewHolder
{
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 270f;
    
    @NonNull
    private final ImageView mArrowExpandImageView;
    private TextView title ;
    private ImageView iv_is_delegation;
    Context mContext;
    
    
    public CircuitOuterViewHolder(@NonNull View itemView , Context context)
    {
        super(itemView);
        mContext = context;
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_Arrow);
        title = (TextView) itemView.findViewById(R.id.tv_title);
        iv_is_delegation = (ImageView) itemView.findViewById(R.id.iv_is_delegated);
    }
    
    public void bind(@NonNull final CircuitParentModel recipe)
    {
        title.setText(recipe.getTitle());
        String isDelegated = recipe.isDelegated();
        
        if(isDelegated.equalsIgnoreCase("0"))
            iv_is_delegation.setVisibility(View.VISIBLE);
        else
            iv_is_delegation.setVisibility(View.VISIBLE);
    
        mArrowExpandImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(mContext , LocationDemoActivity.class);
                i.putExtra("CIRCUITID" , recipe.getId());
                i.putExtra("BIDPLANID" , recipe.getBidPlanID());
                i.putExtra("CIRCUITTITLE" , recipe.getTitle());
    
                mContext.startActivity(i);
            }
        });
    }
    
    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                mArrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                mArrowExpandImageView.setRotation(INITIAL_POSITION);
            }
        }*/
    }
    
    @Override
    public void onExpansionToggled(boolean expanded)
    {
        super.onExpansionToggled(expanded);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                                                      INITIAL_POSITION,
                                                      RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                                      RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                                                      INITIAL_POSITION,
                                                      RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                                      RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }
            
            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            mArrowExpandImageView.startAnimation(rotateAnimation);
        }*/
    }
}
