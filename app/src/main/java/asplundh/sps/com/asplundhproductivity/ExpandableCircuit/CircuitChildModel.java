package asplundh.sps.com.asplundhproductivity.ExpandableCircuit;

/**
 * Created by Malik Muhamad Qirtas on 10/25/2017.
 */

public class CircuitChildModel
{
    String type , milage , surveysDone , equipmentNote;
    private boolean mIsVegetarian;
    
    public CircuitChildModel(String type, String milage, String surveysDone, String equipmentNote , boolean isVege)
    {
        this.type = type;
        this.milage = milage;
        this.surveysDone = surveysDone;
        this.equipmentNote = equipmentNote;
        this.mIsVegetarian = isVege;
    }
    
    public boolean ismIsVegetarian()
    {
        return mIsVegetarian;
    }
    
    public void setmIsVegetarian(boolean mIsVegetarian)
    {
        this.mIsVegetarian = mIsVegetarian;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getMilage()
    {
        return milage;
    }
    
    public void setMilage(String milage)
    {
        this.milage = milage;
    }
    
    public String getSurveysDone()
    {
        return surveysDone;
    }
    
    public void setSurveysDone(String surveysDone)
    {
        this.surveysDone = surveysDone;
    }
    
    public String getEquipmentNote()
    {
        return equipmentNote;
    }
    
    public void setEquipmentNote(String equipmentNote)
    {
        this.equipmentNote = equipmentNote;
    }
}
