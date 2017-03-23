package com.example.algo.benchmarkapp.algorithms;

public class Constants {

    public static final int BENCHMARK_ITER = 100;

    public static final int BENCHMARK_MESSAGE_NEW = 17;
    public static final int BENCHMARK_MESSAGE_DONE = 4711;

    public enum ALGORITHMS {
        JNI_EMPTY,
        JNI_PARAMS,
        JNI_VECTOR_CONVERSION,
        JNI_COLUMBIA,

        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_COLUMBIA,
        FFT_CPP_KISS,

        NEON_CPP_RECURSIVE,
        NEON_CPP_ITERATIVE,

        ARR_CPP_ITERATIVE_PRINCETON,
        ARR_CPP_RECURSIVE_PRINCETON,
        ARR_CPP_ITERATIVE_COLUMBIA,
        ARR_CPP_KISS,

        ARR_CPP_NEON_RECURSIVE,
        ARR_CPP_NEON_ITERATIVE,

        FLOAT_JAVA_ITERATIVE_PRINCETON,
        FLOAT_JAVA_RECURSIVE_PRINCETON,
        FLOAT_JAVA_ITERATIVE_COLUMBIA,

        FLOAT_CPP_ITERATIVE_PRINCETON,
        FLOAT_CPP_RECURSIVE_PRINCETON,
        FLOAT_CPP_ITERATIVE_COLUMBIA,

        ARR_CPP_FLOAT_ITERATIVE_PRINCETON,
        ARR_CPP_FLOAT_RECURSIVE_PRINCETON,
        ARR_CPP_FLOAT_ITERATIVE_COLUMBIA,
    }

    public static final String[] ALGORITHM_NAMES = {
            "JNI 1_Benchmark_no_params",
            "JNI 2_Benchmark_vector",
            "JNI 3_Benchmark_convert",
            "JNI 4_Benchmark_Columbia",

            "FFT Java_Princeton_Iterative",
            "FFT Java_Princeton_Recursive",
            "FFT Java_Columbia_Iterative",
            "FFT CPP_Princeton_Iterative",
            "FFT CPP_Princeton_Recursive",
            "FFT CPP_Columbia_Iterative",
            "FFT CPP_KISS",

            "NEON CPP_Recursive",
            "NEON CPP_Iterative",

            "ARR CPP_Princeton_Iterative",
            "ARR CPP_Princeton_Recursive",
            "ARR CPP_Columbia_Iterative",
            "ARR CPP_KISS",
            "ARR CPP_NEON_Recursive",
            "ARR CPP_NEON_Iterative",

            "FLOAT Java_Princeton_Iterative",
            "FLOAT Java_Princeton_Recursive",
            "FLOAT Java_Columbia_Iterative",

            "FLOAT CPP_Princeton_Iterative",
            "FLOAT CPP_Princeton_Recursive",
            "FLOAT CPP_Columbia_Iterative",

            "ARR CPP_Float_Princeton_Iterative",
            "ARR CPP_Float_Princeton_Recursive",
            "ARR CPP_Float_Columbia_Iterative",
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

    public static int nextPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    public static final int[] BLOCK_SIZES = getBlockSizes(18);

    public static final int NUM_ALGORITHMS = ALGORITHMS.values().length;
}
