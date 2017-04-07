#ifndef BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#define BENCHMARKAPP_FFTPRINCETONCONVERTED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

long fftPrincetonIterative(std::vector<std::complex<double> >& x);
std::vector<std::complex<double> > fftPrincetonRecursive(std::vector<std::complex<double> > &x);

int floatFftPrincetonIterative(std::vector<std::complex<float> >& x);
std::vector<std::complex<float> > floatFftPrincetonRecursive(std::vector<std::complex<float> > &x);
uint32_t reverseInt(uint32_t x);

#endif //BENCHMARKAPP_FFTPRINCETONCONVERTED_H
