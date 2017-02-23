package com.example.algo.whistledetector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    RelativeLayout rl;
    TextView tv_freq, tv_mag;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                rl.setBackgroundColor(Color.GREEN);

            } else if (msg.what == 2){
                rl.setBackgroundColor(Color.RED);
            }
            tv_freq.setText("Frequency: " + msg.arg1);
            tv_mag.setText("Magnitude: " + msg.arg2);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_freq = (TextView)findViewById(R.id.frequency);
        tv_freq.setText("Frequency: ");

        tv_mag = (TextView)findViewById(R.id.magnitude);
        tv_mag.setText("Magnitude: ");

        rl = (RelativeLayout)findViewById(R.id.activity_main);
        rl.setBackgroundColor(Color.RED);

        requestRecordAudioPermission();

        new ReadMic(mHandler);
    }

    private void requestRecordAudioPermission() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }
}
