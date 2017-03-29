package com.example.algo.whistledetector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private static final String FREQ_FRAGMENT = "FREQ_FRAGMENT";
    private static final String PLOT_FRAGMENT = "PLOT_FRAGMENT";

    FrequencyDetectorFragment fdf;
    PlotFragment pf;
    FragmentManager fm;

    public RecorderThread mThread;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Complex[] result = (Complex[]) msg.obj;

            if (fm.findFragmentByTag(FREQ_FRAGMENT).isVisible()) {
                fdf.updateView(ConvertDomain.maxAmplitude(result));
            } else {
                pf.updateView(result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate() is called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRecordAudioPermission();

        fm = getSupportFragmentManager();

        mThread = new RecorderThread(mHandler);
        mThread.start();

        // Create the two fragments
        if (savedInstanceState == null) {
            fdf = new FrequencyDetectorFragment();
            fm.beginTransaction()
                    .add(R.id.activity_main, fdf, FREQ_FRAGMENT)
                    .commit();

            // Let PlotFragment start as hidden
            pf = new PlotFragment();
            fm.beginTransaction()
                    .add(R.id.activity_main, pf, PLOT_FRAGMENT)
                    .hide(pf)
                    .commit();
        }

        setFragment();
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged() called");
        setFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy() called");
        mThread.stopRecording();
    }

    private void requestRecordAudioPermission() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                }
            }
        }
    }

    // Shows and hides appropriate fragments depending on screen orientation
    void setFragment() {
        switch (this.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (fm.findFragmentByTag(FREQ_FRAGMENT) != null) {
                    fm.beginTransaction()
                            .show(fm.findFragmentByTag(FREQ_FRAGMENT))
                            .commit();
                }
                if (fm.findFragmentByTag(PLOT_FRAGMENT) != null) {
                    fm.beginTransaction()
                            .hide(fm.findFragmentByTag(PLOT_FRAGMENT))
                            .commit();
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (fm.findFragmentByTag(PLOT_FRAGMENT) != null) {
                    fm.beginTransaction()
                            .show(fm.findFragmentByTag(PLOT_FRAGMENT))
                            .commit();
                }
                if (fm.findFragmentByTag(FREQ_FRAGMENT) != null) {
                    fm.beginTransaction().
                            hide(fm.findFragmentByTag(FREQ_FRAGMENT))
                            .commit();
                }
                break;
        }
    }
}
