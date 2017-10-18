package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/16/2017.
 */

public class SubUnit
{
    String id , title  , bidPlanID;
    
    public SubUnit(String id, String title , String bidPlanID)
    {
        this.id = id;
        this.title = title;
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
}
