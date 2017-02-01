#include "FFTPrincetonConverted.h"

FFTPrincetonConverted::FFTPrincetonConverted() {

}
FFTPrincetonConverted::~FFTPrincetonConverted() {

}
int FFTPrincetonConverted::fft(std::vector<std::complex<double> >& x) {
    int N = x.size();

    // N not power of 2
    if ((N & (N - 1)) != 0) {
        return -1;
    }

    int shift = 1 + __builtin_clz(N);
    for (unsigned int k = 0; k < N; k++) {
        int j = reverseInt(k) >> shift;
        if (j > k) {
            std::complex<double> temp = x[j];
            x[j] = x[k];
            x[k] = temp;
        }
    }

    // butterfly updates
    for (int L = 2; L <= N; L = L+L) {
        for (int k = 0; k < L/2; k++) {
            double kth = -2 * k * M_PI / L;
            std::complex<double> w = std::complex<double>(cos(kth), sin(kth));
            for (int j = 0; j < N/L; j++) {
                std::complex<double> tao = w * (x[j*L + k + L/2]);
                x[j*L + k + L/2] = x[j*L + k] - tao;
                x[j*L + k]       = x[j*L + k] + tao;
            }
        }
    }
    return 0;
}

uint32_t FFTPrincetonConverted::reverseInt(uint32_t x)
{
    x = ((x >> 1) & 0x55555555u) | ((x & 0x55555555u) << 1);
    x = ((x >> 2) & 0x33333333u) | ((x & 0x33333333u) << 2);
    x = ((x >> 4) & 0x0f0f0f0fu) | ((x & 0x0f0f0f0fu) << 4);
    x = ((x >> 8) & 0x00ff00ffu) | ((x & 0x00ff00ffu) << 8);
    x = ((x >> 16) & 0xffffu) | ((x & 0xffffu) << 16);
    return x;
}
