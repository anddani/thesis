package com.example.algo.benchmarkapp;

import com.example.algo.benchmarkapp.algorithms.Constants;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MyBenchmarkHandler extends Handler {

    private Handler mUIHandler;
    private Benchmark[] benchmarks = new Benchmark[Constants.BLOCK_SIZES.length];


    public MyBenchmarkHandler(Looper myLooper, Handler mUIHandler) {
        super(myLooper);
        this.mUIHandler = mUIHandler;
    }

    public void newBenchmarks() {
        for (int i = 0; i < Constants.BLOCK_SIZES.length; i++) {
            benchmarks[i] = new Benchmark(Constants.BLOCK_SIZES[i]);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == Constants.BENCHMARK_MESSAGE_NEW) {
            newBenchmarks();
            mUIHandler.obtainMessage(msg.what).sendToTarget();
        } else {
            BenchmarkMessage message = (BenchmarkMessage) msg.obj;
            int algorithm = message.algorithmNum;
            int iterations = message.iterations;
            int sizeId = message.sizeId;

            message.setMessageString(algTest(algorithm, iterations, sizeId));
            mUIHandler.obtainMessage(Constants.BENCHMARK_MESSAGE_DONE, message).sendToTarget();
        }
    }

    String algTest(int algorithm, int iterations, int sizeId) {
        boolean jniTest = false;
        int size = Constants.BLOCK_SIZES[sizeId];

        StringBuilder sb = new StringBuilder();
        sb.append(Constants.ALGORITHM_NAMES[algorithm]);
        sb.append(" blockSize: ");
        sb.append(size);
        sb.append(" executionTime: ");

        if (benchmarks[sizeId] == null) {
            benchmarks[sizeId] = new Benchmark(size);
        }

        Benchmark bm = benchmarks[sizeId];

        while (iterations-- > 0) {
            long time = 0L;
            switch (Constants.ALGORITHMS.values()[algorithm]) {
                case JNI_EMPTY:
                    time = bm.JNIBenchmarkEmpty();
                    jniTest = true;
                    break;
                case JNI_PARAMS:
                    time = bm.JNIBenchmarkParams(size);
                    jniTest = true;
                    break;
                case JNI_VECTOR_CONVERSION:
                    time = bm.JNIBenchmarkVectorConversion(size);
                    jniTest = true;
                    break;
                case JNI_COLUMBIA:
                    time = bm.JNIBenchmarkColumbia(size);
                    jniTest = true;
                    break;
                case FFT_JAVA_ITERATIVE_PRINCETON:
                    time = bm.FFTJavaIterativePrinceton();
                    break;
                case FFT_JAVA_RECURSIVE_PRINCETON:
                    time = bm.FFTJavaRecursivePrinceton();
                    break;
                case FFT_JAVA_ITERATIVE_COLUMBIA:
                    time = bm.FFTJavaIterativeColumbia();
                    break;
                case FFT_CPP_ITERATIVE_PRINCETON:
                    time = bm.FFTCppIterativePrinceton(0);
                    break;
                case FFT_CPP_RECURSIVE_PRINCETON:
                    time = bm.FFTCppRecursivePrinceton(0);
                    break;
                case FFT_CPP_ITERATIVE_COLUMBIA:
                    time = bm.FFTCppIterativeColumbia(0);
                    break;
                case FFT_CPP_KISS:
                    time = bm.FFTCppKiss(0);
                    break;
                case NEON_CPP_RECURSIVE:
                    time = bm.FFTCppRecursiveNeon(0);
                    break;
                case NEON_CPP_ITERATIVE:
                    time = bm.FFTCppIterativeNeon(0);
                    break;
                case ARR_CPP_ITERATIVE_PRINCETON:
                    time = bm.FFTCppIterativePrinceton(1);
                    break;
                case ARR_CPP_RECURSIVE_PRINCETON:
                    time = bm.FFTCppRecursivePrinceton(1);
                    break;
                case ARR_CPP_ITERATIVE_COLUMBIA:
                    time = bm.FFTCppIterativeColumbia(1);
                    break;
                case ARR_CPP_KISS:
                    time = bm.FFTCppKiss(1);
                    break;
                case ARR_CPP_NEON_RECURSIVE:
                    time = bm.FFTCppRecursiveNeon(1);
                    break;
                case ARR_CPP_NEON_ITERATIVE:
                    time = bm.FFTCppIterativeNeon(1);
                    break;
                case FLOAT_JAVA_ITERATIVE_PRINCETON:
                    time = bm.FloatFFTJavaIterativePrinceton();
                    break;
                case FLOAT_JAVA_RECURSIVE_PRINCETON:
                    time = bm.FloatFFTJavaRecursivePrinceton();
                    break;
                case FLOAT_JAVA_ITERATIVE_COLUMBIA:
                    time = bm.FloatFFTJavaIterativeColumbia();
                    break;
                case FLOAT_CPP_ITERATIVE_PRINCETON:
                    time = bm.FloatFFTCppIterativePrinceton(0);
                    break;
                case FLOAT_CPP_RECURSIVE_PRINCETON:
                    time = bm.FloatFFTCppRecursivePrinceton(0);
                    break;
                case FLOAT_CPP_ITERATIVE_COLUMBIA:
                    time = bm.FloatFFTCppIterativeColumbia(0);
                    break;
                case ARR_CPP_FLOAT_ITERATIVE_PRINCETON:
                    time = bm.FloatFFTCppIterativePrinceton(1);
                    break;
                case ARR_CPP_FLOAT_RECURSIVE_PRINCETON:
                    time = bm.FloatFFTCppRecursivePrinceton(1);
                    break;
                case ARR_CPP_FLOAT_ITERATIVE_COLUMBIA:
                    time = bm.FloatFFTCppIterativeColumbia(1);
                    break;
                default:
                    break;
            }
            sb.append(jniTest ? time/1000.0 : time/1000000.0);
            sb.append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
