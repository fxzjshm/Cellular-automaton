#pragma once

#include "libopencl.h"
#include <stdexcept>
#include "android_log_print.h"

#ifndef LOG_TAG
#define LOG_TAG "CLUtil"
#endif

extern "C" {
void checkCLError(cl_int errcode);

char* getPlatformInfoString(cl_platform_id platformId, cl_platform_info info);
char* getDeviceInfoString(cl_device_id deviceId, cl_platform_info info);
}