#include <jni.h>
#include <string>
#include <android/log.h>
#include "FFTPrincetonConverted.h"
#include "FFTColumbiaConverted.h"
#include "kiss-fft/_kiss_fft_guts.h"
#include "FFTRecursiveNeon.h"
#include "FFTIterativeNeon.h"

#define LOGTAG "FFTLIB"

struct objFFT objfft;
struct ParametersStruct parametersStruct;
kiss_fft_cfg fwd;

void jniEmpty(JNIEnv*, jobject) {
    return;
}

jdoubleArray jniParams(JNIEnv*, jobject, jdoubleArray arr) {
    return arr;
}

jdoubleArray jniVectorConversion(JNIEnv* env, jobject, jdoubleArray arr) {
    jdouble* elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
    (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    return arr;
}
jdoubleArray jniColumbia(JNIEnv* env, jobject obj, jdoubleArray arr, jdoubleArray cos, jdoubleArray sin) {
    jdouble* elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
    jdouble* sin_v = (jdouble*)(*env).GetPrimitiveArrayCritical(sin, 0);
    jdouble* cos_v = (jdouble*)(*env).GetPrimitiveArrayCritical(cos, 0);

    (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    (*env).ReleasePrimitiveArrayCritical(sin, sin_v, 0);
    (*env).ReleasePrimitiveArrayCritical(cos, cos_v, 0);
    return arr;
}

jlong fftPI(JNIEnv* env, jobject obj, jdoubleArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements;
    if (arrTest) {
        elements = (*env).GetDoubleArrayElements(arr, 0);
    } else {
        elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }

    std::vector<std::complex<double> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<double>(elements[i], elements[i+1]));
    }

    long ret = fftPrincetonIterative(x); // Run FFT
    if (ret == -1) {
        __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- Size not power of 2");
    }

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    if (arrTest) {
        (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return ret;
}

jdoubleArray fftPR(JNIEnv* env, jobject obj, jdoubleArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements;
    if (arrTest) {
        elements = (*env).GetDoubleArrayElements(arr, 0);
    } else {
        elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }

    std::vector<std::complex<double> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<double>(elements[i], elements[i+1]));
    }

    x = fftPrincetonRecursive(x);

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    if (arrTest) {
        (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

jfloatArray floatFftPI(JNIEnv* env, jobject obj, jfloatArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements;
    if (arrTest) {
        elements = (*env).GetFloatArrayElements(arr, 0);
    } else {
        elements = (jfloat*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }

    std::vector<std::complex<float> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<float>(elements[i], elements[i+1]));
    }

    int ret = floatFftPrincetonIterative(x); // Run FFT
    if (ret == -1) {
        __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- Size not power of 2");
    }

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    if (arrTest) {
        (*env).ReleaseFloatArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

jfloatArray floatFftPR(JNIEnv* env, jobject obj, jfloatArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements;
    if (arrTest) {
        elements = (*env).GetFloatArrayElements(arr, 0);
    } else {
        elements = (jfloat*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }

    std::vector<std::complex<float> > x;
    for (int i = 0; i < size; i+=2) {
        x.push_back(std::complex<float>(elements[i], elements[i+1]));
    }

    x = floatFftPrincetonRecursive(x);

    // place in return array
    // [x[0].real, x[0].imag, ... x[n-1].real, x[n-1].imag]
    for (int i = 0; i < size; i+=2) {
        elements[i] = x[i/2].real();
        elements[i + 1] = x[i/2].imag();
    }

    if (arrTest) {
        (*env).ReleaseFloatArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

jfloatArray floatFftColumbiaIterative(JNIEnv* env, jobject obj, jfloatArray arr, jfloatArray cos, jfloatArray sin, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements;
    jfloat* sin_v;
    jfloat* cos_v;

    if (arrTest) {
        elements = (*env).GetFloatArrayElements(arr, 0);
        sin_v = (*env).GetFloatArrayElements(sin, 0);
        cos_v = (*env).GetFloatArrayElements(cos, 0);
    } else {
        elements = (jfloat *)(*env).GetPrimitiveArrayCritical(arr, 0);
        sin_v = (jfloat *)(*env).GetPrimitiveArrayCritical(sin, 0);
        cos_v = (jfloat *)(*env).GetPrimitiveArrayCritical(cos, 0);
    }

    int N = size/2;

    floatFftCI(elements, elements+N, N, cos_v, sin_v);

    if (arrTest) {
        (*env).ReleaseFloatArrayElements(arr, elements, 0);
        (*env).ReleaseFloatArrayElements(sin, sin_v, 0);
        (*env).ReleaseFloatArrayElements(cos, cos_v, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
        (*env).ReleasePrimitiveArrayCritical(sin, sin_v, 0);
        (*env).ReleasePrimitiveArrayCritical(cos, cos_v, 0);
    }
    return arr;
}

jlong fftColumbiaIterative(JNIEnv* env, jobject obj, jdoubleArray arr, jdoubleArray cos, jdoubleArray sin, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements;
    jdouble* sin_v;
    jdouble* cos_v;

    if (arrTest) {
        elements = (*env).GetDoubleArrayElements(arr, 0);
        sin_v = (*env).GetDoubleArrayElements(sin, 0);
        cos_v = (*env).GetDoubleArrayElements(cos, 0);
    } else {
        elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
        sin_v = (jdouble*)(*env).GetPrimitiveArrayCritical(sin, 0);
        cos_v = (jdouble*)(*env).GetPrimitiveArrayCritical(cos, 0);
    }

    int N = size/2;

    long ret = fftCI(elements, elements+N, N, cos_v, sin_v);

    if (arrTest) {
        (*env).ReleaseDoubleArrayElements(arr, elements, 0);
        (*env).ReleaseDoubleArrayElements(sin, sin_v, 0);
        (*env).ReleaseDoubleArrayElements(cos, cos_v, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
        (*env).ReleasePrimitiveArrayCritical(sin, sin_v, 0);
        (*env).ReleasePrimitiveArrayCritical(cos, cos_v, 0);
    }
    return ret;
}

void fftKissInit(JNIEnv* env, jobject, jint half) {
    fwd = kiss_fft_alloc(half, 0, 0, 0);
}

jdoubleArray fftKiss(JNIEnv* env, jobject, jdoubleArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements;
    if (arrTest) {
        elements = (*env).GetDoubleArrayElements(arr, 0);
    } else {
        elements = (jdouble*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }

    int half = size/2;
    std::vector<kiss_fft_cpx> in(half);
    std::vector<kiss_fft_cpx> out(half);

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

    // Return a double[]
    if (arrTest) {
        (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

void fftKissDelete(JNIEnv* env, jobject) {
    kiss_fft_free(fwd);
}

jfloatArray runRecursiveNeon(JNIEnv* env, jobject obj, jfloatArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements;
    if (arrTest) {
        elements = (*env).GetFloatArrayElements(arr, 0);
    } else {
        elements = (jfloat*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }
    int N = size/2;

    cd* in = (cd*)malloc(N * sizeof(cd));
    cd* out = (cd*)malloc(N * sizeof(cd));

    int i;
    for(i = 0; i < N; ++i) {
        in[i] = cd(elements[i], 0);
        out[i] = cd(0, 0);
    }

    int stride = 1;

    fftRecursiveNeonInit(N);

    fftRecursiveNeon(in, out, (int)(log(stride)/log(2)), stride, N); // Run FFT

    for (i = 0; i < N; i+=2) {
        elements[i] = out[i].real();
        elements[i+1] = out[i].imag();
        elements[i+N] = out[i+1].real();
        elements[i+N+1] = out[i+1].imag();
    }

    free(in);
    free(out);

    if (arrTest) {
        (*env).ReleaseFloatArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

void initIterativeNeon(JNIEnv*, jobject, jint size) {
    fftIterativeNeonInit(&objfft, &parametersStruct, size);
}

jfloatArray runIterativeNeon(JNIEnv* env, jobject, jfloatArray arr, jint arrTest) {
    jsize size = (*env).GetArrayLength(arr);
    jfloat* elements;
    if (arrTest) {
        elements = (*env).GetFloatArrayElements(arr,0);
    } else {
        elements = (jfloat*)(*env).GetPrimitiveArrayCritical(arr, 0);
    }
    int N = size/2;

    float* sourceReal = (float*)malloc(N * sizeof(float*));
    float* sourceImag = (float*)malloc(N * sizeof(float*));

    int i;
    for (i = 0; i < N; ++i) {
        sourceReal[i] = elements[i];
        sourceImag[i] = elements[i+N];
    }

    fftIterativeNeon(&objfft, sourceReal, sourceImag, elements, elements+N);

    free(sourceReal);
    free(sourceImag);

    if (arrTest) {
        (*env).ReleaseFloatArrayElements(arr, elements, 0);
    } else {
        (*env).ReleasePrimitiveArrayCritical(arr, elements, 0);
    }
    return arr;
}

void deleteIterativeNeon(JNIEnv*, jobject) {
    fftTerminate(&objfft);
}

static JNINativeMethod s_methods[] {
        // JNI Tests
        {"jni_empty",                         "()V",        (void*)jniEmpty},
        {"jni_params",                        "([D)[D",     (void*)jniParams},
        {"jni_vector_conversion",             "([D)[D",     (void*)jniVectorConversion},
        {"jni_columbia",                      "([D[D[D)[D", (void*)jniColumbia},

        // Converted FFTs
        {"fft_princeton_iterative",           "([DI)J",     (void*)fftPI},
        {"fft_princeton_recursive",           "([DI)[D",     (void*)fftPR},
        {"fft_columbia_iterative",            "([D[D[DI)J", (void*)fftColumbiaIterative},
        // Kiss FFT
        {"fft_kiss_init",                     "(I)V",       (void*)fftKissInit},
        {"fft_kiss",                          "([DI)[D",    (void*)fftKiss},
        {"fft_kiss_delete",                   "()V",        (void*)fftKissDelete},

        // Recursive NEON
        {"fft_recursive_neon",                 "([FI)[F",   (void*)runRecursiveNeon},

        // Iterative NEON
        {"init_iterative_neon",               "(I)V",       (void*)initIterativeNeon},
        {"run_iterative_neon",                "([FI)[F",    (void*)runIterativeNeon},
        {"delete_iterative_neon",             "()V",        (void*)deleteIterativeNeon},

        // Converted float FFTs
        {"float_fft_princeton_iterative",     "([FI)[F",     (void*)floatFftPI},
        {"float_fft_princeton_recursive",     "([FI)[F",     (void*)floatFftPR},
        {"float_fft_columbia_iterative",      "([F[F[FI)[F", (void*)floatFftColumbiaIterative},
};

jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}
