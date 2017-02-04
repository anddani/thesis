package com.example.algo.benchmarkapp.algorithms;

public class Constants {
    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_COLUMBIA,
        FFT_CPP_KISS,
    }
    public static String[] ALGORITHM_NAMES = {
            "FFT Princeton Java Iterative",
            "FFT Princeton Java Recursive",
            "FFT Princeton converted C++ Iterative",
            "FFT Princeton converted C++ Recursive",
            "FFT Columbia Java Iterative",
            "FFT C++ KISS",
    };

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
