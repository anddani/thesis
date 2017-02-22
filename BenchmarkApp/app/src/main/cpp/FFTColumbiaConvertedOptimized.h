#ifndef BENCHMARKAPP_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#define BENCHMARKAPP_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

void fftColumbiaIterativeOptimized(double* x, double* y, int n);

#endif //BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
