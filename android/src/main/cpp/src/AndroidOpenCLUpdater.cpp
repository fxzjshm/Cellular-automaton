#include "AndroidOpenCLUpdater.h"
#include <cstring>

static cl_int ret = 0;
cl_context clContext;
cl_command_queue clCommandQueue;
cl_program updaterProgram;
cl_kernel clKernel;
cl_mem oldMapMemory = NULL, mapMemory = NULL;
jint *oldMapCache = NULL, *mapCache = NULL;

void (*p_contextFallback)(const char *, const void *, size_t,
                          void *) =AndroidOpenCLUpdater_contextFallback;

extern "C" JNIEXPORT jstring JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_getPlatformName0(JNIEnv *env,
                                                                                     jobject thiz) {
    return charTojstring(env, getPlatformInfoString(platformId, CL_PLATFORM_NAME));
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_getDeviceName0(JNIEnv *env,
                                                                                   jobject thiz) {
    return charTojstring(env, getDeviceInfoString(deviceId, CL_DEVICE_NAME));
}

extern "C" void
AndroidOpenCLUpdater_contextFallback(const char *errinfo, void const *private_info, size_t cb,
                                     void *user_data) {

}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_createContext0(JNIEnv *env,
                                                                                   jobject thiz) {
    cl_context_properties ctxp[] = {CL_CONTEXT_PLATFORM, (cl_context_properties) platformId, 0};
    clContext = clCreateContext(ctxp, 1, &deviceId, p_contextFallback,/* user data */NULL, &ret);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_createCommandQueue0(JNIEnv *env,
                                                                                        jobject thiz) {
    clCommandQueue = clCreateCommandQueue(clContext, deviceId, NULL, &ret);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_createProgram0(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jstring source) {
    const char *src = jstringToChar(env, source);
    const char *srcArray[] = {src};
    const char **src2 = srcArray;
    size_t lengths[] = {(size_t) env->GetStringLength(source) + 1};
    updaterProgram = clCreateProgramWithSource(clContext, 1, src2, lengths, &ret);
    checkCLError(ret);
    ret = clBuildProgram(updaterProgram, 1, &deviceId, "", NULL, NULL);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_createKernel0(JNIEnv *env,
                                                                                  jobject thiz) {
    clKernel = clCreateKernel(updaterProgram, "update", &ret);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_createMemory0(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jintArray old_map) {
    jsize len = env->GetArrayLength(old_map);
    size_t byteCount = len * sizeof(jint);
    oldMapCache = (jint *) (malloc(byteCount));
    env->GetIntArrayRegion(old_map, 0, len, oldMapCache);
    oldMapMemory = clCreateBuffer(clContext, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, byteCount,
                                  oldMapCache, &ret);
    checkCLError(ret);
    mapMemory = clCreateBuffer(clContext, CL_MEM_READ_WRITE, byteCount, NULL, &ret);
    checkCLError(ret);
    mapCache = (jint *) (malloc(byteCount));
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_releaseMemory0(JNIEnv *env,
                                                                                   jobject thiz) {
    if (oldMapMemory != NULL) {
        ret = clReleaseMemObject(oldMapMemory);
        checkCLError(ret);
    }
    if (oldMapCache != NULL) {
        free(oldMapCache);
    }
    if (mapMemory != NULL) {
        ret = clReleaseMemObject(mapMemory);
        checkCLError(ret);
    }
    if (mapCache != NULL) {
        free(mapCache);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_writeToOldMap0(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jintArray old_map) {
    jsize len = env->GetArrayLength(old_map);
    env->GetIntArrayRegion(old_map, 0, len, oldMapCache);
    ret = clEnqueueWriteBuffer(clCommandQueue, oldMapMemory, CL_TRUE, 0, len * sizeof(jint),
                               oldMapCache, 0, NULL, NULL);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_callKernel0(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jint width,
                                                                                jint height) {
    const int dimensions = 1;
    size_t globalWorkSize[dimensions] = {(size_t) (width * height)};
    ret = clEnqueueNDRangeKernel(clCommandQueue, clKernel, dimensions, NULL, globalWorkSize, NULL,
                                 0, NULL, NULL);
    checkCLError(ret);
    ret = clFinish(clCommandQueue);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_setKernelArgs0(JNIEnv *env,
                                                                                   jobject thiz,
                                                                                   jint width,
                                                                                   jint height) {
    ret = clSetKernelArg(clKernel, 0, sizeof(cl_mem), &oldMapMemory);
    checkCLError(ret);
    ret = clSetKernelArg(clKernel, 1, sizeof(cl_mem), &mapMemory);
    checkCLError(ret);
    ret = clSetKernelArg(clKernel, 2, sizeof(jint), &width);
    checkCLError(ret);
    ret = clSetKernelArg(clKernel, 3, sizeof(jint), &height);
    checkCLError(ret);
}

extern "C" JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidOpenCLUpdater_readMap0(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jintArray new_map) {
    jsize len = env->GetArrayLength(new_map);
    ret = clEnqueueReadBuffer(clCommandQueue, mapMemory, CL_TRUE, 0,
                              env->GetArrayLength(new_map) * sizeof(jint), mapCache, 0, NULL, NULL);
    checkCLError(ret);
    env->SetIntArrayRegion(new_map,0,len,mapCache);
}