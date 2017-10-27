package asplundh.sps.com.asplundhproductivity.ExpandableCircuit;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.List;

/**
 * Created by Malik Muhamad Qirtas on 10/25/2017.
 */

public class CircuitParentModel implements Parent<CircuitChildModel>
{
    public String id , title , isDelegated , bidPlanID;
    private List<CircuitChildModel> mIngredients;
    
    public CircuitParentModel(String id, String title, String isDelegated, List<CircuitChildModel> mIngredients , String bidPlanID)
    {
        this.id = id;
        this.title = title;
        this.isDelegated = isDelegated;
        this.mIngredients = mIngredients;
        this.bidPlanID = bidPlanID;
    }
    
    public String getBidPlanID()
    {
        return bidPlanID;
    }
    
    public void setBidPlanID(String bidPlanID)
    {
        this.bidPlanID = bidPlanID;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String isDelegated()
    {
        return isDelegated;
    }
    
    public void setDelegated(String delegated)
    {
        isDelegated = delegated;
    }
    
    public List<CircuitChildModel> getmIngredients()
    {
        return mIngredients;
    }
    
    public void setmIngredients(List<CircuitChildModel> mIngredients)
    {
        this.mIngredients = mIngredients;
    }
    
    @Override
    public List<CircuitChildModel> getChildList() {
        return mIngredients;
    }
    
    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
    
    public CircuitChildModel getIngredient(int position) {
        return mIngredients.get(position);
    }
    
    public boolean isVegetarian() {
        for (CircuitChildModel ingredient : mIngredients) {
            if (!ingredient.ismIsVegetarian()) {
                return false;
            }
        }
        return true;
    }
}
