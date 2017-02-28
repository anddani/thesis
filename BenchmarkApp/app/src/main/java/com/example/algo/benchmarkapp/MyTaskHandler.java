package com.example.algo.benchmarkapp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.algo.benchmarkapp.algorithms.Constants;

//public class MyTaskHandler extends Thread {
public class MyTaskHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {

        if (msg.what == Constants.BENCHMARK_MESSAGE_NEW) {
            System.out.println("Sending new benchmark message");
//            newBenchmarks();
//            mUIHandler.obtainMessage(msg.what).sendToTarget();
        } else {
            int algorithm = msg.arg1;
            int numIter = msg.arg2;
            StringBuilder sb = new StringBuilder();
            double[] results = new double[numIter];
            BenchmarkMessage message = (BenchmarkMessage) msg.obj;
//            Benchmark bm = benchmarks[message.sizeId];

            while (numIter-- > 0) {
                long time = 0L;
//                switch (Constants.ALGORITHMS.values()[algorithm]) {
//                    case FFT_JAVA_ITERATIVE_PRINCETON:
//                        time = bm.FFTJavaIterativePrinceton();
//                        break;
//                    case FFT_JAVA_RECURSIVE_PRINCETON:
//                        time = bm.FFTJavaRecursivePrinceton();
//                        break;
//                    case FFT_JAVA_ITERATIVE_COLUMBIA:
//                        time = bm.FFTJavaIterativeColumbia();
//                        break;
//                    case FFT_JAVA_JTRANSFORMS:
//                        time = bm.FFTJavaJTransforms();
//                        break;
//                    case FFT_CPP_ITERATIVE_PRINCETON:
//                        time = bm.FFTCppIterativePrinceton();
//                        break;
//                    case FFT_CPP_RECURSIVE_PRINCETON:
//                        time = bm.FFTCppRecursivePrinceton();
//                        break;
//                    case FFT_CPP_ITERATIVE_COLUMBIA:
//                        time = bm.FFTCppIterativeColumbia();
//                        break;
//                    case FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED:
//                        time = bm.FFTCppIterativeColumbiaOptimized();
//                        break;
//                    case FFT_CPP_KISS:
//                        time = bm.FFTCppKiss();
//                        break;
//                    case JNI_EMPTY:
//                        time = bm.JNIBenchmarkEmpty();
//                        break;
//                    case JNI_PARAMS:
//                        time = bm.JNIBenchmarkParams();
//                        break;
//                    case JNI_VECTOR_CONVERSION:
//                        time = bm.JNIBenchmarkVectorConversion();
//                        break;
//                    default:
//                        break;
//                }
                sb.append(time / 1000000.0);
                sb.append(" ms\n");
                results[numIter] = time / 1000000.0;
            }
            double sum = 0.0;
            for (double r : results) {
                sum += r;
            }
            System.out.println("Done with algorithm nr: " + algorithm);
//            if (mUIHandler == null) {
//                System.out.println("ERROR: mUIHandler is null");
//            } else {
//                String returnString = String.valueOf(sb.toString() + "averate: " + sum / results.length + " ms\n");
//                mUIHandler.obtainMessage(0, returnString).sendToTarget();
//            }
        }
    }

    //    Benchmark[] benchmarks;
//    Handler mUIHandler;
//    public Handler mTaskHandler;
//
//    public MyTaskHandler(Handler mUIHandler) {
//        this.mUIHandler = mUIHandler;
//    }
//
//    public void newBenchmarks() {
//        for (int i = 0; i < Constants.BLOCK_SIZES.length; i++) {
//            benchmarks[i] = new Benchmark(Constants.BLOCK_SIZES[i]);
//        }
//    }

//    @Override
//    public void run() {
//        Looper.prepare();
//
//        mTaskHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == Constants.NEW_BENCHMARKS_MESSAGE) {
//                    newBenchmarks();
//                    mUIHandler.obtainMessage(msg.what).sendToTarget();
//                } else {
//                    int algorithm = msg.arg1;
//                    int numIter = msg.arg2;
//                    StringBuilder sb = new StringBuilder();
//                    double[] results = new double[numIter];
//                    BenchmarkMessage message = (BenchmarkMessage) msg.obj;
//                    Benchmark bm = benchmarks[message.sizeId];
//
//                    while (numIter-- > 0) {
//                        long time = 0L;
//                        switch (Constants.ALGORITHMS.values()[algorithm]) {
//                            case FFT_JAVA_ITERATIVE_PRINCETON:
//                                time = bm.FFTJavaIterativePrinceton();
//                                break;
//                            case FFT_JAVA_RECURSIVE_PRINCETON:
//                                time = bm.FFTJavaRecursivePrinceton();
//                                break;
//                            case FFT_JAVA_ITERATIVE_COLUMBIA:
//                                time = bm.FFTJavaIterativeColumbia();
//                                break;
//                            case FFT_JAVA_JTRANSFORMS:
//                                time = bm.FFTJavaJTransforms();
//                                break;
//                            case FFT_CPP_ITERATIVE_PRINCETON:
//                                time = bm.FFTCppIterativePrinceton();
//                                break;
//                            case FFT_CPP_RECURSIVE_PRINCETON:
//                                time = bm.FFTCppRecursivePrinceton();
//                                break;
//                            case FFT_CPP_ITERATIVE_COLUMBIA:
//                                time = bm.FFTCppIterativeColumbia();
//                                break;
//                            case FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED:
//                                time = bm.FFTCppIterativeColumbiaOptimized();
//                                break;
//                            case FFT_CPP_KISS:
//                                time = bm.FFTCppKiss();
//                                break;
//                            case JNI_EMPTY:
//                                time = bm.JNIBenchmarkEmpty();
//                                break;
//                            case JNI_PARAMS:
//                                time = bm.JNIBenchmarkParams();
//                                break;
//                            case JNI_VECTOR_CONVERSION:
//                                time = bm.JNIBenchmarkVectorConversion();
//                                break;
//                            default:
//                                break;
//                        }
//                        sb.append(time / 1000000.0);
//                        sb.append(" ms\n");
//                        results[numIter] = time / 1000000.0;
//                    }
//                    double sum = 0.0;
//                    for (double r : results) {
//                        sum += r;
//                    }
//                    System.out.println("Done with algorithm nr: " + algorithm);
//                    if (mUIHandler == null) {
//                        System.out.println("ERROR: mUIHandler is null");
//                    } else {
//                        String returnString = String.valueOf(sb.toString() + "averate: " + sum / results.length + " ms\n");
//                        mUIHandler.obtainMessage(0, returnString).sendToTarget();
//                    }
//                }
//            }
//        };
//
//        Looper.loop();
//        System.out.println("TESTING");
//    }
}
