package asplundh.sps.com.asplundhproductivity.Expandable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import asplundh.sps.com.asplundhproductivity.Activity.CircuitsActivity;
import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class OuterViewHolder extends ParentViewHolder
{
    
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 270f;
    
    @NonNull
    private final ImageView mArrowExpandImageView;
    private TextView title , versionNumber;
    public Context mContext;
    SharedPreferences mPrefs;
    
    public OuterViewHolder(@NonNull View itemView , Context context) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.tv_recepie);
        versionNumber = (TextView) itemView.findViewById(R.id.tv_versionNo);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_Arrow);
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
              //  Log.v(AppConstants.TAG , "ROW CLICKED:  " + holder.getAdapterPosition());
                
            }
        });
    
        mArrowExpandImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.v(AppConstants.TAG , "mArrowExpandImageView CLICKED" + getAdapterPosition());
            }
        });
    }
    
    public void bind(@NonNull final ParentModel recipe) {
        
        title.setText(recipe.getTitle());
        
        if(recipe.getVersionNumber().equalsIgnoreCase("0"))
        {
            versionNumber.setVisibility(View.GONE);
        }
         else
            versionNumber.setText("V:" +recipe.getVersionNumber());
        
        
        mArrowExpandImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.w(AppConstants.TAG , "mArrowExpandImageView CLICKED  "+ getAdapterPosition() + "   BIDPLANID " + recipe.getId());
                mPrefs.edit().putString(AppConstants.BID_PLAN_ID , recipe.getId()).commit();
                Intent i = new Intent(mContext , CircuitsActivity.class);
                i.putExtra("BIDPLANID" , recipe.getId());
                mContext.startActivity(i);
                
            }
        });
        
        /*if(Integer.parseInt(recipe.getWorkedHours()) > 1)
            tv_worked.setText(recipe.getWorkedHours() + "hrs");
        else
            tv_worked.setText(recipe.getWorkedHours() + "hr");*/
        
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
