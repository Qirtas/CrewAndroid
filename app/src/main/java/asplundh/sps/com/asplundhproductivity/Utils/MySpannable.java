package asplundh.sps.com.asplundhproductivity.Utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Malik Muhamad Qirtas on 10/26/2017.
 */

public class MySpannable extends ClickableSpan
{
    private boolean isUnderline = true;
    
    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }
    
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#1b76d3"));
    }
    
    @Override
    public void onClick(View widget) {
        
        
    }
}
