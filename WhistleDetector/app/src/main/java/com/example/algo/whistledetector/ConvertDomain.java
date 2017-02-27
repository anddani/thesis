package com.example.algo.whistledetector;

public class ConvertDomain {

    public static void hanningWindow(double[] samples) {
        for (int i = 1; i < samples.length; i++) {
            samples[i] *= 0.5 * (1 - Math.cos((2 * Math.PI * i) / (samples.length-1f)));
        }
    }

    public static Complex[] timeToFrequency(short[] buffer, FFTColumbiaIterative fftci) {
        int bufferSize = buffer.length;
        double[] doubleBuffer = new double[bufferSize];

        // Convert to double
        for (int i = 0; i < buffer.length; i++) {
            doubleBuffer[i] = (double) buffer[i] / 32768.0;
        }

        // Apply windowing function
        hanningWindow(doubleBuffer);

        // Run FFT and get sample in frequency domain
        Complex[] freqDomain = SignalDetector.getFrequencyDomain(doubleBuffer, fftci);

        return freqDomain;
    }

    public static int[] maxAmplitude(Complex[] freqDomain, int bufferSize) {
        double maxAmplitude = 0.0;
        double bestFrequency = 0.0;
        double frequencyFactor = (1.0 * MainActivity.SAMPLING_RATE) / (1.0 * bufferSize);
        for (int i = 0; i < freqDomain.length/2; i++) {
            double frequency = i * frequencyFactor;
            double amplitude = freqDomain[i].abs();
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
                bestFrequency = frequency;
            }
        }

        // Display frequency 0 if amplitude is 0
        bestFrequency = ((int)maxAmplitude == 0) ? 0.0 : bestFrequency;

        return new int[] {(int)bestFrequency, (int)maxAmplitude};
    }
}
