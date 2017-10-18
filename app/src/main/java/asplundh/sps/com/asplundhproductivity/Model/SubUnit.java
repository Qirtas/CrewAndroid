package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/16/2017.
 */

public class SubUnit
{
    String id , title;
    
    public SubUnit(String id, String title)
    {
        this.id = id;
        this.title = title;
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
