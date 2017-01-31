package com.example.algo.benchmarkapp;

import com.example.algo.benchmarkapp.algorithms.Complex;
import com.example.algo.benchmarkapp.algorithms.InplaceFFT;
import com.example.algo.benchmarkapp.algorithms.FFT;

import java.io.File;
import java.io.PrintWriter;

public class Benchmark {

    public static void FFTJavaIterative(int N) {
        Complex[] x = new Complex[N];

        // original data
        for (int i = 0; i < N; i++) {
//            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
//        for (int i = 0; i < N; i++)
//            System.out.println(x[i]);
//        System.out.println();

        // FFT of original data
        InplaceFFT.fft(x);
//        for (int i = 0; i < N; i++)
//            System.out.println(x[i]);
//        System.out.println();

    }
    public static void FFTJavaRecursive(int N) {
        Complex[] x = new Complex[N];

        // original data
        for (int i = 0; i < N; i++) {
//            x[i] = new Complex(i, 0);
            x[i] = new Complex(-2*Math.random() + 1, 0);
        }
//        for (int i = 0; i < N; i++)
//            System.out.println(x[i]);
//        System.out.println();

        // FFT of original data
        InplaceFFT.fft(x);
//        for (int i = 0; i < N; i++)
//            System.out.println(x[i]);
//        System.out.println();

    }

    public static long FFTCpp(int maxN) {
        // Open file to store data
        // 1 -> maxN each n:
        for (int n = 0; n < maxN; n++) {
            //   100 times do:
            int numRuns = 100;
            while (numRuns-- > 0) {
                long start;
                //     Start timer
                //     fft(n)
                //     Stop timer
                //     Write to file
                // Timber - log
            }
        }
        // Close file
//        System.out.println(fft(maxN));
        return fft(maxN);
    }

    public static native long fft(int maxN);

    static {
        System.loadLibrary("fft-lib");
    }

//    static {
//        System.loadLibrary("native-lib");
//    }
}
