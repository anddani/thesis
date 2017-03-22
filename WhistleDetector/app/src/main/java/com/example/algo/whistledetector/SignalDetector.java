package com.example.algo.whistledetector;

public class SignalDetector {

    static {
        System.loadLibrary("native-lib");
    }

    /**
     * Calls the native FFT and transfers the result to complex container x
     *
     * @param x output from FFT
     * @param samples input to FFT
     * @param trigTables sin and cos tables
     */
    static void getFrequencyDomain(Complex[] x, double[] samples, TrigTables trigTables) {
        int half = samples.length/2;

        fft(samples, trigTables.sin, trigTables.cos);

        for (int i = 0; i < half; i++) {
            x[i].re = samples[i];
            x[i].im = samples[i+half];
        }
    }

    /**
     * Finds the next power of two for a given number.
     *
     * Examples:
     *      nextPowerOfTwo(99)   => 128
     *      nextPowerOfTwo(1024) => 1024
     *
     * @param v input number
     * @return next power of two for v
     */
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

    public static native void fft(double[] arr, double[] sin, double[] cos);
}
