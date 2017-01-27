#include <jni.h>
#include <string>
#include "fft-lib.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    std::string bla = "";
    JNIEnv *env = NULL;

    vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass cls = env->FindClass("com/example/algo/benchmarkapp/Benchmark");
    int len = sizeof(s_methods) / sizeof(s_methods[0]);
    env->RegisterNatives(cls, s_methods, len);
    return JNI_VERSION_1_6;
}

jlong fft(JNIEnv* env, jobject) {
    return 17;
}
