#include <jni.h>
#include <string>
#include <android/log.h>
#include "FFTPrincetonConverted.h"
#include "FFTColumbiaConverted.h"
#include "FFTColumbiaConvertedOptimized.h"
#include "kiss-fft/_kiss_fft_guts.h"

#define LOGTAG "FFTLIB"

jdoubleArray fftPrincetonIterative(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);

    std::vector<std::complex<double> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<double>(elements[i], elements[i+1]));
    }

    FFTPrincetonConverted fpc = FFTPrincetonConverted();
    int ret = fpc.fftIterative(x); // Run FFT
    if (ret == -1) {
        __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- Size not power of 2");
    }

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

jdoubleArray fftPrincetonRecursive(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);

    std::vector<std::complex<double> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<double>(elements[i], elements[i+1]));
    }

    FFTPrincetonConverted fpc = FFTPrincetonConverted();
    x = fpc.fftRecursive(x);

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

jdoubleArray fftColumbiaIterative(JNIEnv* env, jobject obj, jdoubleArray arr, jdoubleArray cos, jdoubleArray sin) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);
    jdouble* sin_v = (*env).GetDoubleArrayElements(sin, 0);
    jdouble* cos_v = (*env).GetDoubleArrayElements(cos, 0);
    int N = size/2;

//    FFTColumbiaConverted fcc = FFTColumbiaConverted(N);
//    fcc.fftIterative(elements, elements+N); // Run FFT
    fftCI(elements, elements+N, N, cos_v, sin_v);

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

jdoubleArray fftCIO(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);
    int N = size/2;

    fftColumbiaIterativeOptimized(elements, elements+N, N); // Run FFT

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

jfloatArray fftNeon(JNIEnv* env, jobject obj, jfloatArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements = (*env).GetFloatArrayElements(arr, 0);
//    jfloat* outElements = (*env).GetFloatArrayElements(outArr, 0);
    int N = size/2;
//    std::vector<double> out(size);

    cd* in = (cd*)malloc(N * sizeof(cd*));
    cd* out = (cd*)malloc(N * sizeof(cd*));
    int i;
    for(i = 0; i < N; ++i) {
        in[i] = cd(elements[i], 0);
        out[i] = cd(0, 0);
//        __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- (%f, %f)", in[i].real(), in[i].imag());
    }

    int stride = 1;

    fftColumbiaNeonInit(N);
    fftColumbiaNeon(in, out, (int)(log(stride)/log(2)), stride, N); // Run FFT

    for (i = 0; i < N; i+=2) {
        elements[i] = out[i].real();
        elements[i+1] = out[i].imag();
        elements[i+N] = out[i+1].real();
        elements[i+N+1] = out[i+1].imag();
    }

    free(in);
    free(out);

    (*env).ReleaseFloatArrayElements(arr, elements, 0);
//    (*env).ReleaseFloatArrayElements(outArr, outElements, 0);
//    return outArr;
    return arr;
}

jdoubleArray fftKiss(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);

    int half = size/2;
    std::vector<kiss_fft_cpx> in(half);
    std::vector<kiss_fft_cpx> out(half);

    // Allocate memory
    kiss_fft_cfg fwd = kiss_fft_alloc(half, 0, 0, 0);

    for (int i = 0; i < size; i+=2) {
        in[i/2].r = elements[i];
        in[i/2].i = elements[i+1];
        out[i/2].r = 0.0;
        out[i/2].i = 0.0;
    }

    kiss_fft(fwd, &in[0], &out[0]);

    for (int i = 0; i < size; i+=2) {
        elements[i] = out[i/2].r;
        elements[i+1] = out[i/2].i;
    }

    kiss_fft_free(fwd);

    // Return a double[]
    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

void jniEmpty(JNIEnv*, jobject) {
    return;
}

jdoubleArray jniParams(JNIEnv*, jobject, jdoubleArray arr) {
    return arr;
}

jdoubleArray jniVectorConversion(JNIEnv* env, jobject, jdoubleArray arr) {
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);
    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

static JNINativeMethod s_methods[] {
        {"fft_princeton_iterative",           "([D)[D",     (void*)fftPrincetonIterative},
        {"fft_princeton_recursive",           "([D)[D",     (void*)fftPrincetonRecursive},
        {"fft_columbia_iterative",            "([D[D[D)[D", (void*)fftColumbiaIterative},
        {"fft_columbia_iterative_optimized",  "([D)[D",     (void*)fftCIO},
        {"fft_columbia_neon",                 "([F)[F",     (void*)fftNeon},
        {"fft_kiss",                          "([D)[D",     (void*)fftKiss},
        {"jni_empty",                         "()V",        (void*)jniEmpty},
        {"jni_params",                        "([D)[D",     (void*)jniParams},
        {"jni_vector_conversion",             "([D)[D",     (void*)jniVectorConversion},
};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}
