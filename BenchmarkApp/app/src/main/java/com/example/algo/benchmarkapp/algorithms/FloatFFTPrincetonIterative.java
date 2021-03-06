package com.example.algo.benchmarkapp.algorithms;

/******************************************************************************
 *  Compilation:  javac FFTPrincetonIterative.java
 *  Execution:    java FFTPrincetonIterative N
 *  Dependencies: Complex.java
 *
 *  Compute the FFT of a length N complex sequence in-place.
 *  Uses a non-recursive version of the Cooley-Tukey FFT.
 *  Runs in O(N log N) time.
 *
 *  Reference:  Algorithm 1.6.1 in Computational Frameworks for the
 *  Fast Fourier Transform by Charles Van Loan.
 *
 *
 *  Limitations
 *  -----------
 *   -  assumes N is a power of 2
 *
 *  
 ******************************************************************************/

public class FloatFFTPrincetonIterative {

    // compute the FFT of x[], assuming its length is a power of 2
    public static void fft(FloatComplex[] x) {

        // check that length is a power of 2
        int N = x.length;
        if (Integer.highestOneBit(N) != N) {
            throw new RuntimeException("N is not a power of 2");
        }

        // bit reversal permutation
        int shift = 1 + Integer.numberOfLeadingZeros(N);
        for (int k = 0; k < N; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                FloatComplex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int L = 2; L <= N; L = L+L) {
            for (int k = 0; k < L/2; k++) {
                float kth = (float)(-2 * k * Math.PI / L);
                FloatComplex w = new FloatComplex((float)Math.cos(kth), (float)Math.sin(kth));
                for (int j = 0; j < N/L; j++) {
                    FloatComplex tao = w.times(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].minus(tao);
                    x[j*L + k]       = x[j*L + k].plus(tao);
                }
            }
        }
    }
}
