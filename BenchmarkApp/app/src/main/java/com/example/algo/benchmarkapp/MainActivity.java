package com.example.algo.benchmarkapp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

    private Benchmark[] benchmarks = new Benchmark[Constants.BLOCK_SIZES.length];

    private Handler mUIHandler = new Handler() {
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

        Button btn = (Button) findViewById(R.id.run_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Reset file content for each run
                File file = new File(getFilesDir(), "data.out");
                boolean deleted = file.delete();
                if (!deleted) {
                    System.out.println("DID NOT GET DELETED");
                }

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
    }

    private void setOnClick(final Button btn, final int n) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button pressed with n: " + n);

                String dataSizeText = dataSize.getText().toString();
                if (dataSizeText.isEmpty()) {
                    dataSizeText = DEFAULT_N;
                }
                int d = nextPowerOfTwo(Integer.parseInt(dataSizeText));

                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, n, d);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        });
    }


    /**
     * Starts the benchmark by sending messages to the handler thread
     * for all block sizes and all algorithms
     */
    private void startBenchmarks() {
        for (int a = 0; a < Constants.NUM_ALGORITHMS; a++) {
            for (int b = 0; b < Constants.BLOCK_SIZES.length; b++) {
                System.out.println("Sending message alg: " + a + " blockId: " + b);
                BenchmarkMessage message = new BenchmarkMessage(Constants.BENCHMARK_ITER, a, b);
                mTaskHandler.obtainMessage(0, message).sendToTarget();
            }
        }
    }

    /**
     * Logs the result on the screen
     *
     * @param result Execution time
     */
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

    public void newBenchmarks() {
        for (int i = 0; i < Constants.BLOCK_SIZES.length; i++) {
            benchmarks[i] = new Benchmark(Constants.BLOCK_SIZES[i]);
        }
    }

    private Handler mTaskHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == Constants.BENCHMARK_MESSAGE_NEW) {
                newBenchmarks();
                mUIHandler.obtainMessage(msg.what).sendToTarget();
            } else {
                BenchmarkMessage message = (BenchmarkMessage) msg.obj;
                int algorithm = message.algorithm;
                int iterations = message.iterations;
                StringBuilder sb = new StringBuilder();
                double[] results = new double[iterations];
                Benchmark bm = benchmarks[message.sizeId];

                while (iterations-- > 0) {
                    long time = 0L;
                    switch (Constants.ALGORITHMS.values()[algorithm]) {
                        case FFT_JAVA_ITERATIVE_PRINCETON:
                            time = bm.FFTJavaIterativePrinceton();
                            break;
                        case FFT_JAVA_RECURSIVE_PRINCETON:
                            time = bm.FFTJavaRecursivePrinceton();
                            break;
                        case FFT_JAVA_ITERATIVE_COLUMBIA:
                            time = bm.FFTJavaIterativeColumbia();
                            break;
                        case FFT_CPP_ITERATIVE_PRINCETON:
                            time = bm.FFTCppIterativePrinceton();
                            break;
                        case FFT_CPP_RECURSIVE_PRINCETON:
                            time = bm.FFTCppRecursivePrinceton();
                            break;
                        case FFT_CPP_ITERATIVE_COLUMBIA:
                            time = bm.FFTCppIterativeColumbia();
                            break;
                        case FFT_CPP_ITERATIVE_COLUMBIA_OPTIMIZED:
                            time = bm.FFTCppIterativeColumbiaOptimized();
                            break;
                        case FFT_CPP_KISS:
                            time = bm.FFTCppKiss();
                            break;
                        case JNI_EMPTY:
                            time = bm.JNIBenchmarkEmpty();
                            break;
                        case JNI_PARAMS:
                            time = bm.JNIBenchmarkParams();
                            break;
                        case JNI_VECTOR_CONVERSION:
                            time = bm.JNIBenchmarkVectorConversion();
                            break;
                        default:
                            break;
                    }
                    sb.append("Algorithm: ");
                    sb.append(Constants.ALGORITHM_NAMES[algorithm]);
                    sb.append(" block size: ");
                    sb.append(Constants.BLOCK_SIZES[message.sizeId]);
                    sb.append(" ");
                    sb.append(time / 1000000.0);
                    sb.append(" ms\n");
                    results[iterations] = time / 1000000.0;
                }

                // Calculate average
                double sum = 0.0;
                for (double r : results) {
                    sum += r;
                }

                String returnString = String.valueOf(sb.toString() + "average: " + sum / results.length + " ms\n");
                message.setMessageString(returnString);
                mUIHandler.obtainMessage(Constants.BENCHMARK_MESSAGE_DONE, message).sendToTarget();
            }
        }
    };

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
