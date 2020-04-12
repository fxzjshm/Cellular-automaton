#include <jni.h>
#include <cstdio>
#include "dlopen.h"

#include <android/log.h>

#define LOG_TAG "AndroidOpenCLLoader"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO , LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN , LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR , LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jint JNICALL
Java_com_entermoor_cellular_1automaton_android_opencl_AndroidOpenCLLoader_loadOpenCLLibrary(
        JNIEnv *env, jclass jclazz) {
    // TODO: implement loadOpenCLLibrary()
    ndk_init(env);
    void *handle;
    handle = ndk_dlopen("/vendor/lib64/egl/libGLES_1_mali.so", RTLD_NOW);
    if (!handle) {
        LOGE("%s", ndk_dlerror());
        return -1;
    }
    return 0;
}