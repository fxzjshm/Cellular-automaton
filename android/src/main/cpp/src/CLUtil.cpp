#include "CLUtil.h"

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
}