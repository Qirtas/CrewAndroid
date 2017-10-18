package asplundh.sps.com.asplundhproductivity.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class ConfirmationActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, Runnable
{
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
        
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        LinearLayout btn_backward = (LinearLayout) findViewById(R.id.btn_backward);
        btn_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        RelativeLayout btn_back = (RelativeLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        String LAT = getIntent().getStringExtra("LAT");
        String LNG = getIntent().getStringExtra("LNG");
        String ADDRESS = getIntent().getStringExtra("ADDRESS");
        String RADIUS = getIntent().getStringExtra("RADIUS");
        String SelectedRadio = getIntent().getStringExtra("SELECTEDRADIO");
        
        TextView tv_counter1 = (TextView) findViewById(R.id.tv_counter1);
        TextView tv_counter2 = (TextView) findViewById(R.id.tv_counter2);
        TextView tv_counter3 = (TextView) findViewById(R.id.tv_counter3);
        TextView tv_lat = (TextView) findViewById(R.id.tv_lat);
        TextView tv_lng = (TextView) findViewById(R.id.tv_lng);
        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        TextView tv_radius = (TextView) findViewById(R.id.tv_radius);
        TextView tv_text_note = (TextView) findViewById(R.id.tv_text_note);
        
        TextView tv_one_heading = (TextView) findViewById(R.id.tv_one_heading);
        TextView tv_two_heading = (TextView) findViewById(R.id.tv_two_heading);
        TextView tv_three_heading = (TextView) findViewById(R.id.tv_three_heading);
        TextView tv_four_heading = (TextView) findViewById(R.id.tv_four_heading);
        TextView tv_five_heading = (TextView) findViewById(R.id.tv_five_heading);
        TextView tv_six_heading = (TextView) findViewById(R.id.tv_six_heading);
        TextView tv_seven_heading = (TextView) findViewById(R.id.tv_seven_heading);
        TextView tv_eight_heading = (TextView) findViewById(R.id.tv_eight_heading);
        TextView tv_nine_heading = (TextView) findViewById(R.id.tv_nine_heading);
        
        final Button btn_play_audio = (Button) findViewById(R.id.btn_play_audio);
        
        tv_one_heading.setText(HomeActivity.counter1 + "");
        tv_counter1.setText(HomeActivity.counter_one + "");
        
        tv_two_heading.setText(HomeActivity.counter2 + "");
        tv_counter2.setText(HomeActivity.counter_two + "");
        
        tv_three_heading.setText(HomeActivity.counter3 + "");
        tv_counter3.setText(HomeActivity.counter_three + "");
        
        tv_four_heading.setText("Latitude:");
        tv_lat.setText(LAT + "");
        
        tv_five_heading.setText("Longitude:");
        tv_lng.setText(LNG + "");
        
        tv_six_heading.setText("Address:");
        tv_address.setText(ADDRESS + "");
        
       // tv_seven_heading.setText("Address:");
        tv_seven_heading.setText(SelectedRadio + "");
        tv_text_note.setText(AddTextActivity.textNote);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        
        if(VoiceRecordingActivity.AudioSavePathInDevice != null)
        {
            
            seekBar.setOnSeekBarChangeListener(this);
            seekBar.setEnabled(false);
    
            mediaPlayer = new MediaPlayer();
            try
            {
                mediaPlayer.setDataSource(VoiceRecordingActivity.AudioSavePathInDevice);
                mediaPlayer.prepare();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
    
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btn_play_audio.setBackground(getResources().getDrawable(R.drawable.play));
                    //  seekBar.setProgress(0);
                    Log.v(AppConstants.TAG , "onCompletion");
                }
        
            });
            
    
            btn_play_audio.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
            
                    if(mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        Log.i(AppConstants.TAG , "isPlaying");
                        mediaPlayer.pause();
                        btn_play_audio.setBackground(getResources().getDrawable(R.drawable.play));
                    }
                    else
                    {
                
                        seekBar.setEnabled(true);
                        btn_play_audio.setBackground(getResources().getDrawable(R.drawable.pause_ic));
                
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        new Thread(ConfirmationActivity.this).start();
                    }
            
                }
            });
        }
        else
        {
            btn_play_audio.setVisibility(View.GONE);
            tv_nine_heading.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
        }
        
    }
    
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        try {
            if (mediaPlayer.isPlaying() || mediaPlayer != null) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);
            } else if (mediaPlayer == null) {
                Toast.makeText(getApplicationContext(), "Media is not running",
                               Toast.LENGTH_SHORT).show();
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            Log.e("seek bar", "" + e);
            seekBar.setEnabled(false);
        
        }
    }
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        
    }
    
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        
    }
    
    @Override
    public void run()
    {
        Log.w(AppConstants.TAG , "run");
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();
    
        while (mediaPlayer != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }
}
