#ifndef BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#define BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

class FFTPrincetonConverted {
public:
    FFTPrincetonConverted();
    ~FFTPrincetonConverted();
    int fft(std::vector<std::complex<double> >& x);

private:
    int leadingZeros(int num);
    uint32_t reverseInt(uint32_t num);
    void swap(std::complex<double>& a, std::complex<double>& b);

};

#endif //BENCHMARKAPP_FFTPRINCETONCONVERTED_H
