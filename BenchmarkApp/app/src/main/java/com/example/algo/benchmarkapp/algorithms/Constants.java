package com.example.algo.benchmarkapp.algorithms;

public class Constants {

    public static final int BENCHMARK_ITER = 30;
//    public static final int BENCHMARK_ITER = 5;

    public static final int BENCHMARK_MESSAGE_NEW = 17;
    public static final int BENCHMARK_MESSAGE_DONE = 4711;

    public static final int JNI_TYPE = 0;
    public static final int ALG_TYPE = 1;

    public enum JNI_TESTS {
        JNI_EMPTY,
        JNI_PARAMS,
        JNI_VECTOR_CONVERSION,
        JNI_COLUMBIA,
    }

    public enum ALGORITHMS {
        FFT_JAVA_ITERATIVE_PRINCETON,
        FFT_JAVA_RECURSIVE_PRINCETON,
        FFT_JAVA_ITERATIVE_COLUMBIA,
        FFT_CPP_ITERATIVE_PRINCETON,
        FFT_CPP_RECURSIVE_PRINCETON,
        FFT_CPP_ITERATIVE_COLUMBIA,
        FFT_CPP_KISS,

        NEON_CPP_RECURSIVE,
        NEON_CPP_ITERATIVE,
    }

    public static final String[] JNI_NAMES = {
            "JNI 1_Benchmark_no_params",
            "JNI 2_Benchmark_vector",
            "JNI 3_Benchmark_convert",
            "JNI 4_Benchmark_Columbia",
    };

    public static final String[] ALGORITHM_NAMES = {
            "FFT Java_Princeton_Iterative",
            "FFT Java_Princeton_Recursive",
            "FFT Java_Columbia_Iterative",
            "FFT CPP_Princeton_Iterative",
            "FFT CPP_Princeton_Recursive",
            "FFT CPP_Columbia_Iterative",
            "FFT CPP_KISS",

            "NEON CPP_Recursive",
            "NEON CPP_Iterative",
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
    public static final int NUM_JNI_TESTS = JNI_TESTS.values().length;
}
