package com.example.algo.whistledetector;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PlotFragment extends Fragment {

    View rootView;

    // Reusable data points
    private DataPoint[] dataPoints;
    private LineGraphSeries<DataPoint> series;

    GraphView graph;

    private static final String LOG_TAG = "PlotFrag";

    /**
     * Sets new dataPoints in the graph and refreshes the view
     *
     * @param result result from the FFT
     */
    public void updateView(Complex[] result) {
        final double frequencyResolution = (1.0 * Constants.SAMPLING_RATE) / (1.0 * result.length);

        for (int i = 0; i < result.length/2; i++) {
            double frequency = i * frequencyResolution;
            double amplitude = result[i].abs();

            dataPoints[i] = new DataPoint(frequency, amplitude);
        }
        series.resetData(dataPoints);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.plot_layout, container, false);
        Log.d(LOG_TAG, "onCreateView() called");

        if (savedInstanceState == null) {
            graph = (GraphView) rootView.findViewById(R.id.graph);

            Viewport mViewport = graph.getViewport();

            // Set x limits
            graph.getViewport().setXAxisBoundsManual(true);
            mViewport.setMinX(0);
            mViewport.setMaxX(Constants.SAMPLING_RATE / 2);

            // Set y limits
            graph.getViewport().setYAxisBoundsManual(true);
            mViewport.setMinY(0);
            mViewport.setMaxY(20);

            // Initialize dataPoints once
            int bufferSize = ((MainActivity)getActivity()).mThread.getBufferSize();
            dataPoints = new DataPoint[bufferSize / 2];
            for (int i = 0; i < dataPoints.length; i++) {
                dataPoints[i] = new DataPoint(0.0, 0.0);
            }
            series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
