package com.example.algo.whistledetector;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int SAMPLING_RATE = 44100;
    public static final int TARGET_FREQUENCY = 1000;
    public static final int MIN_AMPLITUDE = 5;

    RelativeLayout rl;
    TextView tv_freq, tv_mag;

    AudioRecord recorder;
    int bufferSize;
    short[] buffer;

    // Notify UI thread to change background color
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                rl.setBackgroundColor(Color.GREEN);
            } else if (msg.what == 2) {
                rl.setBackgroundColor(Color.RED);
            }
            tv_freq.setText("Frequency: " + msg.arg1);
            tv_mag.setText("Amplitude: " + msg.arg2);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl = (RelativeLayout)findViewById(R.id.activity_main);

        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        // Ensure that we take 2^N numbers of samples from mic
        bufferSize = SignalDetector.nextPowerOfTwo(minBufferSize);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        buffer = new short[bufferSize];

        recorder.startRecording();

        switch (this.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:

                tv_freq = (TextView)findViewById(R.id.frequency);
                tv_mag = (TextView)findViewById(R.id.magnitude);

                rl.setBackgroundColor(Color.RED);
                requestRecordAudioPermission();

                new SampleFromMic().start();

                System.out.println("Orientation: Portrait");
                break;
            case Configuration.ORIENTATION_LANDSCAPE:

                rl.setBackgroundColor(Color.BLACK);

                System.out.println("Orientation: Landscape");
                break;
        }
    }

    class SampleFromMic extends Thread {
        @Override
        public void run() {
            // Pre-compute sin and cos tables
            FFTColumbiaIterative fftci = new FFTColumbiaIterative(bufferSize);

            while (true) {
                recorder.read(buffer, 0, buffer.length);

                // Run FFT
                Complex[] freqDomain = ConvertDomain.timeToFrequency(buffer, fftci);

                // Get frequency with highest amplitude
                int[] freq = ConvertDomain.maxAmplitude(freqDomain, bufferSize);

                // Change color  of screen when target frequency is present
                if (freq[0] > TARGET_FREQUENCY - 100 && freq[0] < TARGET_FREQUENCY + 100 && freq[1] > MIN_AMPLITUDE) {
                    mHandler.obtainMessage(1, (int)freq[0], (int)freq[1]).sendToTarget();
                } else {
                    mHandler.obtainMessage(2, (int)freq[0], (int)freq[1]).sendToTarget();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop recorder when activity is removed
        recorder.stop();
        recorder.release();
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
