#pragma once

#include "libopencl.h"
#include <stdexcept>
#include "android_log_print.h"
#include <assert.h>

#ifndef LOG_TAG
#define LOG_TAG "CLUtil"
#endif

#define checkCLError(errcode) \
{ \
    if (errcode != CL_SUCCESS) { \
        LOGE("OpenCL error [%d] in %s at line %d\n", errcode, __FILE__, __LINE__); \
        assert(errcode == CL_SUCCESS); \
    } \
}

extern "C" {
char *getPlatformInfoString(cl_platform_id platformId, cl_platform_info info);
char *getDeviceInfoString(cl_device_id deviceId, cl_platform_info info);
}