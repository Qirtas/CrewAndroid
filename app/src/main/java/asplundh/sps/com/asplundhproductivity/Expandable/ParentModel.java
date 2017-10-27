package asplundh.sps.com.asplundhproductivity.Expandable;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class ParentModel implements Parent<ChildModel>
{
    private String id , customerBidID , title , versionNumber , subUnits , unit;
    private List<ChildModel> mIngredients;
    boolean isOnline , isSynced;
    
    public ParentModel(String id , String customerBidID , String name, String versionNumber ,  List<ChildModel> mIngredients , boolean isOnline, boolean isSynced , String subUnits , String unit)
    {
        this.id = id;
        this.customerBidID = customerBidID;
        this.title = name;
        this.versionNumber = versionNumber;
        this.mIngredients = mIngredients;
        this.isOnline = isOnline;
        this.isSynced = isSynced;
        this.subUnits = subUnits;
        this.unit = unit;
    }
    
    public String getUnit()
    {
        return unit;
    }
    
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    
    public String getSubUnits()
    {
        return subUnits;
    }
    
    public void setSubUnits(String subUnits)
    {
        this.subUnits = subUnits;
    }
    
    public boolean isSynced()
    {
        return isSynced;
    }
    
    public void setSynced(boolean synced)
    {
        isSynced = synced;
    }
    
    public boolean isOnline()
    {
        return isOnline;
    }
    
    public void setOnline(boolean online)
    {
        isOnline = online;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getCustomerBidID()
    {
        return customerBidID;
    }
    
    public void setCustomerBidID(String customerBidID)
    {
        this.customerBidID = customerBidID;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getVersionNumber()
    {
        return versionNumber;
    }
    
    public void setVersionNumber(String versionNumber)
    {
        this.versionNumber = versionNumber;
    }
    
    @Override
    public List<ChildModel> getChildList() {
        return mIngredients;
    }
    
    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
    
    public ChildModel getIngredient(int position) {
        return mIngredients.get(position);
    }
    
    public boolean isVegetarian() {
        for (ChildModel ingredient : mIngredients) {
            if (!ingredient.isVegetarian()) {
                return false;
            }
        }
        return true;
    }
}
