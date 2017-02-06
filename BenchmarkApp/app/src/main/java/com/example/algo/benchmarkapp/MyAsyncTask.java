package com.example.algo.benchmarkapp;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Constants;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {

    OnTaskCompleted listener;
    Benchmark bm;

    public MyAsyncTask(OnTaskCompleted l, Benchmark bm) {
        listener = l;
        this.bm = bm;
    }

    @Override
    protected String doInBackground(Integer... input) {
        int numIter = input[0];
        int algorithm = input[1];
        StringBuilder sb = new StringBuilder();

        while (numIter-- > 0) {
            long time = 0;
            switch (Constants.ALGORITHMS.values()[algorithm]) {
                case FFT_JAVA_ITERATIVE_PRINCETON:
                    time = bm.FFTJavaIterativePrinceton();
                    break;
                case FFT_JAVA_RECURSIVE_PRINCETON:
                    time = bm.FFTJavaRecursivePrinceton();
                    break;
                case FFT_CPP_ITERATIVE_PRINCETON:
                    time = bm.FFTCppIterativePrinceton();
                    break;
                case FFT_CPP_RECURSIVE_PRINCETON:
                    time = bm.FFTCppRecursivePrinceton();
                    break;
                case FFT_JAVA_ITERATIVE_COLUMBIA:
                    time = bm.FFTJavaIterativeColumbia();
                    break;
                case FFT_CPP_KISS:
                    time = bm.FFTCppKiss();
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
