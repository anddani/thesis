package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE,
        FFT_JAVA_RECURSIVE,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Java Iterative",
            "FFT Java Recursive",
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
