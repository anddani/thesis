#include "FFTColumbiaConverted.h"
#define LOGTAG "FFTLIB"
#include "android/log.h"



long fftCI(double* x, double* y, int n, double* cos_v, double* sin_v) {
     int m = (int)(log(n) / log(2));
    int i,j,k,n1,n2,a;
    double c,s,e,t1,t2;


    // Bit-reverse
    j = 0;
    n2 = n/2;
    int shift = 1 + __builtin_clz(n);
    for (unsigned int k = 0; k < n; k++) {
        int j = reverseIntc(k) >> shift;
        if (j > k) {
            double temp = x[j];
            x[j] = x[k];
            x[k] = temp;
        }
    }

    // FFT
    n1 = 0;
    n2 = 1;
    long long counter = 0L;

    clock_t time1 = clock();
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


    double total_time = double(clock() - time1);
    return (long)total_time;
}

void floatFftCI(float* x, float* y, int n, float* cos_v, float* sin_v) {
    int m = (int)(log(n) / log(2));
    int i,j,k,n1,n2,a;
    float c,s,e,t1,t2;

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
}

uint32_t reverseIntc(uint32_t x)
{
    x = ((x >> 1) & 0x55555555u) | ((x & 0x55555555u) << 1);
    x = ((x >> 2) & 0x33333333u) | ((x & 0x33333333u) << 2);
    x = ((x >> 4) & 0x0f0f0f0fu) | ((x & 0x0f0f0f0fu) << 4);
    x = ((x >> 8) & 0x00ff00ffu) | ((x & 0x00ff00ffu) << 8);
    x = ((x >> 16) & 0xffffu) | ((x & 0xffffu) << 16);
    return x;
}
