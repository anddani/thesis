package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE,
        FFT_JAVA_RECURSIVE,
        FFT_CPP_ITERATIVE,
        FFT_CPP_RECURSIVE,
        FFT_CPP_KISS,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Java Iterative",
            "FFT Java Recursive",
            "FFT C++ Iterative",
            "FFT C++ Recursive",
            "FFT C++ KISS",
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
