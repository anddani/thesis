package com.example.algo.whistledetector;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.algo.whistledetector.Constants.MIN_AMPLITUDE;
import static com.example.algo.whistledetector.Constants.TARGET_FREQUENCY;

public class FrequencyDetectorFragment extends Fragment {

    TextView tv_freq, tv_mag;
    RelativeLayout rl;

    private static final String LOG_TAG = "FrequencyDetectorFrag";

    /**
     * Sends a message to the UI thread to update background color depending on the frequency
     * with the largest amplitude
     *
     * @param freq [frequency, amplitude]
     */
    public void updateView(int[] freq) {
        // Change color  of screen when target frequency is present
        if (freq[0] > TARGET_FREQUENCY - 100 && freq[0] < TARGET_FREQUENCY + 100 && freq[1] > MIN_AMPLITUDE) {
            mUIHandler.obtainMessage(1, freq[0], freq[1]).sendToTarget();
        } else {
            mUIHandler.obtainMessage(2, freq[0], freq[1]).sendToTarget();
        }
    }

    private final Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                rl.setBackgroundColor(Color.GREEN);
            } else if (msg.what == 2) {
                rl.setBackgroundColor(Color.RED);
            }
            tv_freq.setText("Frequency: " + msg.arg1);
            tv_mag.setText("Amplitude: " + msg.arg2);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        Log.d(LOG_TAG, "onCreateView() called");

        if (savedInstanceState == null) {
            rl = (RelativeLayout)rootView.findViewById(R.id.activity_main);

            tv_freq = (TextView)rootView.findViewById(R.id.frequency);
            tv_mag = (TextView)rootView.findViewById(R.id.magnitude);

            rl.setBackgroundColor(Color.RED);
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
