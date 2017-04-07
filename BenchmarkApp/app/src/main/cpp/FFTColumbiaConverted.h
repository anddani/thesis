#ifndef BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
#define BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

long fftCI(double* x, double* y, int n, double* cos_v, double* sin_v);
void floatFftCI(float* x, float* y, int n, float* cos_v, float* sin_v);
uint32_t reverseIntc(uint32_t x);

#endif //BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
