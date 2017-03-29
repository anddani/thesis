package com.example.algo.whistledetector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

public class RecorderThread extends Thread {

    private int bufferSize;
    private short[] buffer;
    private AudioRecord recorder;

    private Handler mHandler;

    private boolean recording = true;


    public RecorderThread(Handler mHandler) {
        this.mHandler = mHandler;

        int minBufferSize = AudioRecord.getMinBufferSize(Constants.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        // Ensure that we take 2^N numbers of samples from mic
        bufferSize = SignalDetector.nextPowerOfTwo(minBufferSize);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize );

        buffer = new short[bufferSize];

        recorder.startRecording();
    }

    @Override
    public void run() {
        double[] doubleBuffer = new double[bufferSize*2];

        // Pre-compute sin and cos tables before entering loop
        TrigTables trigTables = new TrigTables(bufferSize);
        Complex[] freqDomain = new Complex[bufferSize];

        // Keep instances of complex numbers
        for (int i = 0; i < freqDomain.length; i++) {
            freqDomain[i] = new Complex(0.0, 0.0);
        }

        while (recording) {
            recorder.read(buffer, 0, buffer.length);

            // Convert to double
            for (int i = 0; i < bufferSize; i++) {
                doubleBuffer[i] = (double) buffer[i] / 32768.0; // Real
                doubleBuffer[i+bufferSize] = 0.0;               // Imaginary
            }

            // Run FFT
            ConvertDomain.timeToFrequency(freqDomain, doubleBuffer, trigTables);

            // Send result back to fragment
            mHandler.obtainMessage(0, freqDomain).sendToTarget();

        }
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void stopRecording() {
        recording = false;
        recorder.stop();
        recorder.release();
    }
}
