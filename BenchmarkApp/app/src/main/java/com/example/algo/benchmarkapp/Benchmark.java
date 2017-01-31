package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.InplaceFFT;
import com.example.algo.benchmarkapp.algorithms.FFT;

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

    public static long FFTJavaIterative(double[] in) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = in.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(in[i], 0);
        }

        InplaceFFT.fft(x);
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTJavaRecursive(double[] in) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = in.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(in[i], 0);
        }

        FFT.fft(x);
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static native long FFTCppIterative(double[] arr);
    public static native long FFTCppRecursive(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }

//    static {
//        System.loadLibrary("native-lib");
//    }
}
