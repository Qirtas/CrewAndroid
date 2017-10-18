package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/12/2017.
 */

public class Circuit
{
    String id, name, area , type , isAssigned , LinePath , bidPlanID;
    
    public Circuit(String id , String name, String area, String type, String isAssigned , String LinePath , String bidPlanID)
    {
        this.id = id;
        this.name = name;
        this.area = area;
        this.type = type;
        this.isAssigned = isAssigned;
        this.LinePath = LinePath;
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
    
    public String getLinePath()
    {
        return LinePath;
    }
    
    public void setLinePath(String linePath)
    {
        LinePath = linePath;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getArea()
    {
        return area;
    }
    
    public void setArea(String area)
    {
        this.area = area;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getIsAssigned()
    {
        return isAssigned;
    }
    
    public void setIsAssigned(String isAssigned)
    {
        this.isAssigned = isAssigned;
    }
}
