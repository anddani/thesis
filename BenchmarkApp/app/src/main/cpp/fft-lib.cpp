#include <jni.h>
#include <string>
#include <android/log.h>
#include "FFTPrincetonConverted.h"
#include "FFTColumbiaConverted.h"
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

jdoubleArray fftColumbiaIterative(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);
    int N = size/2;

    FFTColumbiaConverted fcc = FFTColumbiaConverted(N);
    fcc.fftIterative(elements, elements+N); // Run FFT

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
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
        {"fft_princeton_iterative", "([D)[D", (void*)fftPrincetonIterative},
        {"fft_princeton_recursive", "([D)[D", (void*)fftPrincetonRecursive},
        {"fft_columbia_iterative",  "([D)[D", (void*)fftColumbiaIterative},
        {"fft_kiss",                "([D)[D", (void*)fftKiss},
        {"jni_empty",               "()V",    (void*)jniEmpty},
        {"jni_params",              "([D)[D", (void*)jniParams},
        {"jni_vector_conversion",   "([D)[D", (void*)jniVectorConversion},
};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}
