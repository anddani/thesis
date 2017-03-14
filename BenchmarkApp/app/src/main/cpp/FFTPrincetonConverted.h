#ifndef BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#define BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

int fftPrincetonIterative(std::vector<std::complex<double> >& x);
std::vector<std::complex<double> > fftPrincetonRecursive(std::vector<std::complex<double> > &x);
uint32_t reverseInt(uint32_t x);

#endif //BENCHMARKAPP_FFTPRINCETONCONVERTED_H
