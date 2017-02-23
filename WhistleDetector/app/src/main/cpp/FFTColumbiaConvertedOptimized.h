#ifndef WHISTLEDETECTOR_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#define WHISTLEDETECTOR_FFTCOLUMBIACONVERTEDOPTIMIZED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

void fftColumbiaIterativeOptimized(double* x, double* y, int n, double* sin_v, double* cos_v);

#endif //WHISTLEDETECTOR_FFTCOLUMBIACONVERTED_H
