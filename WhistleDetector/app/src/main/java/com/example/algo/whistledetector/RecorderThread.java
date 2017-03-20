package com.example.algo.whistledetector;

import android.app.usage.ConfigurationStats;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class RecorderThread extends Thread {

    private int bufferSize;
    private short[] buffer;
    private AudioRecord recorder;

    private PlotFragment plotFragment;
    private FrequencyDetectorFragment frequencyDetectorFragment;

    private boolean recording = true;

    private int fragmentType;

    public int getBufferSize() {
        return bufferSize;
    }

    public void stopRecording() {
        if (recording) {
            recording = false;
            recorder.stop();
            recorder.release();
        }
    }

    public RecorderThread(FrequencyDetectorFragment fragment, int fragmentType) {
        this(fragmentType);
        frequencyDetectorFragment = fragment;
    }

    public RecorderThread(PlotFragment fragment, int fragmentType) {
        this(fragmentType);
        plotFragment = fragment;
    }

    public RecorderThread(int fragmentType) {
        this.fragmentType = fragmentType;

        int minBufferSize = AudioRecord.getMinBufferSize(Constants.SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        System.out.println("minBufferSize: " + minBufferSize);

        // Ensure that we take 2^N numbers of samples from mic
        bufferSize = SignalDetector.nextPowerOfTwo(minBufferSize);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        buffer = new short[bufferSize];

        System.out.println("Start recording");
        recorder.startRecording();
    }

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

        while (recording) {
            recorder.read(buffer, 0, buffer.length);

            // Convert to double
            for (int i = 0; i < bufferSize; i++) {
                doubleBuffer[i] = (double) buffer[i] / 32768.0; // Real
                doubleBuffer[i+bufferSize] = 0.0;               // Imaginary
            }

            // Run FFT
            ConvertDomain.timeToFrequency(freqDomain, doubleBuffer, fftci);

            if (fragmentType == Constants.FREQUENCY_DETECTOR_FRAGMENT) {
                int[] freq = ConvertDomain.maxAmplitude(freqDomain);
                frequencyDetectorFragment.updateView(freq);
            } else if (fragmentType == Constants.PLOT_FRAGMENT) {
                plotFragment.updateView(freqDomain);
            }
        }
    }
}
