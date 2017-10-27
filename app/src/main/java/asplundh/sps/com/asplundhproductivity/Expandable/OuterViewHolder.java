package asplundh.sps.com.asplundhproductivity.Expandable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import asplundh.sps.com.asplundhproductivity.Activity.BidPlanActivity;
import asplundh.sps.com.asplundhproductivity.Activity.CircuitsActivity;
import asplundh.sps.com.asplundhproductivity.Activity.MapDownloadActivity;
import asplundh.sps.com.asplundhproductivity.Helper.DBController;
import asplundh.sps.com.asplundhproductivity.Model.SubUnit;
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
    ImageView iv_sync;
    
    public OuterViewHolder(@NonNull View itemView , Context context) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.tv_recepie);
        versionNumber = (TextView) itemView.findViewById(R.id.tv_versionNo);
        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_Arrow);
        iv_sync = (ImageView) itemView.findViewById(R.id.iv_sync);
        
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
        
      /*  if(recipe.getVersionNumber().equalsIgnoreCase("0"))
        {
            versionNumber.setVisibility(View.GONE);
        }
         else*/
        
        /*if(!recipe.isSynced())
            iv_sync.setVisibility(View.VISIBLE);
        else
            iv_sync.setVisibility(View.GONE);*/
      
        versionNumber.setText("V:" +recipe.getVersionNumber());
        
        mArrowExpandImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String subUnits = recipe.getSubUnits().toString();
                
                Log.d(AppConstants.TAG , "subUnitsArray creation: " + subUnits.toString());
    
                mPrefs.edit().putString(AppConstants.SUB_UNITS , subUnits.toString()).commit();
                mPrefs.edit().putString(AppConstants.UNIT , recipe.getUnit()).commit();
                
                Log.w(AppConstants.TAG , "mArrowExpandImageView CLICKED  "+ getAdapterPosition() + "   BIDPLANID " + recipe.getId());
                mPrefs.edit().putString(AppConstants.BID_PLAN_ID , recipe.getId()).commit();
                
                if(AppConstants.isNetworkAvailable(mContext))
                {
                    Intent i = new Intent(mContext , CircuitsActivity.class);
                    i.putExtra("BIDPLANID" , recipe.getId());
                    mContext.startActivity(i);
                }
                else
                {
                    Toast.makeText(mContext , "Network not available!",
                                   Toast.LENGTH_LONG).show();
                }
                
            }
        });
    
        iv_sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.w(AppConstants.TAG , "iv_sync");
    
                DBController mDB = new DBController(mContext.getApplicationContext());
                Cursor c = mDB.getAllData(DBController.DB_TABLE_BIDPLANS);
                int syncedBidsCount = c.getCount();
                
                if(syncedBidsCount > 0)
                {
                    Toast.makeText(mContext, "You can sync single bid at a time",
                                   Toast.LENGTH_LONG).show();
    
                    showNewSyncBidDialog(recipe.getChildList() , recipe.getId(), recipe.getTitle() , recipe.getSubUnits());
                }
                else
                {
                    boolean isBidAlreadySynced = mDB.isBidAlreadySynced(recipe.getId());
    
                    if(!isBidAlreadySynced)
                    {
                        String subUnits = "";
        
                        List<ChildModel> childList = recipe.getChildList();
                        for(int i=0; i<childList.size(); i++)
                        {
                            Log.i(AppConstants.TAG  ,"CHILD LIST ITEM IN VIEW HOLDER: " + childList.get(i).getSubunitsList());
            
                            ArrayList<SubUnit> sunUnitsList =  childList.get(i).getSubunitsList();
                            for(int j=0; j<sunUnitsList.size(); j++)
                            {
                                Log.i(AppConstants.TAG , "sunUnitsList: " + sunUnitsList.get(j).getTitle());
                                if(j == 0)
                                    subUnits = sunUnitsList.get(j).getTitle();
                                else
                                    subUnits =  subUnits + "," + sunUnitsList.get(j).getTitle();
                            }
                        }
        
                        Log.w(AppConstants.TAG , "SUBUNIT LIST in VIEW HODLER: " + childList.get(0).getSubunitsList().toString());
        
                        BidPlanActivity.syncBid(mContext , recipe.getId(), recipe.getTitle() , childList.get(0).getUnit(), recipe.getSubUnits() , childList.get(0).getCustomerName() , childList.get(0).getCity() + "," + childList.get(0).getCountry() , childList.get(0).getLocation_description() , childList.get(0).getVersionNumber(), getCircuitPostParams(recipe.getId()));
                        Log.v(AppConstants.TAG , "Location is: " + childList.get(0).getCity());
                        
                        Intent i = new Intent(mContext , MapDownloadActivity.class);
                        i.putExtra(AppConstants.CITY_NAME , "Islamabad");
                        mContext.startActivity(i);
                        
                       // getCityBounds("Karachi");
                        
                    }
                    else
                    {
                        Toast.makeText(mContext, "This bid already synced",
                                       Toast.LENGTH_LONG).show();
                    }
                }
                
                
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
    
    
    public JSONObject getCircuitPostParams(String BidPlanID)
    {
        JSONObject obj = new JSONObject();
        JSONObject credentialsObj = new JSONObject();
        
        try
        {
            credentialsObj.put("userId" , mPrefs.getString(AppConstants.USER_ID , ""));
            credentialsObj.put("bidPlanId" , BidPlanID);
        }
        catch (JSONException e)
        {
            
        }
        
        return  credentialsObj;
    }
    
    public void showNewSyncBidDialog(final List<ChildModel> childList, final String bidPlanID, final String bidPlanTitle , final String subUnitss)
    {
        final Dialog dialog = new Dialog(mContext, R.style.mapbox_AlertDialogStyle);
        dialog.setContentView(R.layout.dialog_new_bid_sync);
        dialog.setCanceledOnTouchOutside(false);
    
        RelativeLayout btn_yes = (RelativeLayout) dialog.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String subUnits = "";
    
                for(int i=0; i<childList.size(); i++)
                {
                    Log.i(AppConstants.TAG  ,"CHILD LIST ITEM IN VIEW HOLDER: " + childList.get(i).getSubunitsList());
        
                    ArrayList<SubUnit> sunUnitsList =  childList.get(i).getSubunitsList();
                    for(int j=0; j<sunUnitsList.size(); j++)
                    {
                        Log.i(AppConstants.TAG , "sunUnitsList: " + sunUnitsList.get(j).getTitle());
                        if(j == 0)
                            subUnits = sunUnitsList.get(j).getTitle();
                        else
                            subUnits =  subUnits + "," + sunUnitsList.get(j).getTitle();
                    }
                }
    
                Log.w(AppConstants.TAG , "SUBUNIT LIST in VIEW HODLER: " + childList.get(0).getSubunitsList().toString());
    
                BidPlanActivity.syncBid(mContext , bidPlanID, bidPlanTitle , childList.get(0).getUnit(), subUnitss , childList.get(0).getCustomerName() , childList.get(0).getCity() + "," + childList.get(0).getCountry() , childList.get(0).getLocation_description() , childList.get(0).getVersionNumber(), getCircuitPostParams(bidPlanID));
                Log.v(AppConstants.TAG , "Location is: " + childList.get(0).getCity());
    
                Intent i = new Intent(mContext , MapDownloadActivity.class);
                i.putExtra(AppConstants.CITY_NAME , "Islamabad");
                mContext.startActivity(i);
                
                dialog.dismiss();
            }
        });
    
        RelativeLayout btn_no = (RelativeLayout) dialog.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    
        dialog.show();
    }
    
}
