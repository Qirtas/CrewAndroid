package asplundh.sps.com.asplundhproductivity.Model;

import java.io.Serializable;

/**
 * Created by Malik Muhamad Qirtas on 10/18/2017.
 */

public class CircuitType implements Serializable
{
    String id , title;
    
    public CircuitType(String id, String title)
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
