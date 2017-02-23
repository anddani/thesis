package com.example.algo.whistledetector;

public class SignalDetector {

    public static int FREQ_MAX = 1500; // Filter out frequencies lower than this

    static {
        System.loadLibrary("native-lib");
    }

    static int[] getHighestMagnitude(double[] samples, FFTColumbiaIterative fftci) {

        // Compute FFT
        System.out.println("samples length: " + samples.length);
        double[] z = new double[nextPowerOfTwo(samples.length)*2];
        System.out.println("z length: " + z.length);
        for (int i = 0; i < samples.length; i++) {
            z[i] = samples[i];
            z[i+samples.length] = 0.0;
        }

        double[] nativeResult = fft(z, fftci.sin, fftci.cos);

        Complex[] x = new Complex[z.length/2];
        for (int i = 0; i < z.length/2; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+samples.length]);
        }

        // Extract amplitudes
        double[] magnitudes = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            magnitudes[i] = x[i].abs();
        }

        // Get frequency with highest magnitude within allowed frequency range
        double maxVal = -1.0;
        int maxIndex = 1;
        double maxFrequency = 0.0;
        double frequencyFactor = (1.0 * ReadMic.SAMPLING_RATE) / (1.0 * z.length);
        for (int i = 0; i < x.length/2; i++) {
            double freq = i * frequencyFactor;
            if (magnitudes[i] > maxVal && freq < FREQ_MAX) {
                maxVal = magnitudes[i];
                maxIndex = i;
                maxFrequency = freq;
            }
        }

        System.out.println("Max Frequency of samples: " + maxFrequency + " mag: " + magnitudes[maxIndex] + " maxVal: " + maxVal + " maxIndex: " + maxIndex);
        return new int[] {(int)maxFrequency, (int)magnitudes[maxIndex]};
    }

    public static int nextPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    public static native double[] fft(double[] arr, double[] sin, double[] cos);
}
