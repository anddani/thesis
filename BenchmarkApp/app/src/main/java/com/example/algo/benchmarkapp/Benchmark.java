package com.example.algo.benchmarkapp;

import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.FloatComplex;
import com.example.algo.benchmarkapp.algorithms.Constants;
import com.example.algo.benchmarkapp.algorithms.FFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FFTPrincetonRecursive;
import com.example.algo.benchmarkapp.algorithms.FloatFFTColumbiaIterative;
import com.example.algo.benchmarkapp.algorithms.FloatFFTPrincetonIterative;
import com.example.algo.benchmarkapp.algorithms.FloatFFTPrincetonRecursive;

import java.util.Random;

import static com.example.algo.benchmarkapp.algorithms.Constants.MEMORY;
import static com.example.algo.benchmarkapp.algorithms.Constants.TEST_TYPE;
import static com.example.algo.benchmarkapp.algorithms.Constants.TIME;

public class Benchmark {

    private static final boolean DEBUG = false;
    private static final double EPSILON = 0.001;
    private Random rand;
    private double[] re;
    private double[] im;
    private float[] fRe;
    private float[] fIm;

    private double[] tempRe;
    private double[] tempIm;
    private float[] fTempRe;
    private float[] fTempIm;

    private float[] fTableCos;
    private float[] fTableSin;
    private double[] dTableCos;
    private double[] dTableSin;

    private Complex[] complexResult;
    private FloatComplex[] fComplexResult;

    private float[] floatMemoryArray;
    private double[] doubleMemoryArray;

    private final String LOG_TAG = "Benchmark";

    private Complex[] correctOut;

    FloatFFTColumbiaIterative floatCI;
    FFTColumbiaIterative doubleCI;

    public Benchmark(int N) {
        Log.d(LOG_TAG, "new Benchmark()");
        rand = new Random(SystemClock.currentThreadTimeMillis());

        // Generate test data
        re = randomInput(N);
        im = new double[N];
        fRe = new float[N];
        fIm = new float[N];

        tempRe = new double[N];
        tempIm = new double[N];
        fTempRe = new float[N];
        fTempIm = new float[N];

        complexResult = new Complex[N];
        fComplexResult = new FloatComplex[N];

        for (int i = 0; i < N; i++) {
            complexResult[i] = new Complex(0.0, 0.0);
            fComplexResult[i] = new FloatComplex(0.0f, 0.0f);
        }

        floatMemoryArray = new float[N*2];
        doubleMemoryArray = new double[N*2];

        floatCI = new FloatFFTColumbiaIterative(re.length);
        fTableCos = floatCI.cos;
        fTableSin = floatCI.sin;

        doubleCI = new FFTColumbiaIterative(re.length);
        dTableCos = doubleCI.cos;
        dTableSin = doubleCI.sin;

        for (int i = 0; i < N; i++) {
            fRe[i] = (float)re[i];
        }

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
        Complex[] x = complexResult;
        for (int i = 0; i < N; i+=2) {
            x[i/2].re = c[i];
            x[i/2].im = c[i+1];
        }
        return x;
    }

    private FloatComplex[] toComplex(float[] c) {
        int N = c.length;
        FloatComplex[] x = fComplexResult;
        for (int i = 0; i < N; i+=2) {
            x[i/2].re = c[i];
            x[i/2].im = c[i+1];
        }
        return x;
    }

    // x = [Complex(real[0], imaginary[0]), Complex(real[1], imaginary[1]), ...]
    private Complex[] toComplex(double[] real, double[] imaginary) {
        int N = real.length;
        Complex[] x = complexResult;
        for (int i = 0; i < N; i++) {
            x[i].re = real[i];
            x[i].im = imaginary[i];
        }
        return x;
    }
    private FloatComplex[] toComplex(float[] real, float[] imaginary) {
        int N = real.length;
        FloatComplex[] x = fComplexResult;
        for (int i = 0; i < N; i++) {
            x[i].re = real[i];
            x[i].im = imaginary[i];
        }
        return x;
    }

    // [real[0], imaginary[0], real[1], imaginary[1], ...]
    private double[] combineComplex(double[] real, double[] imaginary) {
        int N = real.length*2;
        double[] c = doubleMemoryArray;
        for (int i = 0; i < N; i+=2) {
            c[i] = real[i/2];
            c[i + 1] = imaginary[i/2];
        }
        return c;
    }
    private float[] combineComplex(float[] real, float[] imaginary) {
        int N = real.length*2;
        float[] c = floatMemoryArray;
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
    private void printComplex(FloatComplex[] in) {
        for (FloatComplex c : in) {
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
        }
    }

    private void checkCorrectness(FloatComplex[] x, String message) {
        if (!isCorrect(x)) {
            System.out.println(x.length + " " + message);
            for (int i = 0; i < 10; i++) {
                System.out.println(x[i]);
            }
            System.out.println("CORRECT: ");
            for (int i = 0; i < 10; i++) {
                System.out.println(correctOut[i]);
            }
        }
    }

    private boolean isCorrect(Complex[] c) {
        if (correctOut == null) {
            correctOut = new Complex[c.length];
            for (int i = 0; i < c.length; i++) {
                correctOut[i] = new Complex(c[i].re, c[i].im);
            }
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

    private boolean isCorrect(FloatComplex[] c) {
        if (correctOut != null) {
            for (int i = 0; i < c.length; i++) {
                if (Math.abs(c[i].im() - correctOut[i].im()) > EPSILON ||
                    Math.abs(c[i].re() - correctOut[i].re()) > EPSILON) {
                    return false;
                }
            }
        }
        return true;
    }

    public long JNIBenchmarkEmpty() {
        long start, stop;
        start = SystemClock.elapsedRealtimeNanos();

        jni_empty();

        stop = SystemClock.elapsedRealtimeNanos() - start;
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
        double[] z = combineComplex(re, im);
        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = jni_columbia(z, dTableCos, dTableSin);

        long stop = SystemClock.elapsedRealtimeNanos() - start;
        return stop;
    }


    public long FFTJavaRecursivePrinceton() {
        // Initialize data
        Complex[] x = toComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        Complex[] result = FFTPrincetonRecursive.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            if (DEBUG) {
                System.out.println("************* FFT JAVA REC PRINCETON ************");
                printComplex(result);
            }

            checkCorrectness(result, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTJavaIterativePrinceton() {
        // Initialize data
        Complex[] x = toComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        FFTPrincetonIterative.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            if (DEBUG) {
                System.out.println("************* FFT JAVA ITER PRINCETON ************");
                printComplex(x);
            }
            checkCorrectness(x, "FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTCppIterativePrinceton(int arrTest) {
        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        fft_princeton_iterative(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            Complex[] x = toComplex(z);

            if (DEBUG) {
                System.out.println("************* FFT CPP ITER PRINCETON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTCppRecursivePrinceton(int arrTest) {

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_princeton_recursive(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            // Create Java complex numbers
            Complex[] x = toComplex(nativeResult);


            if (DEBUG) {
                System.out.println("************* FFT CPP REC PRINCETON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTJavaIterativeColumbia() {
        for (int i = 0; i < re.length; i++) {
            tempRe[i] = re[i];
            tempIm[i] = im[i];
        }

        // Initialize cos and sin tables
        long start = SystemClock.elapsedRealtimeNanos();

        doubleCI.fft(tempRe, tempIm);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            Complex[] x = toComplex(tempRe, tempIm);


            if (DEBUG) {
                System.out.println("************* FFT JAVA ITER COLUMBIA ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTCppIterativeColumbia(int arrTest) {
        // Let first half be filled with real and second half with imaginary
        double[] z = doubleMemoryArray;
        for (int i = 0; i < re.length; i++) {
            z[i] = re[i];
            z[i+re.length] = im[i];
        }

        long start = SystemClock.elapsedRealtimeNanos();

        fft_columbia_iterative(z, dTableCos, dTableSin, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            Complex[] x = complexResult;
            for (int i = 0; i < re.length; i++) {
                x[i].re = z[i];
                x[i].im = z[i + re.length];
            }


            if (DEBUG) {
                System.out.println("************* FFT JAVA ITER COLUMBIA ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTCppKiss(int arrTest) {

        // Merge real and imaginary numbers
        double[] z = combineComplex(re, im);

        fft_kiss_init(z.length/2);

        long start = SystemClock.elapsedRealtimeNanos();

        double[] nativeResult = fft_kiss(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        fft_kiss_delete();

        if (TEST_TYPE == TIME) {
            Complex[] x = toComplex(nativeResult);

            if (DEBUG) {
                System.out.println("************* FFT CPP ITER KISS ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT CPP ITER KISS GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FFTCppRecursiveNeon(int arrTest) {
        float[] z = floatMemoryArray;
        for (int i = 0; i < re.length; i++) {
            z[i] = (float)re[i];
            z[i+re.length] = 0.0f;
        }

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = fft_recursive_neon(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            Complex[] x = complexResult;
            for (int i = 0; i < re.length; i++) {
//                x[i] = new Complex(nativeResult[i], nativeResult[i + re.length]);
                x[i].re = nativeResult[i];
                x[i].im = nativeResult[i + re.length];
            }

            if (DEBUG) {
                System.out.println("************* FFT CPP RECURSIVE NEON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT CPP RECURSIVE NEON");
        }
        return stop;
    }

    public long FFTCppIterativeNeon(int arrTest) {
        float[] z = floatMemoryArray;
        for (int i = 0; i < re.length; i++) {
            z[i] = (float)re[i];
            z[i+re.length] = 0.0f;
        }

        init_iterative_neon(re.length);

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = run_iterative_neon(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            Complex[] x = complexResult;
            for (int i = 0; i < re.length; i++) {
//                x[i] = new Complex(nativeResult[i], nativeResult[i + re.length]);
                x[i].re = nativeResult[i];
                x[i].im = nativeResult[i + re.length];
            }

            delete_iterative_neon();

            if (DEBUG) {
                System.out.println("************* FFT CPP ITER COLUMBIA OPTIMIZED ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT CPP ITER NEON INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FloatFFTJavaIterativeColumbia() {
        for (int i = 0; i < fRe.length; i++) {
            fTempRe[i] = fRe[i];
            fTempIm[i] = fIm[i];
        }

        long start = SystemClock.elapsedRealtimeNanos();

        floatCI.fft(fTempRe, fTempIm);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            FloatComplex[] x = toComplex(fTempRe, fTempIm);

            if (DEBUG) {
                System.out.println("************* FFT JAVA ITER COLUMBIA ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        }
        return stop;
    }
    public long FloatFFTJavaRecursivePrinceton() {
        // Initialize data
        FloatComplex[] x = toComplex(fRe, fIm);

        long start = SystemClock.elapsedRealtimeNanos();

        FloatComplex[] result = FloatFFTPrincetonRecursive.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            if (DEBUG) {
                System.out.println("************* FFT JAVA REC PRINCETON ************");
                printComplex(result);
            }

            checkCorrectness(result, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FloatFFTJavaIterativePrinceton() {
        // Initialize data
        FloatComplex[] x = toComplex(fRe, fIm);

        long start = SystemClock.elapsedRealtimeNanos();

        FloatFFTPrincetonIterative.fft(x);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            if (DEBUG) {
                System.out.println("************* FFT JAVA REC PRINCETON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FloatFFTCppIterativePrinceton(int arrTest) {
        // Merge real and imaginary numbers
        float[] z = combineComplex(fRe, fIm);

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = float_fft_princeton_iterative(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            FloatComplex[] x = toComplex(nativeResult);

            if (DEBUG) {
                System.out.println("************* FFT CPP ITER PRINCETON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FloatFFTCppRecursivePrinceton(int arrTest) {
        // Merge real and imaginary numbers
        float[] z = combineComplex(fRe, fIm);

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = float_fft_princeton_recursive(z, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            // Create Java complex numbers
            FloatComplex[] x = toComplex(nativeResult);


            if (DEBUG) {
                System.out.println("************* FFT CPP REC PRINCETON ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA REC PRINCETON GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public long FloatFFTCppIterativeColumbia(int arrTest) {
        float[] z = floatMemoryArray;
        for (int i = 0; i < re.length; i++) {
            z[i] = fRe[i];
            z[i + fRe.length] = fIm[i];
        }

        long start = SystemClock.elapsedRealtimeNanos();

        float[] nativeResult = float_fft_columbia_iterative(z, fTableCos, fTableSin, arrTest);

        long stop = SystemClock.elapsedRealtimeNanos() - start;

        if (TEST_TYPE == TIME) {
            FloatComplex[] x = new FloatComplex[fRe.length];
            for (int i = 0; i < fRe.length; i++) {
                x[i] = new FloatComplex(nativeResult[i], nativeResult[i + fRe.length]);
            }

            if (DEBUG) {
                System.out.println("************* FFT JAVA ITER COLUMBIA ************");
                printComplex(x);
            }

            checkCorrectness(x, "FFT JAVA ITER COLUMBIA GIVES INCORRECT OUTPUT");
        }
        return stop;
    }

    public native void     jni_empty();
    public native double[] jni_params(double[] arr);
    public native double[] jni_vector_conversion(double[] arr);
    public native double[] jni_columbia(double[] arr, double[] cos, double[] sin);

    public native double[] fft_princeton_iterative(double[] arr, int arrTest);
    public native double[] fft_princeton_recursive(double[] arr, int arrTest);
    public native double[] fft_columbia_iterative(double[] arr, double[] cos, double[] sin, int arrTest);

    public native void     fft_kiss_init(int N);
    public native double[] fft_kiss(double[] arr, int arrTest);
    public native void     fft_kiss_delete();

    public native float[]  fft_recursive_neon(float[] arr, int arrTest);

    public native void     init_iterative_neon(int half);
    public native float[]  run_iterative_neon(float[] arr, int arrTest);
    public native void     delete_iterative_neon();

    public native float[] float_fft_princeton_iterative(float[] arr, int arrTest);
    public native float[] float_fft_princeton_recursive(float[] arr, int arrTest);
    public native float[] float_fft_columbia_iterative(float[] arr, float[] cos, float[] sin, int arrTest);

    static {
        System.loadLibrary("fft-lib");
    }
}
