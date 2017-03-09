#ifndef BENCHMARKAPP_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#define BENCHMARKAPP_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#include <vector>
#include <complex>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif
typedef std::complex<float> cd;

void fftColumbiaIterativeOptimized(double* x, double* y, int n);
void fftColumbiaNeonInit(int N);
void fftColumbiaNeon(cd *in, cd* out, int log2stride, int stride, int N);


#endif //BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
