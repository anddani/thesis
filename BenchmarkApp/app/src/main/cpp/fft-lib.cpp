#include <jni.h>
#include <string>
#include <complex>
#include <math.h>
#include <stdlib.h>
#include <android/log.h>

#define LOGTAG "FFTLIB"

double pi = 4 * atan(1.0);
typedef unsigned int uint;

jlong fftIterativeNative(JNIEnv* env, jobject obj, jdoubleArray arr) {
    jsize size = (*env).GetArrayLength(arr);
//    __android_log_print(ANDROID_LOG_INFO, LOGTAG, "-- Native - Size of array : %d", (int)size);

    jdouble* elements = (*env).GetDoubleArrayElements(arr, 0);

    return (jlong)17;
}

jlong fftRecursiveNative(JNIEnv* env, jobject obj, jdoubleArray arr) {

    return (jlong)17;
}

static JNINativeMethod s_methods[] {
        {"FFTCppIterative", "([D)J", (void*)fftIterativeNative},
        {"FFTCppRecursive", "([D)J", (void*)fftRecursiveNative}
};

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}
