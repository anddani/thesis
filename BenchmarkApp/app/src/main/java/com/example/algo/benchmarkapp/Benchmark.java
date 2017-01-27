package com.example.algo.benchmarkapp;

/**
 *
 */

public class Benchmark {

    public static void FFTJava(int maxN) {
    }

    public static void FFTCpp(int maxN) {
        // Open file to store data
        // 1 -> maxN each n:
        //   100 times do:
        //     Start timer
        //     fft(n)
        //     Stop timer
        //     Write to file
        // Close file
        System.out.println(fft());
    }

    public static native long fft();

    static {
        System.loadLibrary("fft-lib");
    }

//    static {
//        System.loadLibrary("native-lib");
//    }
}
