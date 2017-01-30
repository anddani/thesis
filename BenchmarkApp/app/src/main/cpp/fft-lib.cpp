#include <jni.h>
#include <string>
#include <math.h>
//#include <fftw3.h>
#include <stdlib.h>
#include <android/log.h>
#include "fft-lib.h"

#define LOGTAG "FFTLIB"

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    std::string bla = "";
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}

inline float frand( void ) { return (float) rand() / RAND_MAX; }

jlong fft(JNIEnv* env, jobject obj, jint a) {
//    int dataSize = 10;
//    int init_exp = 17;
//    int n = (int) pow(2, init_exp);
//    fftwf_complex *in = (fftwf_complex*) fftwf_malloc(sizeof(fftwf_complex) * dataSize);
//    fftwf_plan plan;
//
//    // Initialize input for fft
//    for (int i = 0; i < n; i++) {
//        in[i][0] = frand();
//        in[i][1] = frand();
//    }
//
//    __android_log_print(ANDROID_LOG_VERBOSE, LOGTAG, "-- Forward FFT 1d 2^%d", n);
//
//    plan = fftwf_plan_dft_1d(n, in, in, FFTW_FORWARD, FFTW_ESTIMATE);
//    fftwf_execute(plan);
//    fftwf_destroy_plan(plan);
//    fftwf_free(in);
//    __android_log_print(ANDROID_LOG_VERBOSE, LOGTAG, "-- Done with FFT");


    return (jlong)a;
}
