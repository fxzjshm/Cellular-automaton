#include <jni.h>
#include "dlopen.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_entermoor_cellular_1automaton_android_opencl_AndroidOpenCLLoader_loadOpenCLLibrary(
        JNIEnv *env, jclass jclazz) {
    // TODO: implement loadOpenCLLibrary()
    ndk_init(env);
    ndk_dlopen("/vendor/lib64/egl/libGLES_!_mali.so", RTLD_NOW);
    return -1;
}