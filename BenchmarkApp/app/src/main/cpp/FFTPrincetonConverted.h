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
    int fftIterative(std::vector<std::complex<double> >& x);
    std::vector<std::complex<double> > fftRecursive(std::vector<std::complex<double> > &x);
    std::vector<std::complex<double> > ifftRecursive(std::vector<std::complex<double> > x);

private:
    uint32_t reverseInt(uint32_t num);
};

#endif //BENCHMARKAPP_FFTPRINCETONCONVERTED_H
