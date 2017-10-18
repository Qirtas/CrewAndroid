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
    
    public static final String KEY_EMPID = "emp_id";
    public static final String KEY_EMPPIN = "emp_pin";
    public static final String KEY_EMPNAME = "emp_name";
    
    
    private static final String CREATE_TABLE_LOGINS = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_LOGINS + "("+
                                                      KEY_EMPID + " TEXT," +
                                                      KEY_EMPPIN + " TEXT," +
                                                        KEY_EMPNAME + " TEXT);";
                                                     
    
    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_LOGINS);
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
}
