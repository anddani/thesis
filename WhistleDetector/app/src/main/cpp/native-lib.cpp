#include <jni.h>
#include <string>
#include <android/log.h>
#include "FFTColumbiaConvertedOptimized.h"

jdoubleArray fft(JNIEnv* env, jobject obj, jdoubleArray arr, jdoubleArray sin, jdoubleArray cos) {
    jsize size = (*env).GetArrayLength(arr);
    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);
    jdouble* sin_v = (*env).GetDoubleArrayElements(sin, 0);
    jdouble* cos_v = (*env).GetDoubleArrayElements(cos, 0);

    int N = size/2;

    fftColumbiaIterativeOptimized(elements, elements+N, N, sin_v, cos_v); // Run FFT

    (*env).ReleaseDoubleArrayElements(arr, elements, 0);
    return arr;
}

static JNINativeMethod s_methods[] {
        {"fft", "([D[D[D)[D", (void*)fft},
};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/whistledetector/SignalDetector");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}
