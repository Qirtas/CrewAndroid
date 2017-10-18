package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import asplundh.sps.com.asplundhproductivity.R;

public class StartActivity extends AppCompatActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setupUI(findViewById(R.id.startactivity));
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        RelativeLayout btn_start = (RelativeLayout) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                    Intent i = new Intent(StartActivity.this , HomeActivity.class);
                    startActivity(i);
               
                
            }
        });
    }
    
    public void setupUI(View view)
    {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText))
        {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    
                    hideSoftKeyboard(StartActivity.this);
                    
                    return false;
                }
            });
        }
        
        //If a layout container, iterate over children and seed recursion.
        
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) view).getChildAt(i);
                
                //   Log.v(Constants.TAG , "view instanceof ViewGroup");
                
                setupUI(innerView);
            }
        }
    }
    
    public static void hideSoftKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    
}
