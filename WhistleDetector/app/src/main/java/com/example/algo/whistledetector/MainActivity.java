package com.example.algo.whistledetector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRecordAudioPermission();

        switch (this.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (getSupportFragmentManager().findFragmentByTag("FREQ_FRAGMENT") == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_main, new FrequencyDetectorFragment(), "FREQ_FRAGMENT")
                            .commit();
                }

                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (getSupportFragmentManager().findFragmentByTag("PLOT_FRAGMENT") == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_main, new PlotFragment(), "PLOT_FRAGMENT")
                            .commit();
                }
                break;
        }
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
}
