package com.example.algo.benchmarkapp.algorithms;

public class Constants {

    public static final int BENCHMARK_ITER = 30;

    public static final int BENCHMARK_MESSAGE_NEW = 17;
    public static final int BENCHMARK_MESSAGE_DONE = 4711;

    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
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
            "FFT Princeton_Java_Iterative",
            "FFT Princeton_Java_Recursive",
            "FFT Columbia_Java_Iterative",
            "FFT C++_Princeton_converted_Iterative",
            "FFT C++_Princeton_converted_Recursive",
            "FFT C++_Columbia_converted_Iterative",
            "FFT C++_KISS",
            "FFT C++_Columbia_optimized_Iterative",
//            "FFT Java_Princeton_optimized_Iterative",
            "JNI Benchmark_with_no_params",
            "JNI Benchmark_with_vector_as_param",
            "JNI Benchmark_convert_param_to_vector",
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
