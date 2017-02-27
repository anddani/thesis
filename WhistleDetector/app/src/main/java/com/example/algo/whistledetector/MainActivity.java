package com.example.algo.whistledetector;

import android.Manifest;
import android.content.pm.PackageManager;
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
    public static final int MIN_MAG = 10;

    RelativeLayout rl;
    TextView tv_freq, tv_mag;

    AudioRecord recorder;
    int minBufferSize;
    int bufferSize;
    short[] buffer;

    int counter = 0;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                rl.setBackgroundColor(Color.GREEN);
                tv_freq.setText("Frequency: " + msg.arg1);
                tv_mag.setText("Magnitude: " + msg.arg2);
            } else if (msg.what == 2) {
                rl.setBackgroundColor(Color.RED);
                tv_freq.setText("Frequency: " + msg.arg1);
                tv_mag.setText("Magnitude: " + msg.arg2);
            } else if (msg.what == 3) {
                System.out.println("Updating tv_freq");
                tv_freq.setText("" + counter);
            }
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

        bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        System.out.println("Buffer size before " + bufferSize);
        bufferSize = SignalDetector.nextPowerOfTwo(bufferSize);
        System.out.println("Buffer size after " + bufferSize);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        buffer = new short[bufferSize];

        recorder.startRecording();

        new SampleFromMic().start();
    }

    class SampleFromMic extends Thread {
        @Override
        public void run() {
            FFTColumbiaIterative fftci = new FFTColumbiaIterative(bufferSize);

            while (true) {
                System.out.println("Min buffer size: " + bufferSize);

                recorder.read(buffer, 0, buffer.length);
                Complex[] freqDomain = ConvertDomain.timeToFrequency(buffer, fftci);

                // Get frequency with max amplitude
                double maxAmplitude = 0.0;
                double bestFrequency = 0.0;
                double frequencyFactor = (1.0 * ReadMic.SAMPLING_RATE) / (1.0 * bufferSize);
                for (int i = 0; i < freqDomain.length/2; i++) {
                    double frequency = i * frequencyFactor;
                    double amplitude = freqDomain[i].abs();
                    if (amplitude > maxAmplitude) {
                        maxAmplitude = amplitude;
                        bestFrequency = frequency;
                    }
                }

                bestFrequency = ((int)maxAmplitude == 0) ? 0.0 : bestFrequency;
                if (bestFrequency > TARGET_FREQUENCY - 100 && bestFrequency < TARGET_FREQUENCY + 100 && maxAmplitude > MIN_MAG) {
                    mHandler.obtainMessage(1, (int)bestFrequency, (int)maxAmplitude).sendToTarget();
                } else {
                    mHandler.obtainMessage(2, (int)bestFrequency, (int)maxAmplitude).sendToTarget();
                }
            }
        }
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
