package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;

import java.util.Random;

// TODO: Make non-static methods and save input as class variable.
public class Benchmark {

//    private Random rand;
//
//    public Benchmark() {
//        rand = new Random(SystemClock.currentThreadTimeMillis());
//    }

    public static double[] randomInput(int N) {
        double[] x = new double[N];
        Random rand = new Random(SystemClock.currentThreadTimeMillis());

        for (int i = 0; i < N; i++) {
            x[i] = -2*rand.nextDouble() + 1;
        }
        return x;
    }

    public static long FFTJavaRecursivePrinceton(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = re.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(re[i], im[i]);
        }

        Complex[] result = FFTPrincetonRecursive.fft(x);

        // DEBUG
        System.out.println("************* FFT JAVA REC PRINCETON ************");
        for (Complex c : result) {
            System.out.println(c);
        }
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTJavaIterativePrinceton(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = re.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(re[i], im[i]);
        }

        FFTPrincetonIterative.fft(x);

        // DEBUG
        System.out.println("************* FFT JAVA ITER PRINCETON ************");
        for (Complex c : x) {
            System.out.println(c);
        }
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTCppIterativePrinceton(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();
        int N = re.length*2;

        // z = re + im
        double[] z = new double[N];
        System.arraycopy(re, 0, z, 0, re.length);
        System.arraycopy(im, 0, z, re.length, im.length);

        double[] nativeResult = fft_iterative_native(z);

        Complex[] result = new Complex[re.length];
        int half = N/2;
        for (int i = 0; i < half; i++) {
            result[i] = new Complex(nativeResult[i], nativeResult[i+half]);
        }

        // DEBUG
        System.out.println("************* FFT CPP ITER PRINCETON ************");
        for (Complex c : result) {
            System.out.println(c);
        }

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTCppRecursivePrinceton(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();
        int N = re.length*2;

        // Merge real and imaginary numbers
        // z = re + im
        double[] z = new double[N];
        System.arraycopy(re, 0, z, 0, re.length);
        System.arraycopy(im, 0, z, re.length, im.length);

        double[] nativeResult = fft_recursive_native(z);

        // Create Java complex numbers
        Complex[] result = new Complex[re.length];
        int half = N/2;
        for (int i = 0; i < half; i++) {
            result[i] = new Complex(nativeResult[i], nativeResult[i+half]);
        }

        // DEBUG
        System.out.println("************* FFT CPP REC PRINCETON ************");
        for (Complex c : result) {
            System.out.println(c);
        }

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTJavaIterativeColumbia(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        int N = re.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(re[i], im[i]);
        }

        FFTColumbiaIterative.fft(x);

        // DEBUG
        System.out.println("************* FFT JAVA ITER PRINCETON ************");
        for (Complex c : x) {
            System.out.println(c);
        }
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static long FFTCppKiss(double[] re, double[] im) {
        long start = SystemClock.elapsedRealtimeNanos();

//        double[] nativeResult = fft_kiss(x);

        System.out.println("************* FFT CPP ITER KISS ************");
        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public static native double[] fft_iterative_native(double[] arr);
    public static native double[] fft_recursive_native(double[] arr);
    public static native double[] fft_kiss(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }
}
