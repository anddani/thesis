package com.example.algo.benchmarkapp;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Constants;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {

    OnTaskCompleted listener;
    double[] fftInitialData;

    public MyAsyncTask(OnTaskCompleted l, double[] d) {
        listener = l;
        fftInitialData = d;
    }

    @Override
    protected String doInBackground(Integer... input) {
        int numIter = input[0];
        int algorithm = input[1];
        StringBuilder sb = new StringBuilder();
        while (numIter-- > 0) {
            long time = 0;
            switch (Constants.ALGORITHMS.values()[algorithm]) {
                case FFT_JAVA_ITERATIVE:
                    time = Benchmark.FFTJavaIterative(fftInitialData);
                    break;
                case FFT_JAVA_RECURSIVE:
                    time = Benchmark.FFTJavaRecursive(fftInitialData);
                    break;
                case FFT_CPP_ITERATIVE:
                    time = Benchmark.FFTCppIterative(fftInitialData);
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
