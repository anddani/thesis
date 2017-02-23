package com.example.algo.whistledetector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Process;
import android.util.Log;


public class ReadMic extends Thread {
    public static final int SAMPLING_RATE = 44100;
    public static final int TARGET_FREQUENCY = 1000;
    public static final int FREQ_MAX = 1500; // Filter out frequencies lower than this
    public static final int MIN_MAG = 2000;
    private static final String AUDIO_TAG = "Audio";
    private Handler handler;

    public ReadMic(Handler h) {
        handler = h;
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
        start();
    }

    @Override
    public void run() {
        AudioRecord recorder = null;

        try {
            int minBufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int bufferSize = Math.max(FREQ_MAX*2, minBufferSize);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            short[] buffer = new short[bufferSize];
            FFTColumbiaIterative fftci = new FFTColumbiaIterative(SignalDetector.nextPowerOfTwo(bufferSize));

            double[] doubleBuffer = new double[bufferSize];
            int bytesPerSample = 2;
            double amplification = 1.0;

            recorder.startRecording();

            while (true) {
                bufferSize = recorder.read(buffer, 0, buffer.length);

                // Convert to double
                for (int i = 0, floatIndex = 0; i < (bufferSize - bytesPerSample + 1); i += bytesPerSample, floatIndex++) {
                    double sample = 0.0;
                    for (int b = 0; b < bytesPerSample; b++) {
                        int v = buffer[i + b];
                        if (b < bytesPerSample - 1) {
                            v &= 0xFF;
                        }
                        sample += v << (b*8);
                    }
                    double sample32 = amplification * (sample / 32768.0);
                    doubleBuffer[floatIndex] = sample32;
                }

                // Run FFT and get highest amplitude frequency
                int[] freq = SignalDetector.getHighestAmplitude(doubleBuffer, fftci);

                // If loudest frequency is within range and louder than minimum loudness,
                // send a message to update view
                if (freq[0] > TARGET_FREQUENCY - 100 && freq[0] < TARGET_FREQUENCY + 100 && freq[1] > MIN_MAG) {
                    handler.obtainMessage(1, freq[0], freq[1]).sendToTarget();
                } else {
                    handler.obtainMessage(2, freq[0], freq[1]).sendToTarget();
                }
            }
        } catch (Throwable x) {
            Log.w(AUDIO_TAG, "Error while recording sound", x);
        } finally {
            recorder.stop();
            recorder.release();
        }
    }
}
