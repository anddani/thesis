package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;
import java.util.Random;

// TODO: Convert Columbia FFT into C++
public class Benchmark {

    private static final boolean DEBUG = false;
    private static final double EPSILON = 0.000000001;
    private Random rand;
    private double[] re;
    private double[] im;

    private Complex[] correctOut;

    public Benchmark(int N) {
        rand = new Random(SystemClock.currentThreadTimeMillis());

        // Generate test data
        re = randomInput(N);
        System.out.println("initial real[]: " + Arrays.toString(re));
        im = new double[N];
    }

    private double[] randomInput(int N) {
        double[] x = new double[N];

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

    private boolean isCorrect(Complex[] c) {
        if (correctOut == null) {
            correctOut = c;
            return true;
        }
        for (int i = 0; i < c.length; i++) {
            if (Math.abs(c[i].im() - correctOut[i].im()) > EPSILON ||
                Math.abs(c[i].re() - correctOut[i].re()) > EPSILON) {
                return false;
            }
        }
        return true;
    }

    public long FFTJavaRecursivePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        Complex[] x = toComplex(re, im);

        Complex[] result = FFTPrincetonRecursive.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA REC PRINCETON ************");
            printComplex(result);
        }

        if (!isCorrect(result)) {
            System.out.println("FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
            printComplex(result);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public long FFTJavaIterativePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Initialize data
        Complex[] x = toComplex(re, im);

        FFTPrincetonIterative.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER PRINCETON ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public long FFTCppIterativePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_princeton_iterative(z);

        Complex[] x = toComplex(nativeResult);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT CPP ITER PRINCETON ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public long FFTCppRecursivePrinceton() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_princeton_recursive(z);

        // Create Java complex numbers
        Complex[] x = toComplex(nativeResult);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT CPP REC PRINCETON ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
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

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER COLUMBIA ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public long FFTCppIterativeColumbia() {
        // Will hold the result from FFT

        // Let first half be filled with real and second half with imaginary
        double[] z = new double[re.length*2];
        for (int i = 0; i < re.length; i++) {
            z[i] = re[i];
            z[i+re.length] = im[i];
        }

        long start = SystemClock.elapsedRealtimeNanos();

        // Merge real and imaginary numbers
//        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_columbia_iterative(z);

//        Complex[] x = toComplex(nativeResult);
        Complex[] x = new Complex[re.length];
        for (int i = 0; i < re.length; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+re.length]);
        }

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER COLUMBIA ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public long FFTCppKiss() {
        long start = SystemClock.elapsedRealtimeNanos();

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        double[] nativeResult = fft_kiss(z);

        Complex[] x = toComplex(nativeResult);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT CPP ITER KISS ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT CPP ITER KISS GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }
    public long FFTJavaJTransforms() {
        long start = SystemClock.elapsedRealtimeNanos();

        double[] z = combineComplex(re, im);
        DoubleFFT_1D fftDo = new DoubleFFT_1D(re.length);
        fftDo.complexForward(z);
        Complex[] x = toComplex(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA JTRANSFORMS ************");
            printComplex(x);
        }

        if (!isCorrect(x)) {
            System.out.println("FFT JAVA JTRANSFORMS GIVES INCORRECT OUTPUT");
            printComplex(x);
            System.out.println("CORRECT: ");
            printComplex(correctOut);
        }
        return stop;
    }

    public native double[] fft_princeton_iterative(double[] arr);
    public native double[] fft_princeton_recursive(double[] arr);
    public native double[] fft_columbia_iterative(double[] arr);
    public native double[] fft_kiss(double[] arr);

    static {
        System.loadLibrary("fft-lib");
    }
}
