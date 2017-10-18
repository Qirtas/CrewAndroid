package asplundh.sps.com.asplundhproductivity.Activity;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VoiceRecordingActivity extends AppCompatActivity
{
    ImageView buttonStart, buttonStop, buttonPlayLastRecordAudio , btn_puase_last_recording , btn_resume_last_recording;
            Button buttonStopPlayingRecording ;
    public  static  String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    TextView tv_msg;
    int seconds = 0, minutes = 0, hour = 0;
    Timer timer_start_recording;
    MediaPlayer mp;
    Timer timer_play_recording;
    boolean isPauseTimer = false;
     int currentPosition = 0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recording);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mp = MediaPlayer.create(this, R.raw.beep1);
        
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        final TextView timerValue = (TextView) findViewById(R.id.tv_timerValue);
        
        buttonStart = (ImageView) findViewById(R.id.btn_start);
        buttonStop = (ImageView) findViewById(R.id.btn_stop);
        buttonPlayLastRecordAudio = (ImageView) findViewById(R.id.btn_play);
        btn_puase_last_recording = (ImageView) findViewById(R.id.btn_puase_last_recording);
        btn_resume_last_recording = (ImageView) findViewById(R.id.btn_resume_last_recording);
        buttonStopPlayingRecording = (Button)findViewById(R.id.btn_stop_playing_recording);
        
        ImageView btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        random = new Random();
        
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            
                if(checkPermission())
                {
                    mp.start();
                    buttonStart.setVisibility(View.GONE);
                    buttonStop.setVisibility(View.VISIBLE);
                    buttonPlayLastRecordAudio.setVisibility(View.GONE);
                    btn_puase_last_recording.setVisibility(View.GONE);
                    btn_resume_last_recording.setVisibility(View.GONE);
                    
                    tv_msg.setText("Recording...");
                
                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                
                    MediaRecorderReady();
                
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                
                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                
                     timer_start_recording = new Timer();
                    timer_start_recording.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
    
                                    if (seconds == 60) {
                                        timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                        minutes = seconds / 60;
                                        seconds = seconds % 60;
                                        hour = minutes / 60;
                                    }
                                    seconds += 1;
                                    timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    
                                    Log.i(AppConstants.TAG, "AAA");
                                }
                            });
                        }
                    }, 0, 1000);
                
                    
                }
                else {
                    requestPermission();
                }
            
            }
        });
    
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                timer_start_recording.cancel();
                timerValue.setText("00:00:00");
    
                tv_msg.setText("Done");
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                btn_puase_last_recording.setVisibility(View.GONE);
                buttonPlayLastRecordAudio.setVisibility(View.VISIBLE);
                btn_resume_last_recording.setVisibility(View.GONE);
                
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                
            }
        });
    
    
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                mp.start();
                seconds = 0;
                minutes = 0;
                hour = 0;
                
                tv_msg.setText("Playing");
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                buttonPlayLastRecordAudio.setVisibility(View.GONE);
                btn_puase_last_recording.setVisibility(View.VISIBLE);
                btn_resume_last_recording.setVisibility(View.GONE);
                
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);
            
                mediaPlayer = new MediaPlayer();
                
                try
                {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
    
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        finish();
                    }
        
                });
                mediaPlayer.start();
                
                 timer_play_recording = new Timer();
                timer_play_recording.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                    
                                if(!isPauseTimer)
                                {
                                    if (seconds == 60) {
                                        timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                                        minutes = seconds / 60;
                                        seconds = seconds % 60;
                                        hour = minutes / 60;
                                    }
                                    seconds += 1;
                                    timerValue.setText(String.format("%02d", hour) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    
                                }
                                
                                Log.w(AppConstants.TAG, "AAA");
                            }
                        });
                    }
                }, 0, 1000);
                
            }
        });
    
        btn_puase_last_recording.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(AppConstants.TAG , "btn_puase_last_recording");
                mediaPlayer.pause();
                isPauseTimer = true;
                 currentPosition = mediaPlayer.getCurrentPosition();
    
    
                tv_msg.setText("Pause");
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                buttonPlayLastRecordAudio.setVisibility(View.GONE);
                btn_puase_last_recording.setVisibility(View.GONE);
                btn_resume_last_recording.setVisibility(View.VISIBLE);
            }
        });
    
        btn_resume_last_recording.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isPauseTimer = false;
                tv_msg.setText("Playing");
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                buttonPlayLastRecordAudio.setVisibility(View.GONE);
                btn_puase_last_recording.setVisibility(View.VISIBLE);
                btn_resume_last_recording.setVisibility(View.GONE);
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
        });
    
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
            
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }
    
    public void MediaRecorderReady()
    {
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    
    public String CreateRandomAudioFileName(int string)
    {
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            
            i++ ;
        }
        return stringBuilder.toString();
    }
    
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(VoiceRecordingActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                                                PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                                               PackageManager.PERMISSION_GRANTED;
                    
                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(VoiceRecordingActivity.this, "Permission Granted",
                                       Toast.LENGTH_LONG).show();
                    } else {
                        
                    }
                }
                break;
        }
    }
    
    public boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                                                       WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                                                        RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
               result1 == PackageManager.PERMISSION_GRANTED;
    }
}
