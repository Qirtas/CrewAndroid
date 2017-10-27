package asplundh.sps.com.asplundhproductivity.ExpandableCircuit;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;
import asplundh.sps.com.asplundhproductivity.Utils.MySpannable;
import at.blogc.android.views.ExpandableTextView;

/**
 * Created by Malik Muhamad Qirtas on 10/25/2017.
 */

public class CircuitInnerViewHolder extends ChildViewHolder
{
    private TextView tv_circuit_type , tv_milage ,tv_surveys_completed  ,tv_equipment_note;
    private ExpandableTextView expandableTextView;
    Button buttonToggle;
    
    public CircuitInnerViewHolder(@NonNull View itemView)
    {
        super(itemView);
        tv_circuit_type = (TextView) itemView.findViewById(R.id.tv_circuit_type);
        tv_milage = (TextView) itemView.findViewById(R.id.tv_milage);
        tv_surveys_completed = (TextView) itemView.findViewById(R.id.tv_surveys_completed);
        tv_equipment_note = (TextView) itemView.findViewById(R.id.tv_equipment_note);
    
         expandableTextView = (ExpandableTextView) itemView.findViewById(R.id.expandableTextView);
         buttonToggle = (Button) itemView.findViewById(R.id.button_toggle);
    }
    
    public void bind(@NonNull CircuitChildModel ingredient)
    {
        tv_circuit_type.setText(ingredient.getType());
        tv_milage.setText(ingredient.getMilage());
        tv_surveys_completed.setText(ingredient.getSurveysDone());
        tv_equipment_note.setText(ingredient.getEquipmentNote());
     //   makeTextViewResizable(tv_equipment_note, 3, "See More", true);
        
        String equimentNote = ingredient.getEquipmentNote();
        Log.v(AppConstants.TAG , "equimentNote: " + equimentNote);
        
        if(equimentNote.equalsIgnoreCase(""))
        {
            Log.w(AppConstants.TAG , "equimentNote.equalsIgnoreCase");
            
            expandableTextView.setText("Not Found");
            buttonToggle.setVisibility(View.GONE);
        }
        else if(equimentNote.length() < 80)
        {
            Log.w(AppConstants.TAG , "equimentNote.length() < 80");
            expandableTextView.setText(ingredient.getEquipmentNote());
            buttonToggle.setVisibility(View.GONE);
        }
        else
        {
            Log.w(AppConstants.TAG , "ELSEEEEEE0");
    
            buttonToggle.setVisibility(View.VISIBLE);
            expandableTextView.setText(ingredient.getEquipmentNote());
            expandableTextView.setAnimationDuration(750L);
    
            // set interpolators for both expanding and collapsing animations
            expandableTextView.setInterpolator(new OvershootInterpolator());
    
            expandableTextView.setExpandInterpolator(new OvershootInterpolator());
            expandableTextView.setCollapseInterpolator(new OvershootInterpolator());
    
            buttonToggle.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (expandableTextView.isExpanded())
                    {
                        expandableTextView.collapse();
                        buttonToggle.setText("Show More");
                    }
                    else
                    {
                        expandableTextView.expand();
                        buttonToggle.setText("Show Less");
                    }
                }
            });
        }
        
        /*expandableTextView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
        {
            @Override
            public void onExpand(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView expanded");
            }
            
            @Override
            public void onCollapse(final ExpandableTextView view)
            {
                Log.d(TAG, "ExpandableTextView collapsed");
            }
        });*/
        
    }
    
    public  void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        
        try
        {
            if (tv.getTag() == null) {
                tv.setTag(tv.getText());
            }
            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
            
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        int lineEndIndex = tv.getLayout().getLineEnd(0);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                                                  viewMore), TextView.BufferType.SPANNABLE);
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                                                  viewMore), TextView.BufferType.SPANNABLE);
                    } else {
                        int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                                                  viewMore), TextView.BufferType.SPANNABLE);
                    }
                }
            });
    
        }
        catch (NullPointerException e){}
       
    }
    
    private  SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        
        if (str.contains(spanableText)) {
            
            
            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);
            
        }
        return ssb;
        
    }
}
