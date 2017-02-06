package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;

import java.util.Arrays;
import java.util.Random;

// TODO: Make non-static methods and save input as class variable.
public class Benchmark {

    private Random rand;
    private double[] re;
    private double[] im;

    public Benchmark(int N) {
        rand = new Random(SystemClock.currentThreadTimeMillis());

        // Generate test data
        re = randomInput(N);
        System.out.println("initial real[]: " + Arrays.toString(re));
        im = new double[N];
    }

    private static double[] randomInput(int N) {
        double[] x = new double[N];
        Random rand = new Random(SystemClock.currentThreadTimeMillis());

        for (int i = 0; i < N; i++) {
            x[i] = -2*rand.nextDouble() + 1;
        }
        return x;
    }

    private Complex[] toComplex(double[] real, double[] imaginary) {
        int N = real.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(real[i], imaginary[i]);
        }
        return x;
    }

    private void printComplex(Complex[] in) {
        for (Complex c : in) {
            System.out.println(c);
        }
    }

    public long FFTJavaRecursivePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        Complex[] x = toComplex(re, im);

        Complex[] result = FFTPrincetonRecursive.fft(x);

        // DEBUG
        System.out.println("************* FFT JAVA REC PRINCETON ************");
        printComplex(result);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public long FFTJavaIterativePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        Complex[] x = toComplex(re, im);

        FFTPrincetonIterative.fft(x);

        // DEBUG
        System.out.println("************* FFT JAVA ITER PRINCETON ************");
        printComplex(x);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public long FFTCppIterativePrinceton() {
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

    public long FFTCppRecursivePrinceton() {
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
        printComplex(result);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public long FFTJavaIterativeColumbia() {
        // Will hold the result from FFT
        double[] tempRe = re.clone();
        double[] tempIm = im.clone();

        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize cos and sin tables
        FFTColumbiaIterative fftci = new FFTColumbiaIterative(tempRe.length);
        fftci.fft(tempRe, tempIm);

        Complex[] x = toComplex(tempRe, tempIm);

        // DEBUG
        System.out.println("************* FFT JAVA ITER COLUMBIA ************");
        printComplex(x);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public long FFTCppKiss() {
        long start = SystemClock.elapsedRealtimeNanos();
        int N = re.length*2;

        // Merge real and imaginary numbers
        // z = re + im
        double[] z = new double[N];
        System.arraycopy(re, 0, z, 0, re.length);
        System.arraycopy(im, 0, z, re.length, im.length);

        double[] nativeResult = fft_kiss(z);

        // Create Java complex numbers
        Complex[] result = new Complex[re.length];
        int half = N/2;
        for (int i = 0; i < half; i++) {
            result[i] = new Complex(nativeResult[i], nativeResult[i+half]);
        }

        // DEBUG
        System.out.println("************* FFT CPP ITER KISS ************");
        printComplex(result);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public native double[] fft_iterative_native(double[] arr);
    public native double[] fft_recursive_native(double[] arr);
    public native double[] fft_kiss(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }
}
