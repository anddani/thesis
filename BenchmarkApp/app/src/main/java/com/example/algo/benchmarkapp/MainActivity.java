package com.example.algo.benchmarkapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.algo.benchmarkapp.algorithms.Constants;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    private TextView logTextView;
    private EditText numIter;
    private EditText dataSize;

    private int currentAlgorithm;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void startNextTest() {
        MyAsyncTask myAsyncTask = new MyAsyncTask(MainActivity.this);
        int iter = Integer.parseInt(numIter.getText().toString());
        int N = Integer.parseInt(dataSize.getText().toString());
        int algorithm = currentAlgorithm;
        if (currentAlgorithm < Constants.NUM_ALGORITHMS) {
            myAsyncTask.execute(iter, algorithm, N);
            saveResult("-- Running Algorithm " + Constants.ALGORITHM_NAMES[algorithm] + " for " + iter + " iterations\n");
        }
        currentAlgorithm++;
    }
    @Override
    public void saveResult(String result) {
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
        Benchmark.FFTCpp(1);

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
                // Clear screen between tests
                logTextView.setText("");
                startNextTest();
//                FileOutputStream out;
//                try {
//                    out = openFileOutput("test.out", Context.MODE_PRIVATE);
//                    out.write("TEST".getBytes());
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
