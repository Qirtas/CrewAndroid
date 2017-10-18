package asplundh.sps.com.asplundhproductivity.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import asplundh.sps.com.asplundhproductivity.R;

public class AddTextActivity extends AppCompatActivity
{
    
    public static String textNote = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        setupUI(findViewById(R.id.main));
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        LinearLayout main = (LinearLayout) findViewById(R.id.main);
            
        final EditText et_note = (EditText) findViewById(R.id.et_note);
        et_note.requestFocus();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    
        Button btn_Save = (Button) findViewById(R.id.btn_Save);
        btn_Save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textNote = et_note.getText().toString();
                Toast.makeText(AddTextActivity.this, "Note saved successfully!",
                               Toast.LENGTH_LONG).show();
                finish();
            }
        });
    
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    
    public void setupUI(View view) {
        
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    
                    hideSoftKeyboard(AddTextActivity.this);
                    
                    return false;
                }
            });
        }
        
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                
                View innerView = ((ViewGroup) view).getChildAt(i);
                
                //   Log.v(Constants.TAG , "view instanceof ViewGroup");
                
                setupUI(innerView);
            }
        }
    }
    
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
