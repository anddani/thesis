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
import android.widget.Button;
import android.widget.EditText;
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

    private Button[] buttons = new Button[buttonIds.length];

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

        HandlerThread myThread = new HandlerThread("Worker Thread");
        myThread.start();
        mTaskHandler = new MyBenchmarkHandler(myThread.getLooper(), mUIHandler);


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

        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = (Button) findViewById(buttonIds[i]);
            buttons[i].setText(buttonLabels[i]);
            buttons[i].setTextSize(10);
            System.out.println("Setting up onClick for button nr: " + i);
            setOnClick(buttons[i], i);
        }
        requestWritePerimission();
    }

    private void setOnClick(final Button btn, final int alg) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button pressed with n: " + alg);

                String dataSizeText = dataSize.getText().toString();
                if (dataSizeText.isEmpty()) {
                    dataSizeText = DEFAULT_N;
                }
                int data = Constants.nextPowerOfTwo(Integer.parseInt(dataSizeText));
                int blockIndex = data == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(data-1) - 4;

                // Delete output file each run
                File sdCard = Environment.getExternalStorageDirectory();
                if (!new File(sdCard.getAbsolutePath() + "/data.out").delete()) {
                    System.out.println("DID NOT GET DELETED");
                }
                saveResult("numTests " + Constants.BENCHMARK_ITER);

                // Clear screen between tests
                logTextView.setText("");

                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, alg, blockIndex, Constants.ALG_TYPE);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        });
    }

    /**
     * Starts the benchmark by sending messages to the handler thread
     * for all block sizes and all algorithms
     */
    private void startBenchmarks() {
        // Run JNI Tests
        for (int test = 0; test < Constants.NUM_JNI_TESTS; test++) {
            for (int size = 500; size <= 5000; size+=500) {
                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, test, size, Constants.JNI_TYPE);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        }

        // Run Algorithm Tests
        for (int alg = 0; alg < Constants.NUM_ALGORITHMS; alg++) {
            for (int sizeId = 0; sizeId < Constants.BLOCK_SIZES.length; sizeId++) {
                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, alg, sizeId, Constants.ALG_TYPE);
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

    private static final int[] buttonIds = {
            R.id.fft1,
            R.id.fft2,
            R.id.fft3,
            R.id.fft4,
            R.id.fft5,
            R.id.fft6,
            R.id.fft7,
            R.id.fft8,
            R.id.fft9,
    };

    private static final String[] buttonLabels = {
            "Princeton Java Iterative",
            "Princeton Java Recursive",
            "Columbia Java Iterative",
            "C++ Princeton converted Iterative",
            "C++ Princeton converted Recursive",
            "C++ Columbia converted Iterative",
            "C++ KISS",
            "C++ Columbia optimized Iterative",
            "Java Columbia optimized Iterative"
    };
}
