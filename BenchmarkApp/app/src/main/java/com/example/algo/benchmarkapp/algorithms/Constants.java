package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
        FFT_JAVA_JTRANSFORMS,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_KISS,
        JNI_BENCHMARK,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Princeton Java Iterative",
            "FFT Princeton Java Recursive",
            "FFT Columbia Java Iterative",
            "FFT Java JTransforms",
            "FFT C++ Princeton converted Iterative",
            "FFT C++ Princeton converted Recursive",
            "FFT C++ KISS",
            "JNI Benchmark",
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
