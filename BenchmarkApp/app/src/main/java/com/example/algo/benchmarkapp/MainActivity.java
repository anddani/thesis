package com.example.algo.benchmarkapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.algo.benchmarkapp.algorithms.Constants;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;
    private EditText dataSize;

    private static final String DEFAULT_N = "44000";

    private MyBenchmarkHandler mTaskHandler;

    // Run this handler on UI thread. Updates log
    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constants.BENCHMARK_MESSAGE_NEW) {
                startBenchmarks();
            } else if (msg.what == Constants.BENCHMARK_MESSAGE_DONE){
                BenchmarkMessage message = (BenchmarkMessage) msg.obj;
                saveResult(message.messageString);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set TextView as scrollable
        logTextView = (TextView) findViewById(R.id.log_text);
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        logTextView.append("\n");

        // To get content of edittext
        dataSize = (EditText) findViewById(R.id.data_size);

        Button runBenchmarksButton = (Button) findViewById(R.id.run_button);
        Button runOneBenchmarkButton = (Button) findViewById(R.id.run_one);

        HandlerThread myThread = new HandlerThread("Worker Thread");
        myThread.start();
        mTaskHandler = new MyBenchmarkHandler(myThread.getLooper(), mUIHandler);

        final Spinner spinner = (Spinner) findViewById(R.id.mySpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constants.ALGORITHM_NAMES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        runBenchmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Delete output file each run
                File sdCard = Environment.getExternalStorageDirectory();
                if (!new File(sdCard.getAbsolutePath() + "/data.out").delete()) {
                    System.out.println("DID NOT GET DELETED");
                }

                saveResult("numTests " + Constants.BENCHMARK_ITER);

                mTaskHandler.obtainMessage(Constants.BENCHMARK_MESSAGE_NEW).sendToTarget();

                // Clear screen between tests
                logTextView.setText("");
            }
        });

        runOneBenchmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alg = spinner.getSelectedItemPosition();
                System.out.println("Button pressed with n: " + alg);

                // Clear screen between tests
                logTextView.setText("");

                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, alg, Constants.BLOCK_SIZES.length-1);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        });

        requestWritePerimission();
    }

    /**
     * Starts the benchmark by sending messages to the handler thread
     * for all block sizes and all algorithms
     */
    private void startBenchmarks() {
        for (int alg = 0; alg < Constants.NUM_ALGORITHMS; alg++) {
            for (int sizeId = 0; sizeId < Constants.BLOCK_SIZES.length; sizeId++) {
                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, alg, sizeId);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        }
    }

    /**
     * Logs the result on the screen and save
     * it to a file called 'data.out' on the device.
     *
     * @param result Execution time
     */
    public void saveResult(String result) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "data.out"), true);
            result = result + "\n";
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logTextView.append(result + "\n");
        int scrollAmount = getScrollAmount(logTextView);

        // Scroll number of added lines outside of bottom
        if (scrollAmount > 0)
            logTextView.scrollTo(0, scrollAmount);
        else
            logTextView.scrollTo(0, 0);
    }

    private int getScrollAmount(TextView tv) {
        return tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
    }

    private void requestWritePerimission() {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }
    }
}
