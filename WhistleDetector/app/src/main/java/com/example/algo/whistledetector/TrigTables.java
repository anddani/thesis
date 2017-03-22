package com.example.algo.whistledetector;

public class TrigTables {
    public double[] cos;
    public double[] sin;

    public TrigTables(int n) {

        // Trigonometric tables
        cos = new double[n/2];
        sin = new double[n/2];

        for(int i=0; i<n/2; i++) {
            cos[i] = Math.cos(-2*Math.PI*i/n);
            sin[i] = Math.sin(-2*Math.PI*i/n);
        }
    }
}
