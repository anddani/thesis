package com.example.algo.benchmarkapp;

import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;
import java.util.Random;

public class Benchmark {

    private static final boolean DEBUG = false;
    private static final double EPSILON = 0.001;
    private Random rand;
    private double[] re;
    private double[] im;

    private Complex[] correctOut;

    public Benchmark(int N) {
        rand = new Random(SystemClock.currentThreadTimeMillis());

        // Generate test data
        re = randomInput(N);
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

    private void checkCorrectness(Complex[] x, String message) {
        if (!isCorrect(x)) {
            System.out.println(x.length + " " + message);
            for (int i = 0; i < 10; i++) {
                System.out.println(x[i]);
            }
            System.out.println("CORRECT: ");
            for (int i = 0; i < 10; i++) {
                System.out.println(correctOut[i]);
            }
//            printComplex(x);
//            System.out.println("CORRECT: ");
//            printComplex(correctOut);
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
        // Initialize data
        Complex[] x = toComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        Complex[] result = FFTPrincetonRecursive.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA REC PRINCETON ************");
            printComplex(result);
        }

        checkCorrectness(result, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTJavaIterativePrinceton() {
        // Initialize data
        Complex[] x = toComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        FFTPrincetonIterative.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER PRINCETON ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTCppIterativePrinceton() {
        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_princeton_iterative(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        Complex[] x = toComplex(nativeResult);

        if (DEBUG) {
            System.out.println("************* FFT CPP ITER PRINCETON ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTCppRecursivePrinceton() {

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_princeton_recursive(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        // Create Java complex numbers
        Complex[] x = toComplex(nativeResult);


        if (DEBUG) {
            System.out.println("************* FFT CPP REC PRINCETON ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTJavaIterativeColumbia() {
        // Will hold the result from FFT
        double[] tempRe = re.clone();
        double[] tempIm = im.clone();

        // Initialize cos and sin tables
        FFTColumbiaIterative fftci = new FFTColumbiaIterative(tempRe.length);

        long start = SystemClock.elapsedRealtimeNanos();

        fftci.fft(tempRe, tempIm);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        Complex[] x = toComplex(tempRe, tempIm);


        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER COLUMBIA ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTCppIterativeColumbia() {

        // Let first half be filled with real and second half with imaginary
        double[] z = new double[re.length*2];
        for (int i = 0; i < re.length; i++) {
            z[i] = re[i];
            z[i+re.length] = im[i];
        }

        FFTColumbiaIterative fftci = new FFTColumbiaIterative(re.length);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_columbia_iterative(z, fftci.cos, fftci.sin);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        Complex[] x = new Complex[re.length];
        for (int i = 0; i < re.length; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+re.length]);
        }


        if (DEBUG) {
            System.out.println("************* FFT JAVA ITER COLUMBIA ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long FFTCppKiss() {

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        fft_kiss_init(z.length/2);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_kiss(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        fft_kiss_delete();

        Complex[] x = toComplex(nativeResult);


        if (DEBUG) {
            System.out.println("************* FFT CPP ITER KISS ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT CPP ITER KISS GIVES INCORRECT OUTPUT");
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

        checkCorrectness(x, "FFT JAVA JTRANSFORMS GIVES INCORRECT OUTPUT");
        return stop;
    }

    public long JNIBenchmarkEmpty() {
        long start = SystemClock.elapsedRealtimeNanos();

        jni_empty();

        long stop = SystemClock.elapsedRealtimeNanos() - start;
        return stop;
    }

    public long JNIBenchmarkParams() {
        double[] z = combineComplex(re, im);
        long start = SystemClock.elapsedRealtimeNanos();

        jni_params(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;
        return stop;
    }

    public long JNIBenchmarkVectorConversion() {
        double[] z = combineComplex(re, im);
        long start = SystemClock.elapsedRealtimeNanos();

        jni_vector_conversion(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;
        return stop;
    }

    public long JNIBenchmarkColumbia() {
        // Let first half be filled with real and second half with imaginary
        double[] z = new double[re.length*2];
        for (int i = 0; i < re.length; i++) {
            z[i] = re[i];
            z[i+re.length] = im[i];
        }
        FFTColumbiaIterative fftci = new FFTColumbiaIterative(re.length);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = jni_columbia(z, fftci.cos, fftci.sin);

        long stop = SystemClock.elapsedRealtimeNanos() - start;
        return stop;
    }

    public long FFTCppRecursiveNeon() {
        float[] z = new float[re.length*2];
        for (int i = 0; i < re.length; i++) {
            z[i] = (float)re[i];
        }

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = fft_recursive_neon(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        Complex[] x = new Complex[re.length];
        for (int i = 0; i < re.length; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+re.length]);
        }


        if (DEBUG) {
            System.out.println("************* FFT CPP RECURSIVE NEON ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT CPP RECURSIVE NEON");
        return stop;
    }

    public long FFTCppIterativeNeon() {
        float[] z = new float[re.length*2];
        for (int i = 0; i < re.length; i++) {
            z[i] = (float)re[i];
        }

        init_iterative_neon(re.length);

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = run_iterative_neon(z);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        Complex[] x = new Complex[re.length];
        for (int i = 0; i < re.length; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+re.length]);
        }

        delete_iterative_neon();

        if (DEBUG) {
            System.out.println("************* FFT CPP ITER COLUMBIA OPTIMIZED ************");
            printComplex(x);
        }

        checkCorrectness(x, "FFT CPP ITER NEON INCORRECT OUTPUT");
        return stop;
    }

    public native double[] fft_princeton_iterative(double[] arr);
    public native double[] fft_princeton_recursive(double[] arr);
    public native double[] fft_columbia_iterative(double[] arr, double[] cos, double[] sin);
//    public native double[] fft_columbia_iterative_optimized(double[] arr);


    public native void fft_kiss_init(int N);
    public native double[] fft_kiss(double[] arr);
    public native void fft_kiss_delete();

    public native void jni_empty();
    public native double[] jni_params(double[] arr);
    public native double[] jni_vector_conversion(double[] arr);
    public native double[] jni_columbia(double[] arr, double[] cos, double[] sin);

    public native float[] fft_recursive_neon(float[] arr);

    public native void init_iterative_neon(int half);
    public native float[] run_iterative_neon(float[] arr);
    public native void delete_iterative_neon();

    static {
        System.loadLibrary("fft-lib");
    }
}
