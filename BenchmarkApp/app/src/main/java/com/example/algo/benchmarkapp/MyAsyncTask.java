package com.example.algo.benchmarkapp;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.example.algo.benchmarkapp.algorithms.Constants;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {

    OnTaskCompleted listener;

    public MyAsyncTask(OnTaskCompleted l) {
        listener = l;
    }

    @Override
    protected String doInBackground(Integer... input) {
        int numIter = input[0];
        int algorithm = input[1];
        int N = input[2];
        System.out.println("next power: " + nextPowerOfTwo(numIter));
        StringBuilder sb = new StringBuilder();
        while (numIter-- > 0) {
            long start = SystemClock.elapsedRealtimeNanos();
            switch (Constants.ALGORITHMS.values()[algorithm]) {
                case FFT_JAVA_ITERATIVE:
                    Benchmark.FFTJavaIterative(N);
                    break;
                case FFT_JAVA_RECURSIVE:
                    Benchmark.FFTJavaRecursive(N);
                    break;
                default:
                    break;
            }
            long diff = SystemClock.elapsedRealtimeNanos() - start;
            sb.append(diff/1000000.0 + " ms\n");
        }
        return String.valueOf(sb.toString());
    }

    // Executes on UI thread
    @Override
    protected void onPostExecute(String s) {
        listener.saveResult(s);
        listener.startNextTest();
    }

    private int nextPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }
}
