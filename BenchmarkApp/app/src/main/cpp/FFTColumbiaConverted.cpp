#include "FFTColumbiaConverted.h"
//#include <android/log.h>
#define LOGTAG "FFTLIB"

void fftCI(double* x, double* y, int n, double* cos_v, double* sin_v) {
     int m = (int)(log(n) / log(2));
    int i,j,k,n1,n2,a;
    double c,s,e,t1,t2;

    // Bit-reverse
    j = 0;
    n2 = n/2;
    for (i=1; i < n - 1; i++) {
        n1 = n2;
        while ( j >= n1 ) {
            j = j - n1;
            n1 >>= 1;
        }
        j = j + n1;

        if (i < j) {
            t1 = x[i];
            x[i] = x[j];
            x[j] = t1;
            t1 = y[i];
            y[i] = y[j];
            y[j] = t1;
        }
    }

    // FFT
    n1 = 0;
    n2 = 1;
    long long counter = 0L;

    for (i=0; i < m; i++) {
        n1 = n2;
        n2 = n2 + n2;
        a = 0;

        for (j=0; j < n1; j++) {
            c = cos_v[a];
            s = sin_v[a];
            a +=  1 << (m-i-1);

            for (k=j; k < n; k=k+n2) {
                t1 = c*x[k+n1] - s*y[k+n1];
                t2 = s*x[k+n1] + c*y[k+n1];
                x[k+n1] = x[k] - t1;
                y[k+n1] = y[k] - t2;
                x[k] = x[k] + t1;
                y[k] = y[k] + t2;
                counter++;
            }
        }
    }
//    __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- Num iters: %ld", counter);
}

//FFTColumbiaConverted::FFTColumbiaConverted(int n) {
////    this->n = n;
////    this->m = (int)(log(n) / log(2));
////
////    // precompute tables
////    cos_v = new double[n/2];
////    sin_v = new double[n/2];
////
////    for(int i=0; i<n/2; i++) {
////        cos_v[i] = cos(-2*M_PI*i/n);
////        sin_v[i] = sin(-2*M_PI*i/n);
////    }
////
////    makeWindow();
//}
//
//FFTColumbiaConverted::~FFTColumbiaConverted() {
////    delete[] cos_v;
////    delete[] sin_v;
////    delete[] window;
//}
//
//void FFTColumbiaConverted::makeWindow() {
//    window = new double[n];
//    for(int i = 0; i < n; i++)
//        window[i] = 0.42 - 0.5 * cos(2*M_PI*i/(n-1))
//                    + 0.08 * cos(4*M_PI*i/(n-1));
//}
//
//void FFTColumbiaConverted::fftIterative(double* x, double* y, int n, double* cos_v, double* sin_v) {
//    int m = (int)(log(n) / log(2));
//    int i,j,k,n1,n2,a;
//    double c,s,e,t1,t2;
//
//
//    // Bit-reverse
//    j = 0;
//    n2 = n/2;
//    for (i=1; i < n - 1; i++) {
//        n1 = n2;
//        while ( j >= n1 ) {
//            j = j - n1;
//            n1 = n1/2;
//        }
//        j = j + n1;
//
//        if (i < j) {
//            t1 = x[i];
//            x[i] = x[j];
//            x[j] = t1;
//            t1 = y[i];
//            y[i] = y[j];
//            y[j] = t1;
//        }
//    }
//
//    // FFT
//    n1 = 0;
//    n2 = 1;
//
//    for (i=0; i < m; i++) {
//        n1 = n2;
//        n2 = n2 + n2;
//        a = 0;
//
//        for (j=0; j < n1; j++) {
//            c = cos_v[a];
//            s = sin_v[a];
//            a +=  1 << (m-i-1);
//
//            for (k=j; k < n; k=k+n2) {
//                t1 = c*x[k+n1] - s*y[k+n1];
//                t2 = s*x[k+n1] + c*y[k+n1];
//                x[k+n1] = x[k] - t1;
//                y[k+n1] = y[k] - t2;
//                x[k] = x[k] + t1;
//                y[k] = y[k] + t2;
//            }
//        }
//    }
//}
