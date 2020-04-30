#include <jni.h>
#include <cstdio>
#include "libopencl.h"

#define LOG_TAG "AndroidOpenCLLoader"

#include "android_log_print.h"
#include "AndroidOpenCLLoader.h"
#include "CLUtil.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_entermoor_cellular_1automaton_android_opencl_AndroidOpenCLLoader_loadOpenCLLibrary0(
        JNIEnv *env, jclass jclazz) {
    try {
        ndk_init(env);
        cl_int ret = 0;

        cl_uint platformCount = 0;
        ret = clGetPlatformIDs(0, 0, &platformCount);
        checkCLError(ret);

        cl_platform_id *platforms = new cl_platform_id[platformCount];
        ret = clGetPlatformIDs(platformCount, platforms, 0);
        checkCLError(ret);

        // Assume there is one device available on Android
        cl_uint selected_platform_index = platformCount;
        cl_device_id selected_device_id;
        cl_platform_id selected_platform_id;
        for (cl_uint i = 0; i < platformCount; ++i) {
            cl_platform_id platform = platforms[i];
            cl_uint numDevices = 0;
            cl_device_id *devices = NULL;
            ret = clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, 0, NULL, &numDevices);
            checkCLError(ret);
            if (numDevices > 0) //GPU available.
            {
                devices = (cl_device_id *) malloc(numDevices * sizeof(cl_device_id));
                ret = clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, numDevices, devices, NULL);
                checkCLError(ret);
                selected_device_id = devices[0];
                selected_platform_index = i;
                selected_platform_id = platform;
                break;
            }
        }
        if (selected_platform_index == platformCount) {
            LOGE("No usable platform found.");
            return -1;
        } else {
            platformId = selected_platform_id;
            deviceId = selected_device_id;
            return 0;
        }
    } catch (...) {
        return -1;
    }
}