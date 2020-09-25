#include "CLUtil.h"
#include <dlfcn.h>

extern "C" {
char *getPlatformInfoString(cl_platform_id platformId, cl_platform_info info) {
    cl_int err = CL_SUCCESS;
    size_t len = 0;
    err = clGetPlatformInfo(platformId, info, 0, NULL, &len);
    checkCLError(err);
    char *str = (char *) calloc(len, sizeof(char));
    err = clGetPlatformInfo(platformId, info, len, str, NULL);
    checkCLError(err);
    return str;
}

char *getDeviceInfoString(cl_device_id deviceId, cl_device_info info) {
    cl_int err = CL_SUCCESS;
    size_t len = 0;
    err = clGetDeviceInfo(deviceId, info, 0, NULL, &len);
    checkCLError(err);
    char *str = (char *) calloc(len, sizeof(char));
    err = clGetDeviceInfo(deviceId, info, len, str, NULL);
    checkCLError(err);
    return str;
}

// These 2 pieces of code comes from https://blog.csdn.net/xlxxcc/article/details/51106721
// which seems written by xlxxcc, thanks!
jstring charTojstring(JNIEnv *env, const char *pat) {
    jclass strClass = (env)->FindClass("java/lang/String");
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte *) pat);
    jstring encoding = (env)->NewStringUTF("UTF-8");
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_AndroidLauncher_setenv(JNIEnv *env, jclass clazz,
                                                              jstring name, jstring value,
                                                              jboolean override) {
    setenv(jstringToChar(env, name), jstringToChar(env, value), override);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_entermoor_cellular_1automaton_AndroidLauncher__1_1dlopen(JNIEnv *env, jclass clazz,
                                                                  jstring filename) {
    return reinterpret_cast<jlong>(__dlopen(jstringToChar(env, filename), RTLD_LAZY));
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_entermoor_cellular_1automaton_AndroidLauncher_dlerr(JNIEnv *env, jclass clazz) {
    return charTojstring(env, dlerror());
}