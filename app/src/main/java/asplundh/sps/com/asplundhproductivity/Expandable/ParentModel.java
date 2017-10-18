package asplundh.sps.com.asplundhproductivity.Expandable;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class ParentModel implements Parent<ChildModel>
{
    private String id , customerBidID , title , versionNumber;
    private List<ChildModel> mIngredients;
    
    public ParentModel(String id , String customerBidID , String name, String versionNumber ,  List<ChildModel> mIngredients)
    {
        this.id = id;
        this.customerBidID = customerBidID;
        this.title = name;
        this.versionNumber = versionNumber;
        this.mIngredients = mIngredients;
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
