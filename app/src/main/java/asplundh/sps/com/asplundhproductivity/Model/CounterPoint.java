package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/18/2017.
 */

public class CounterPoint
{
    String bidSubunitID , weight , lat , lng;
    
    public CounterPoint(String bidSubunitID, String weight, String lat, String lng)
    {
        this.bidSubunitID = bidSubunitID;
        this.weight = weight;
        this.lat = lat;
        this.lng = lng;
    }
    
    public String getBidSubunitID()
    {
        return bidSubunitID;
    }
    
    public void setBidSubunitID(String bidSubunitID)
    {
        this.bidSubunitID = bidSubunitID;
    }
    
    public String getWeight()
    {
        return weight;
    }
    
    public void setWeight(String weight)
    {
        this.weight = weight;
    }
    
    public String getLat()
    {
        return lat;
    }
    
    public void setLat(String lat)
    {
        this.lat = lat;
    }
    
    public String getLng()
    {
        return lng;
    }
    
    public void setLng(String lng)
    {
        this.lng = lng;
    }
}
