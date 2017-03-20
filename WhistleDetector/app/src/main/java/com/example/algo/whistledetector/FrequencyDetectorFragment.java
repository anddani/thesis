package com.example.algo.whistledetector;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.algo.whistledetector.Constants.MIN_AMPLITUDE;
import static com.example.algo.whistledetector.Constants.TARGET_FREQUENCY;

public class FrequencyDetectorFragment extends Fragment {

    TextView tv_freq, tv_mag;
    RelativeLayout rl;
    int bufferSize;
    short[] buffer;
    AudioRecord recorder;
    SampleFromMic sampleFromMic;

    public void updateView(Complex[] freqDomain) {

        int[] freq = ConvertDomain.maxAmplitude(freqDomain);

//        if (tv_freq == null || tv_mag == null) {
//            break;
//        }

        // Change color  of screen when target frequency is present
        if (freq[0] > TARGET_FREQUENCY - 100 && freq[0] < TARGET_FREQUENCY + 100 && freq[1] > MIN_AMPLITUDE) {
            mHandler.obtainMessage(1, (int)freq[0], (int)freq[1]).sendToTarget();
        } else {
            mHandler.obtainMessage(2, (int)freq[0], (int)freq[1]).sendToTarget();
        }
    }


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
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("onAttach FrequencyDetectorFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            int minBufferSize = AudioRecord.getMinBufferSize(Constants.SAMPLING_RATE,
                                                             AudioFormat.CHANNEL_IN_MONO,
                                                             AudioFormat.ENCODING_PCM_16BIT);
            System.out.println("minBufferSize: " + minBufferSize);

            // Ensure that we take 2^N numbers of samples from mic
            bufferSize = SignalDetector.nextPowerOfTwo(minBufferSize);

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                       Constants.SAMPLING_RATE,
                                       AudioFormat.CHANNEL_IN_MONO,
                                       AudioFormat.ENCODING_PCM_16BIT,
                                       bufferSize);
            buffer = new short[bufferSize];

            recorder.startRecording();

            sampleFromMic = new SampleFromMic();
            sampleFromMic.start();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        rl = (RelativeLayout)rootView.findViewById(R.id.activity_main);

        tv_freq = (TextView)rootView.findViewById(R.id.frequency);
        tv_mag = (TextView)rootView.findViewById(R.id.magnitude);

        rl.setBackgroundColor(Color.RED);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
    }

    class SampleFromMic extends Thread {
        @Override
        public void run() {
            double[] doubleBuffer = new double[bufferSize*2];

            // Pre-compute sin and cos tables
            FFTColumbiaIterative fftci = new FFTColumbiaIterative(bufferSize);
            Complex[] freqDomain = new Complex[bufferSize];

            // Keep instances of complex numbers
            for (int i = 0; i < freqDomain.length; i++) {
                freqDomain[i] = new Complex(0.0, 0.0);
            }

            while (true) {
                recorder.read(buffer, 0, buffer.length);

                // Convert to double
                for (int i = 0; i < bufferSize; i++) {
                    doubleBuffer[i] = (double) buffer[i] / 32768.0; // Real
                    doubleBuffer[i+bufferSize] = 0.0;               // Imaginary
                }

                // Run FFT
                ConvertDomain.timeToFrequency(freqDomain, doubleBuffer, fftci);

                // Get frequency with highest amplitude
                int[] freq = ConvertDomain.maxAmplitude(freqDomain);

                if (tv_freq == null || tv_mag == null) {
                    break;
                }

                // Change color  of screen when target frequency is present
                if (freq[0] > TARGET_FREQUENCY - 100 && freq[0] < TARGET_FREQUENCY + 100 && freq[1] > MIN_AMPLITUDE) {
                    mHandler.obtainMessage(1, (int)freq[0], (int)freq[1]).sendToTarget();
                } else {
                    mHandler.obtainMessage(2, (int)freq[0], (int)freq[1]).sendToTarget();
                }
            }
        }
    }

}
