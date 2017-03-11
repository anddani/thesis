#ifndef BENCHMARKAPP_FFTRECURSIVENEON_H
#define BENCHMARKAPP_FFTRECURSIVENEON_H
#include <vector>
#include <complex>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif
typedef std::complex<float> cd;

void fftRecursiveNeonInit(int N);
void fftRecursiveNeon(cd *in, cd* out, int log2stride, int stride, int N);


#endif //BENCHMARKAPP_FFTRECURSIVENEON_H
