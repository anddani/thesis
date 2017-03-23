package com.example.algo.benchmarkapp.algorithms;

/******************************************************************************
 *  Compilation:  javac FFTPrincetonRecursive.java
 *  Execution:    java FFTPrincetonRecursive n
 *  Dependencies: Complex.java
 *
 *  Compute the FFT and inverse FFT of a length n complex sequence.
 *  Bare bones implementation that runs in O(n log n) time. Our goal
 *  is to optimize the clarity of the code, rather than performance.
 *
 *  Limitations
 *  -----------
 *   -  assumes n is a power of 2
 *
 *   -  not the most memory efficient algorithm (because it uses
 *      an object type for representing complex numbers and because
 *      it re-allocates memory for the subarray, instead of doing
 *      in-place or reusing a single temporary array)
 *
 ******************************************************************************/

public class FloatFFTPrincetonRecursive {

    // compute the FFT of x[], assuming its length is a power of 2
    public static FloatComplex[] fft(FloatComplex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new FloatComplex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) { throw new RuntimeException("n is not a power of 2"); }

        // fft of even terms
        FloatComplex[] even = new FloatComplex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2 * k];
        }
        FloatComplex[] q = fft(even);

        // fft of odd terms
        FloatComplex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2 * k + 1];
        }
        FloatComplex[] r = fft(odd);

        // combine
        FloatComplex[] y = new FloatComplex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            FloatComplex wk = new FloatComplex((float)Math.cos(kth), (float)Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + n / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
}
