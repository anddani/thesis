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
            int algorithm = message.algorithm;
            int iterations = message.iterations;
            int sizeId = message.sizeId;

            boolean jniTests = false;

            StringBuilder sb = new StringBuilder();
            sb.append(Constants.ALGORITHM_NAMES[algorithm]);
            sb.append(" blockSize: ");
            sb.append(Constants.BLOCK_SIZES[sizeId]);
            sb.append(" executionTime: ");

            if (benchmarks[sizeId] == null) {
                benchmarks[sizeId] = new Benchmark(Constants.BLOCK_SIZES[sizeId]);
            }

            Benchmark bm = benchmarks[sizeId];

            while (iterations-- > 0) {
                long time = 0L;
                switch (Constants.ALGORITHMS.values()[algorithm]) {
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
                        time = bm.FFTCppIterativePrinceton();
                        break;
                    case FFT_CPP_RECURSIVE_PRINCETON:
                        time = bm.FFTCppRecursivePrinceton();
                        break;
                    case FFT_CPP_ITERATIVE_COLUMBIA:
                        time = bm.FFTCppIterativeColumbia();
                        break;
                    case FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED:
                        time = bm.FFTCppIterativeColumbiaOptimized();
                        break;
                    case FFT_CPP_KISS:
                        time = bm.FFTCppKiss();
                        break;
                    case JNI_EMPTY:
                        time = bm.JNIBenchmarkEmpty();
                        jniTests = true;
                        break;
                    case JNI_PARAMS:
                        time = bm.JNIBenchmarkParams();
                        jniTests = true;
                        break;
                    case JNI_VECTOR_CONVERSION:
                        time = bm.JNIBenchmarkVectorConversion();
                        jniTests = true;
                        break;
                    case NEON_CPP_ITERATIVE:
                        time = bm.FFTCppIterativeNeon();
                        break;
                    default:
                        break;
                }
                // Have JNI times in us, FFT in ms
                sb.append(jniTests ? time/1000.0 : time/1000000.0);
                sb.append(" ");
            }
            sb.append("\n");

            message.setMessageString(sb.toString());
            mUIHandler.obtainMessage(Constants.BENCHMARK_MESSAGE_DONE, message).sendToTarget();
        }
    }
}
