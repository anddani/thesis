//
// Created by algo on 1/27/17.
//

#ifndef BENCHMARKAPP_FFT_LIB_H
#define BENCHMARKAPP_FFT_LIB_H

#include <jni.h>

jlong fft(JNIEnv* env, jobject);

static JNINativeMethod s_methods[] {
        {"fft", "()J", (void*)fft}
};

#endif //BENCHMARKAPP_FFT_LIB_H
