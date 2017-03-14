#include "FFTPrincetonConverted.h"

using namespace std;

int fftPrincetonIterative(vector<complex<double> >& x) {
    int N = x.size();

    // N not power of 2
    if ((N & (N - 1)) != 0) {
        return -1;
    }

    // Bit reversal permutation
    int shift = 1 + __builtin_clz(N);
    for (unsigned int k = 0; k < N; k++) {
        int j = reverseInt(k) >> shift;
        if (j > k) {
            complex<double> temp = x[j];
            x[j] = x[k];
            x[k] = temp;
        }
    }

    // butterfly updates
    for (int L = 2; L <= N; L = L+L) {
        for (int k = 0; k < L/2; k++) {
            double kth = -2 * k * M_PI / L;
            complex<double> w(cos(kth), sin(kth));
            for (int j = 0; j < N/L; j++) {
                complex<double> tao = w * (x[j*L + k + L/2]);
                x[j*L + k + L/2] = x[j*L + k] - tao;
                x[j*L + k]       = x[j*L + k] + tao;
            }
        }
    }
    return 0;
}

vector<complex<double> > fftPrincetonRecursive(vector<complex<double> > &x) {
    int n = x.size();
    if (n == 1) {
        vector<complex<double> > ret;
        ret.push_back(x[0]);
        return ret;
    }
    if (n % 2 != 0) {
        return vector<complex<double> >();
    }

    vector<complex<double> > even(n/2);
    for (int k = 0; k < n/2; ++k) {
        even[k] = x[2 * k];
    }
    vector<complex<double> > q = fftPrincetonRecursive(even);

    vector<complex<double> > odd(n/2);
    for (int k = 0; k < n/2; ++k) {
        odd[k] = x[2 * k + 1];
    }
    vector<complex<double> > r = fftPrincetonRecursive(odd);

    // Combine
    vector<complex<double> > y(n);
    for (int k = 0; k < n/2; ++k) {
        double kth = -2 * k * M_PI / n;
        complex<double> wk(cos(kth), sin(kth));
        y[k] = q[k] + (wk*r[k]);
        y[k + n/2] = q[k] - (wk*r[k]);
    }
    return y;
}

uint32_t reverseInt(uint32_t x)
{
    x = ((x >> 1) & 0x55555555u) | ((x & 0x55555555u) << 1);
    x = ((x >> 2) & 0x33333333u) | ((x & 0x33333333u) << 2);
    x = ((x >> 4) & 0x0f0f0f0fu) | ((x & 0x0f0f0f0fu) << 4);
    x = ((x >> 8) & 0x00ff00ffu) | ((x & 0x00ff00ffu) << 8);
    x = ((x >> 16) & 0xffffu) | ((x & 0xffffu) << 16);
    return x;
}
