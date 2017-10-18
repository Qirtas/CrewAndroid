package asplundh.sps.com.asplundhproductivity.Expandable;

import java.util.ArrayList;

import asplundh.sps.com.asplundhproductivity.Model.SubUnit;

/**
 * Created by Malik Muhamad Qirtas on 10/13/2017.
 */

public class ChildModel
{
    private String versionNumber , unit , customerName , city , country , location_description;
    private boolean mIsVegetarian;
    ArrayList<SubUnit> subunitsList = new ArrayList<>();
    
    public ChildModel(String versionNumber , String unit , String customerName , String city , String country, ArrayList<SubUnit> subunits ,boolean isVegetarian , String location_description)
    {
        this.versionNumber = versionNumber;
        this.unit = unit;
        this.customerName = customerName;
        this.city = city;
        this.country = country;
        this.subunitsList = subunits;
        mIsVegetarian = isVegetarian;
        this.location_description = location_description;
    }
    
    public String getLocation_description()
    {
        return location_description;
    }
    
    public void setLocation_description(String location_description)
    {
        this.location_description = location_description;
    }
    
    public ArrayList<SubUnit> getSubunitsList()
    {
        return subunitsList;
    }
    
    public void setSubunitsList(ArrayList<SubUnit> subunitsList)
    {
        this.subunitsList = subunitsList;
    }
    
    public boolean ismIsVegetarian()
    {
        return mIsVegetarian;
    }
    
    public void setmIsVegetarian(boolean mIsVegetarian)
    {
        this.mIsVegetarian = mIsVegetarian;
    }
    
    public String getVersionNumber()
    {
        return versionNumber;
    }
    
    public void setVersionNumber(String versionNumber)
    {
        this.versionNumber = versionNumber;
    }
    
    public String getUnit()
    {
        return unit;
    }
    
    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    
    public String getCustomerName()
    {
        return customerName;
    }
    
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    
    public String getCountry()
    {
        return country;
    }
    
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    public boolean isVegetarian() {
        return mIsVegetarian;
    }
}
