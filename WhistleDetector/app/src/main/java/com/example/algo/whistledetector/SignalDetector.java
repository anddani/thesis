package com.example.algo.whistledetector;

public class SignalDetector {

    static {
        System.loadLibrary("native-lib");
    }

    static Complex[] getFrequencyDomain(double[] samples, FFTColumbiaIterative fftci) {
        // Compute FFT
        double[] z = new double[samples.length*2];
        for (int i = 0; i < samples.length; i++) {
            z[i] = samples[i];
            z[i+samples.length] = 0.0;
        }

        double[] nativeResult = fft(z, fftci.sin, fftci.cos);

        Complex[] x = new Complex[z.length/2];
        for (int i = 0; i < z.length/2; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+samples.length]);
        }

        return x;
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
