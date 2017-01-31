package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE,
        FFT_JAVA_RECURSIVE,
        FFT_CPP_ITERATIVE,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Java Iterative",
            "FFT Java Recursive",
            "FFT C++ Iterative"
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
