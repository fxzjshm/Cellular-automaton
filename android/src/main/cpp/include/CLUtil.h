#pragma once

#include "libopencl.h"
#include <jni.h>
#include "android_log_print.h"
#include <stdexcept>
#include <sstream>
#include <string>
#include <iostream>
#include <cstring>

#ifndef LOG_TAG
#define LOG_TAG "CLUtil"
#endif

#define checkCLError(errcode) \
{ \
    if (errcode != CL_SUCCESS) { \
        std::ostringstream oss; \
        oss << "OpenCL error [" << errcode << "] in " << __FILE__ << " at line " << __LINE__  << std::endl; \
        std::string s = oss.str(); \
        LOGE(s.c_str(), nullptr); \
        throw new std::runtime_error(s); \
    } \
}

extern "C" {
char *getPlatformInfoString(cl_platform_id platformId, cl_platform_info info);
char *getDeviceInfoString(cl_device_id deviceId, cl_platform_info info);
jstring charTojstring(JNIEnv *env, const char *pat);
char *jstringToChar(JNIEnv *env, jstring jstr);
}