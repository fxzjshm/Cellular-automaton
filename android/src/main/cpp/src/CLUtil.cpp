#include "CLUtil.h"

extern "C" {
void checkCLError(cl_int errcode) {
    if (errcode != CL_SUCCESS) {
        LOGE("OpenCL error [%d]", errcode);
        throw errcode;
    }
}

char* getPlatformInfo(cl_platform_id platformId, cl_platform_info info) {
    size_t len = 0;
    clGetPlatformInfo(platformId, info, 0, NULL, &len);
    char *str = (char *) alloca(sizeof(char) * len);
    clGetPlatformInfo(platformId, info, len, &str, NULL);
    return str;
}

char* getDeviceInfo(cl_device_id deviceId, cl_platform_info info) {
    size_t len = 0;
    clGetDeviceInfo(deviceId, info, 0, NULL, &len);
    char *str = (char *) alloca(sizeof(char) * len);
    clGetDeviceInfo(deviceId, info, len, &str, NULL);
    return str;
}
}