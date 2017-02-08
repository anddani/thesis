package com.example.algo.benchmarkapp;

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

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    private TextView logTextView;
    private EditText numIter;
    private EditText dataSize;

    private Benchmark bm;

    private int currentAlgorithm;

    /**
     * Runs a new test in a new thread.
     */
    @Override
    public void startNextTest() {
        MyAsyncTask myAsyncTask = new MyAsyncTask(MainActivity.this, bm);
        int iter = Integer.parseInt(numIter.getText().toString());

        // Input must be a power of two
        int N = nextPowerOfTwo(Integer.parseInt(dataSize.getText().toString()));
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
     * TODO: Print to file
     *
     * @param result Execution time
     */
    @Override
    public void saveResult(String result) {
//        FileOutputStream out;
//        try {
//            out = openFileOutput("test.out", Context.MODE_PRIVATE);
//            out.write("TEST".getBytes());
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
                int N = nextPowerOfTwo(Integer.parseInt(dataSize.getText().toString()));

                // Generates new input
                bm = new Benchmark(N);

                // Clear screen between tests
                logTextView.setText("");
                startNextTest();
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
