package asplundh.sps.com.asplundhproductivity.Model;

/**
 * Created by Malik Muhamad Qirtas on 10/18/2017.
 */

public class Note
{
    String text , time , lat , lng;
    
    public Note(String text, String time, String lat, String lng)
    {
        this.text = text;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }
    
    public String getText()
    {
        return text;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public String getTime()
    {
        return time;
    }
    
    public void setTime(String time)
    {
        this.time = time;
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
