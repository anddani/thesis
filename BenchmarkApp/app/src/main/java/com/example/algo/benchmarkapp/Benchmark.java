package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.InplaceFFT;
import com.example.algo.benchmarkapp.algorithms.FFT;

import java.util.Arrays;
import java.util.Random;

public class Benchmark {

    public static double[] randomInput(int N) {
        double[] x = new double[N];
        Random rand = new Random(SystemClock.currentThreadTimeMillis());

        for (int i = 0; i < N; i++) {
            x[i] = -2*rand.nextDouble() + 1;
        }
        return x;
    }

    public static long FFTJavaIterative(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = re.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(re[i], im[i]);
        }

        InplaceFFT.fft(x);
        System.out.println("************* FFT JAVA ITER ************");
        for (Complex c : x) {
            System.out.println(c);
        }
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTJavaRecursive(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = re.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(re[i], im[i]);
        }

        Complex[] result = FFT.fft(x);
        System.out.println("************* FFT JAVA REC ************");
        for (Complex c : result) {
            System.out.println(c);
        }
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTCppIterative(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();
        int N = re.length*2;

        // z = re + im
        double[] z = new double[N];
        System.arraycopy(re, 0, z, 0, re.length);
        System.arraycopy(im, 0, z, re.length, im.length);

        // z[0,2,4,...] re, z[1,3,5,...] im
//        for (int i = 0; i < N; i++) {
//            z[i] = (i % 2 == 0) ? re[i/2] : im[i/2];
//        }

        double[] nativeResult = fft_iterative_native(z);

        Complex[] result = new Complex[re.length];
        int half = N/2;
        for (int i = 0; i < half; i++) {
            result[i] = new Complex(nativeResult[i], nativeResult[i+half]);
        }

        System.out.println("************* FFT CPP ITER ************");
        for (Complex c : result) {
            System.out.println(c);
        }

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTCppRecursive(double[] arr) {

        return 17;
    }

    public static native double[] fft_iterative_native(double[] arr);
    public static native long fft_recursive_native(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }
}
