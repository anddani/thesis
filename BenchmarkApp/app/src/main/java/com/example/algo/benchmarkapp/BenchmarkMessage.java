package com.example.algo.benchmarkapp;

public class BenchmarkMessage {

    public int iterations;
    public int algorithmNum;
    public int sizeId;
    public int testType;
    public String messageString;

    public BenchmarkMessage(int n, int a, int s, int tt) {
        iterations = n;
        algorithmNum = a;
        sizeId = s;
        testType = tt;
    }

    public void setMessageString(String m) {
        messageString = m;
    }
}
