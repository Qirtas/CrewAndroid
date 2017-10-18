package asplundh.sps.com.asplundhproductivity.Model;

import java.io.Serializable;

/**
 * Created by Malik Muhamad Qirtas on 10/18/2017.
 */

public class Point implements Serializable
{
    String LAT , LNG;
    
    public Point(String LAT, String LNG)
    {
        this.LAT = LAT;
        this.LNG = LNG;
    }
    
    public String getLAT()
    {
        return LAT;
    }
    
    public void setLAT(String LAT)
    {
        this.LAT = LAT;
    }
    
    public String getLNG()
    {
        return LNG;
    }
    
    public void setLNG(String LNG)
    {
        this.LNG = LNG;
    }
}
