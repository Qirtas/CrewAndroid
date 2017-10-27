package asplundh.sps.com.asplundhproductivity.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/16/2017.
 */

public class DBController extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "APA";
    private static final int DATABASE_VERSION = 1;
    public static final String DB_TABLE_LOGINS = "table_logins";
    public static final String DB_TABLE_BIDPLANS = "table_bidplans";
    public static final String DB_TABLE_BIDPLANS_JSON = "table_bidplans_json";
    public static final String DB_TABLE_CIRCUITS_JSON = "table_circuits_json";
    public static final String DB_TABLE_NEW_CIRCUIT = "table_new_circuit";
    public static final String DB_TABLE_CIRCUIT_PATH = "table_circuits_path";
    public static final String DB_TABLE_SURVEY = "table_survey";
    
    
    //Login Table Fields
    public static final String KEY_EMPID = "emp_id";
    public static final String KEY_EMPPIN = "emp_pin";
    public static final String KEY_EMPNAME = "emp_name";
    
    //BidPlan Table Fields
    public static final String KEY_BIDPLANID = "bidplan_id";
    public static final String KEY_BIDPLAN_TITLE = "bidplan_title";
    public static final String KEY_BIDPLAN_UNIT = "bidplan_unit";
    public static final String KEY_BIDPLAN_SUBUNIT = "bidplan_subunit";
    public static final String KEY_BIDPLAN_CUSTOMER_NAME = "bidplan_customername";
    public static final String KEY_BIDPLAN_LOCATION = "bidplan_location";
    public static final String KEY_BIDPLAN_LOCATION_DESC = "bidplan_locationdesc";
    public static final String KEY_BIDPLAN_VERSION = "bidplan_version";
    
    public static final String KEY_BIDPLAN_JSON = "bidplan_JSON";
    
    //Circuits Table Fields
    public static final String KEY_CIRCUIT_ID = "circuit_id";
    public static final String KEY_CIRCUITS_JSON = "circuits_JSON";
    public static final String KEY_PATH_JSON = "path_JSON";
    public static final String KEY_LINE_TYPE = "line_type";
    public static final String KEY_IS_DELEGATED = "is_delegated";
    public static final String KEY_CIRCUIT_MILAGE = "milage";
    public static final String KEY_SUBUNIT = "subunit";
    public static final String KEY_SURVEYS_COMPLETED = "surveys_completed";
    public static final String KEY_EQUIPMENT_NOTE = "equipment_note";
    
    
    //Create new circuit
    public static final String KEY_CIRCUIT_TITLE = "circuit_title";
    public static final String KEY_LINE_TYPE_ID = "LineTypeId";
    public static final String KEY_MILEGAE = "milegae";
    public static final String KEY_ESTIMATED_HOURS = "estimatedHours";
    public static final String KEY_AVERAG_DENSITY = "AverageDensity";
    public static final String KEY_EQUIPMENT_NOTES = "EquipmentNotes";
    public static final String KEY_SOURCE = "Source";
    public static final String KEY_LINE_PATH = "LinePath";
    
    public static final String KEY_SURVEY_POSTPARAMS = "survey_postparams";
    public static final String KEY_SURVEY_NAME = "survey_name";
    
    
    public static final String KEY_NEW_CIRCUIT_POST_PARAMS = "newCircuitPostParams";
    
    
    private static final String CREATE_TABLE_LOGINS = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_LOGINS + "("+
                                                      KEY_EMPID + " TEXT," +
                                                      KEY_EMPPIN + " TEXT," +
                                                        KEY_EMPNAME + " TEXT);";
    
    private static final String CREATE_TABLE_BIDPLANS_JSON = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_BIDPLANS_JSON + "("+
                                                      KEY_EMPID + " TEXT," +
                                                      KEY_BIDPLAN_JSON + " TEXT);";
    
    private static final String CREATE_TABLE_CIRCUITS_JSON = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CIRCUITS_JSON + "("+
                                                             KEY_EMPID + " TEXT," +
                                                             KEY_BIDPLANID + " TEXT," +
                                                             KEY_CIRCUITS_JSON + " TEXT);";
    
    private static final String CREATE_TABLE_CIRCUIT_PATH = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CIRCUIT_PATH + "("+
                                                             KEY_EMPID + " TEXT," +
                                                             KEY_BIDPLANID + " TEXT," +
                                                            KEY_CIRCUIT_ID + " TEXT," +
                                                            KEY_CIRCUIT_TITLE + " TEXT," +
                                                            KEY_CIRCUIT_MILAGE + " TEXT," +
                                                             KEY_PATH_JSON + " TEXT," +
                                                                KEY_LINE_TYPE + " TEXT," +
                                                            KEY_IS_DELEGATED + " TEXT," +
                                                            KEY_SUBUNIT + " TEXT," +
                                                            KEY_SURVEYS_COMPLETED + " TEXT," +
                                                            KEY_EQUIPMENT_NOTE + " TEXT);";
    
    private static final String CREATE_TABLE_NEW_CIRCUIT = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_NEW_CIRCUIT + "("+
                                                             KEY_EMPID + " TEXT," +
                                                             KEY_BIDPLANID + " TEXT," +
                                                           KEY_CIRCUIT_ID + " TEXT," +
                                                           KEY_NEW_CIRCUIT_POST_PARAMS + " TEXT);";
                                                           
    
    private static final String CREATE_TABLE_BIDPLANS = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_BIDPLANS + "("+
                                                      KEY_EMPID + " TEXT," +
                                                        KEY_BIDPLANID + " TEXT," +
                                                        KEY_BIDPLAN_TITLE + " TEXT," +
                                                        KEY_BIDPLAN_UNIT + " TEXT," +
                                                        KEY_BIDPLAN_SUBUNIT + " TEXT," +
                                                        KEY_BIDPLAN_CUSTOMER_NAME + " TEXT," +
                                                        KEY_BIDPLAN_LOCATION + " TEXT," +
                                                        KEY_BIDPLAN_LOCATION_DESC + " TEXT," +
                                                        KEY_BIDPLAN_VERSION + " TEXT);";
    
    private static final String CREATE_TABLE_SURVEYS = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_SURVEY + "("+
                                                        KEY_EMPID + " TEXT," +
                                                        KEY_BIDPLANID + " TEXT," +
                                                       KEY_CIRCUIT_ID + " TEXT," +
                                                       KEY_SURVEY_POSTPARAMS + " TEXT);";
    
    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_LOGINS);
        db.execSQL(CREATE_TABLE_BIDPLANS);
        db.execSQL(CREATE_TABLE_BIDPLANS_JSON);
        db.execSQL(CREATE_TABLE_CIRCUITS_JSON);
        db.execSQL(CREATE_TABLE_NEW_CIRCUIT);
        db.execSQL(CREATE_TABLE_CIRCUIT_PATH);
        db.execSQL(CREATE_TABLE_SURVEYS);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
            
    }
    
    public void addLoginEntry( String emp_id, String emp_pin , String emp_name)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_EMPPIN,    emp_pin);
        cv.put(KEY_EMPNAME,    emp_name);
        
        long res = database.insert( DB_TABLE_LOGINS, null, cv );
        Log.v(AppConstants.TAG , "RES FOR LOGIN ENTRY: " + res);
    }
    
    public void updateLoginEntry( String emp_id, String emp_pin,  String emp_name )
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_EMPPIN,    emp_pin);
        cv.put(KEY_EMPNAME,    emp_name);
        
        long res = database.update( DB_TABLE_LOGINS, cv, KEY_EMPID +"=" + emp_id, null );
        
        Log.v(AppConstants.TAG , "RES FOR LOGIN ENTRY: " + res);
    }
    
    public boolean isLoginRecordExists(String emp_id)
    {
        boolean isExists = false;
        SQLiteDatabase database = this.getWritableDatabase();
        
        Cursor cursor = null;
        String sql ="SELECT "+ KEY_EMPNAME +" FROM "+DB_TABLE_LOGINS+" WHERE " + KEY_EMPID +"="+emp_id;
        cursor= database.rawQuery(sql,null);
        Log.v(AppConstants.TAG ,"Cursor Count : " + cursor.getCount());
        
        if(cursor.getCount()>0){
            //PID Found
            isExists = true;
            
        }
        return isExists;
    }
    
    public boolean isBidAlreadySynced(String bidID)
    {
        boolean isExists = false;
        SQLiteDatabase database = this.getWritableDatabase();
    
        Cursor cursor = null;
        String sql ="SELECT "+ KEY_BIDPLAN_TITLE +" FROM "+ DB_TABLE_BIDPLANS + " WHERE " + KEY_BIDPLANID +"="+bidID;
        cursor= database.rawQuery(sql,null);
        Log.v(AppConstants.TAG ,"Cursor Count for bid existance sttus : " + cursor.getCount());
    
        if(cursor.getCount()>0){
            //PID Found
            isExists = true;
        
        }
        return isExists;
    }
    
    public void addBidPlanJSON(String emp_id, String bidPlanJson)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLAN_JSON,   bidPlanJson);
    
        long res = database.insert( DB_TABLE_BIDPLANS_JSON, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addBidPlanEntry ENTRY: " + res);
    }
    
    public void addCircuitPath(String emp_id, String bidplan_id, String circuit_id , String circuit_title , String circuit_milage, String pathJson , String circuit_type , String isDelegated , String subUnit , String surveys_completed , String eqipment_note)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLANID,    bidplan_id);
        cv.put(KEY_CIRCUIT_ID,    circuit_id);
        cv.put(KEY_CIRCUIT_TITLE,    circuit_title);
        cv.put(KEY_CIRCUIT_MILAGE,    circuit_milage);
        cv.put(KEY_PATH_JSON,   pathJson);
        cv.put(KEY_LINE_TYPE,   circuit_type);
        cv.put(KEY_IS_DELEGATED,   isDelegated);
        cv.put(KEY_SUBUNIT,   subUnit);
        cv.put(KEY_SURVEYS_COMPLETED,   surveys_completed);
        cv.put(KEY_EQUIPMENT_NOTE,   eqipment_note);
        
        long res = database.insert( DB_TABLE_CIRCUIT_PATH, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addCircuitPath ENTRY: " + res);
    }
    
    public Cursor getAllCircuit(String emp_id , String bidPlanID)
    {
        String selectQuery = "SELECT * FROM " + DB_TABLE_CIRCUIT_PATH +" WHERE " + KEY_EMPID +"="+emp_id + " AND " + KEY_BIDPLANID + "=" + bidPlanID;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }
    
    public void addCircuitsJSON(String emp_id, String bidplan_id, String circuitJson)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLANID,    bidplan_id);
        cv.put(KEY_CIRCUITS_JSON,   circuitJson);
        
        long res = database.insert( DB_TABLE_CIRCUITS_JSON, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addCircuitsJSON ENTRY: " + res);
    }
    
    public void addBidPlanEntry(String emp_id, String bidPlanID , String title , String unit , String subUnit , String customerName , String location , String locationDesc , String versionNumber)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLANID,    bidPlanID);
        cv.put(KEY_BIDPLAN_TITLE,    title);
        cv.put(KEY_BIDPLAN_UNIT,    unit);
        cv.put(KEY_BIDPLAN_SUBUNIT,    subUnit);
        cv.put(KEY_BIDPLAN_CUSTOMER_NAME,    customerName);
        cv.put(KEY_BIDPLAN_LOCATION,    location);
        cv.put(KEY_BIDPLAN_LOCATION_DESC,    locationDesc);
        cv.put(KEY_BIDPLAN_VERSION,    versionNumber);
        
        long res = database.insert( DB_TABLE_BIDPLANS, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addBidPlanEntry ENTRY: " + res);
    }
    
    public void addNewCircuitEntry(String emp_id, String bidPlanID , String circuitID, String postParams)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLANID,    bidPlanID);
        cv.put(KEY_CIRCUIT_ID,    circuitID);
        cv.put(KEY_NEW_CIRCUIT_POST_PARAMS,    postParams);
        
        long res = database.insert( DB_TABLE_NEW_CIRCUIT, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addNewCircuitEntry ENTRY: " + res);
    }
    
    public void addSurveyEntry(String emp_id, String bidPlanID , String circuitID , String postParams)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_EMPID,    emp_id);
        cv.put(KEY_BIDPLANID,    bidPlanID);
        cv.put(KEY_CIRCUIT_ID,    circuitID);
        cv.put(KEY_SURVEY_POSTPARAMS,    postParams);
        
        long res = database.insert( DB_TABLE_SURVEY, null, cv );
        Log.v(AppConstants.TAG , "RES FOR addSurveyEntry ENTRY: " + res);
    }
    
    public Cursor getAllData(String tableName)
    {
        String selectQuery = "SELECT * FROM " + tableName;
        
        SQLiteDatabase database = this.getWritableDatabase();
        
        Cursor cursor = database.rawQuery(selectQuery, null);
        
        return cursor;
    }
    
    public Cursor getBidPlanJson(String emp_id)
    {
        String selectQuery = "SELECT * FROM " + DB_TABLE_BIDPLANS_JSON +" WHERE " + KEY_EMPID +"="+emp_id;
    
        SQLiteDatabase database = this.getWritableDatabase();
    
        Cursor cursor = database.rawQuery(selectQuery, null);
    
        return cursor;
    }
    
    public Cursor getCircuitJson(String emp_id , String bidplanID)
    {
        String selectQuery = "SELECT * FROM " + DB_TABLE_CIRCUITS_JSON +" WHERE " + KEY_EMPID +"="+emp_id + " AND " + KEY_BIDPLANID + "=" + bidplanID;
        
        SQLiteDatabase database = this.getWritableDatabase();
        
        Cursor cursor = database.rawQuery(selectQuery, null);
        
        return cursor;
    }
    
    public Cursor getCircuitPath(String emp_id , String bidplanID , String circuitID)
    {
        String selectQuery = "SELECT * FROM " + DB_TABLE_CIRCUIT_PATH +" WHERE " + KEY_EMPID +"="+emp_id + " AND " + KEY_BIDPLANID + "=" + bidplanID+ " AND " + KEY_CIRCUIT_ID + "=" + circuitID;
        
        SQLiteDatabase database = this.getWritableDatabase();
        
        Cursor cursor = database.rawQuery(selectQuery, null);
        
        return cursor;
    }
    
    public void clearTable(String tableName)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(tableName, null,null);
    }
    
    public void updateCircuitIDInSurveyTable(String oldCircuitID, String newCircuitID)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_CIRCUIT_ID,    newCircuitID);
        
        long res = database.update(DB_TABLE_SURVEY, cv, KEY_CIRCUIT_ID +"=" + oldCircuitID, null );
        
        Log.v(AppConstants.TAG , "RES FOR updateCircuitIDInSurveyTable ENTRY: " + res);
    }
    
    public boolean isSurveyForNewCircuitRecordExists(String circuitID)
    {
        boolean isExists = false;
        SQLiteDatabase database = this.getWritableDatabase();
        
        Cursor cursor = null;
        String sql ="SELECT *"+ " FROM "+DB_TABLE_SURVEY+" WHERE " + KEY_CIRCUIT_ID +"="+circuitID;
        cursor= database.rawQuery(sql,null);
        Log.v(AppConstants.TAG ,"Cursor Count for isSurveyForNewCircuitRecordExists : " + cursor.getCount());
        
        if(cursor.getCount()>0){
            //PID Found
            isExists = true;
            
        }
        return isExists;
    }
    
    public void deleteCircuitRecord(String circuitID) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        cv.put(KEY_CIRCUIT_ID,    circuitID);
        
        database.execSQL("DELETE FROM " + DB_TABLE_NEW_CIRCUIT + " WHERE " + KEY_CIRCUIT_ID + "= '" + circuitID + "'");
        
        //Close the database
        database.close();
    }
}
