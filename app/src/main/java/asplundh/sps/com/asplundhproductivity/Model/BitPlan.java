package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/12/2017.
 */

public class BitPlan
{
    String title, unit;
    
    public BitPlan(String title, String unit)
    {
        this.title = title;
        this.unit = unit;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getUnit()
    {
        return unit;
    }
    
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
}
