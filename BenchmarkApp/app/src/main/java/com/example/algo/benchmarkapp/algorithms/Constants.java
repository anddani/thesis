package com.example.algo.benchmarkapp.algorithms;

public class Constants {

    public static final int BENCHMARK_ITER = 1;

    public static final int BENCHMARK_MESSAGE_NEW = 17;
    public static final int BENCHMARK_MESSAGE_ONE_ALG = 3333;
    public static final int BENCHMARK_MESSAGE_DONE = 4711;

    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
//        FFT_JAVA_JTRANSFORMS,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_COLUMBIA,
        FFT_CPP_KISS,
        FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED,
//        FFT_JAVA_ITERATIVE_COLUMBIA_OPTIMIZED,
        JNI_EMPTY,
        JNI_PARAMS,
        JNI_VECTOR_CONVERSION,
    }

    public static final String[] ALGORITHM_NAMES = {
            "FFT Princeton Java Iterative",
            "FFT Princeton Java Recursive",
            "FFT Columbia Java Iterative",
//            "FFT Java JTransforms",
            "FFT C++ Princeton converted Iterative",
            "FFT C++ Princeton converted Recursive",
            "FFT C++ Columbia converted Iterative",
            "FFT C++ KISS",
            "FFT C++ Columbia optimized Iterative",
//            "FFT Java Princeton optimized Iterative",
            "JNI Benchmark with no params",
            "JNI Benchmark with vector as param",
            "JNI Benchmark convert param to vector",
    };

    /**
     * [16, 32, ..., 2^N]
     */
    public static int[] getBlockSizes(int N) {
        int[] blockSizes = new int[N-3];
        int num = 16;
        for (int i = 0; i < N-3; i++, num <<= 1) {
            blockSizes[i] = num;
        }

        return blockSizes;
    }

    public static final int[] BLOCK_SIZES = getBlockSizes(16);

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
