package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;

import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.reflect.Array;
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

    // x = [Complex(c[0],c[1]),Complex(c[2],c[3])...]
    private Complex[] toComplex(double[] c) {
        int N = c.length;
        Complex[] x = new Complex[N/2];
        for (int i = 0; i < N; i+=2) {
            x[i/2] = new Complex(c[i], c[i+1]);
        }
        return x;
    }

    // x = [Complex(real[0], imaginary[0]), Complex(real[1], imaginary[1]), ...]
    private Complex[] toComplex(double[] real, double[] imaginary) {
        int N = real.length;
        Complex[] x = new Complex[N];
        for (int i = 0; i < N; i++) {
            x[i] = new Complex(real[i], imaginary[i]);
        }
        return x;
    }

    // [real[0], imaginary[0], real[1], imaginary[1], ...]
    private double[] combineComplex(double[] real, double[] imaginary) {
        int N = real.length*2;
        double[] c = new double[N];
        for (int i = 0; i < N; i+=2) {
            c[i] = real[i/2];
            c[i + 1] = imaginary[i/2];
        }
        return c;
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

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_iterative_native(z);

        Complex[] x = toComplex(nativeResult);

        // DEBUG
        System.out.println("************* FFT CPP ITER PRINCETON ************");
        printComplex(x);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public long FFTCppRecursivePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();
        int N = re.length*2;

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_recursive_native(z);

        // Create Java complex numbers
        Complex[] x = toComplex(nativeResult);

        // DEBUG
        System.out.println("************* FFT CPP REC PRINCETON ************");
        printComplex(x);

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

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_kiss(z);

        Complex[] x = toComplex(nativeResult);

        // DEBUG
        System.out.println("************* FFT CPP ITER KISS ************");
        printComplex(x);

        return SystemClock.elapsedRealtimeNanos() - start;
    }
    public long FFTJavaJTransforms() {
        long start = SystemClock.elapsedRealtimeNanos();

        double[] z = combineComplex(re, im);
        DoubleFFT_1D fftDo = new DoubleFFT_1D(re.length);
        fftDo.complexForward(z);
        Complex[] x = toComplex(z);

        // DEBUG
        System.out.println("************* FFT JAVA JTRANSFORMS ************");
        printComplex(x);

        return SystemClock.elapsedRealtimeNanos() - start;
    }

    public native double[] fft_iterative_native(double[] arr);
    public native double[] fft_recursive_native(double[] arr);
    public native double[] fft_kiss(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }
}
