package com.example.algo.benchmarkapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.example.algo.benchmarkapp.algorithms.Constants;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    private TextView logTextView;
    private EditText numIter;
    private EditText dataSize;

    private static final String DEFAULT_ITER = "5";
    private static final String DEFAULT_N = "44000";

    private Benchmark bm;
    private Benchmark bmMemory;

    private int currentAlgorithm;

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
            R.id.fft10,
    };

    private static final String[] buttonLabels = {
            "Princeton Java Iterative",
            "Princeton Java Recursive",
            "Columbia Java Iterative",
            "Java JTransforms",
            "C++ Princeton converted Iterative",
            "C++ Princeton converted Recursive",
            "C++ Columbia converted Iterative",
            "C++ KISS",
            "C++ Columbia optimized Iterative",
            "Java Columbia optimized Iterative"
    };

    private Button[] buttons = new Button[buttonIds.length];

    /**
     * Runs a new test in a new thread.
     */
    @Override
    public void startNextTest() {
        MyAsyncTask myAsyncTask = new MyAsyncTask(MainActivity.this, bm);

        String iterText = numIter.getText().toString();
        // If not specified, set to DEFAULT_ITER
        if (iterText.isEmpty()) {
            iterText = DEFAULT_ITER;
        }
        int iter = Integer.parseInt(iterText);
        String dataSizeText = dataSize.getText().toString();
        // If not specified, set to DEFAULT_N
        if (dataSizeText.isEmpty()) {
            dataSizeText = DEFAULT_N;
        }
        int N = nextPowerOfTwo(Integer.parseInt(dataSizeText));
        int algorithm = currentAlgorithm;
        if (currentAlgorithm < Constants.NUM_ALGORITHMS) {
            myAsyncTask.execute(iter, algorithm);
            saveResult("-- Running Algorithm " + Constants.ALGORITHM_NAMES[algorithm] + " N=" + N + " for " + iter + " iter\n");
        }
        currentAlgorithm++;
    }

    /**
     * Logs the result on the screen
     *
     * @param result Execution time
     */
    @Override
    public void saveResult(String result) {
        FileOutputStream out;
        try {
            out = openFileOutput("data.out", MODE_APPEND);
            result = result + "\n";
            out.write(result.getBytes());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set TextView as scrollable
        logTextView = (TextView) findViewById(R.id.log_text);
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        logTextView.append("\n");

        // To get content of edittext
        numIter = (EditText) findViewById(R.id.num_iter);
        dataSize = (EditText) findViewById(R.id.data_size);

        Button btn = (Button) findViewById(R.id.run_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAlgorithm = 0;
                String dataSizeText = dataSize.getText().toString();
                if (dataSizeText.isEmpty()) {
                    dataSizeText = DEFAULT_N;
                }
                int N = nextPowerOfTwo(Integer.parseInt(dataSizeText));

                // Reset file content for each run
                File file = new File(getFilesDir(), "data.out");
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("DID NOT GET DELETED");
                }

                // Generates new input
                bm = new Benchmark(N);

                // Clear screen between tests
                logTextView.setText("");
                startNextTest();
            }
        });

        bmMemory = new Benchmark(nextPowerOfTwo(Integer.parseInt(DEFAULT_N)));
        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = (Button) findViewById(buttonIds[i]);
            buttons[i].setText(buttonLabels[i]);
            buttons[i].setTextSize(10);
            System.out.println("Setting up onClick for button nr: " + i);
            setOnClick(buttons[i], i);
        }
    }

    private void setOnClick(final Button btn, final int n) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button pressed with n: " + n);
                MyAsyncTask myAsyncTask = new MyAsyncTask(null, bmMemory);
                myAsyncTask.execute(1, n);
            }
        });
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
