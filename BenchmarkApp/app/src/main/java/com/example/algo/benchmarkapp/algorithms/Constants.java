package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
        FFT_JAVA_JTRANSFORMS,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_COLUMBIA,
        FFT_CPP_KISS,
        JNI_BENCHMARK,
        JNI_BENCHMARK_SMALL,
//        FFT_JAVA_ITERATIVE_COLUMBIA_OPTIMIZED,
//        FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Princeton Java Iterative",
            "FFT Princeton Java Recursive",
            "FFT Columbia Java Iterative",
            "FFT Java JTransforms",
            "FFT C++ Princeton converted Iterative",
            "FFT C++ Princeton converted Recursive",
            "FFT C++ Columbia converted Iterative",
            "FFT C++ KISS",
            "JNI Benchmark",
            "JNI Benchmark Small",
//            "FFT Java Princeton optimized Iterative",
//            "FFT C++ Princeton optimized Iterative",
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
