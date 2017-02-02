package com.example.algo.benchmarkapp;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Constants;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {

    OnTaskCompleted listener;
    double[] fftInitialRe;
    double[] fftInitialIm;

    public MyAsyncTask(OnTaskCompleted l, double[] re, double[] im) {
        listener = l;
        fftInitialRe = re;
        fftInitialIm = im;
    }

    @Override
    protected String doInBackground(Integer... input) {
        int numIter = input[0];
        int algorithm = input[1];
        StringBuilder sb = new StringBuilder();

        // Benckmark bm = new Benchmark(fftInitialRe, fftInitialIm);

        while (numIter-- > 0) {
            long time = 0;
            switch (Constants.ALGORITHMS.values()[algorithm]) {
                case FFT_JAVA_ITERATIVE:
                    time = Benchmark.FFTJavaIterative(fftInitialRe, fftInitialIm);
                    break;
                case FFT_JAVA_RECURSIVE:
                    time = Benchmark.FFTJavaRecursive(fftInitialRe, fftInitialIm);
                    break;
                case FFT_CPP_ITERATIVE:
                    time = Benchmark.FFTCppIterative(fftInitialRe, fftInitialIm);
                    break;
                case FFT_CPP_RECURSIVE:
                    time = Benchmark.FFTCppRecursive(fftInitialRe, fftInitialIm);
                    break;
                default:
                    break;
            }
            sb.append(time/1000000.0 + " ms\n");
        }
        return String.valueOf(sb.toString());
    }

    // Executes on UI thread
    @Override
    protected void onPostExecute(String s) {
        listener.saveResult(s);
        listener.startNextTest();
    }
}
