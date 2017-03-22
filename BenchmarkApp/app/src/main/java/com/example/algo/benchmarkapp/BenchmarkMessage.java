package com.example.algo.benchmarkapp;

public class BenchmarkMessage {

    public int iterations;
    public int algorithmNum;
    public int sizeId;
    public String messageString;

    public BenchmarkMessage(int n, int a, int s) {
        iterations = n;
        algorithmNum = a;
        sizeId = s;
    }

    public void setMessageString(String m) {
        messageString = m;
    }
}
