#ifndef BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
#define BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
#include <complex>
#include <vector>
#include <math.h>

#ifndef M_PI
const double M_PI = 3.14159265358979323846;
#endif

void fftCI(double* x, double* y, int n, double* cos_v, double* sin_v);

class FFTColumbiaConverted {
public:
//    void fftIterative(double* x, double* y);
    void fftIterative(double* x, double* y, int n, double* cos_v, double* sin_v);
    FFTColumbiaConverted(int n);
    ~FFTColumbiaConverted();
private:
    int n;
    int m;
    double* cos_v;
    double* sin_v;
    double* window;

    void makeWindow();
};

#endif //BENCHMARKAPP_FFTCOLUMBIACONVERTED_H
