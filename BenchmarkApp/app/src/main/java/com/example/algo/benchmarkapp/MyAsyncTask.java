package com.example.algo.benchmarkapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MyAsyncTask extends AsyncTask<Integer, String, String> {

    WeakReference<Activity> mWeakActivity;

    public MyAsyncTask(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);

    }

    @Override
    protected String doInBackground(Integer... input) {
        System.out.println("next power: " + nextPowerOfTwo(input[0]));
        long start = SystemClock.elapsedRealtime();
        Benchmark.FFTJava(16);
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        long diff = SystemClock.elapsedRealtime() - start;
        return String.valueOf(diff);
    }

    @Override
    protected void onPostExecute(String s) {
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            TextView logTextView = (TextView) activity.findViewById(R.id.log_text);

            logTextView.append(s + " ms\n");

            int scrollAmount = getScrollAmount(logTextView);
            // Scroll number of added lines outside of bottom
            if (scrollAmount > 0)
                logTextView.scrollTo(0, scrollAmount);
            else
                logTextView.scrollTo(0, 0);
        }
    }

    private int getScrollAmount(TextView tv) {
        return tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
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
