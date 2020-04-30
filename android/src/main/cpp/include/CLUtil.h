#include <stdexcept>

void checkCLError(cl_int errcode) {
    if (errcode != CL_SUCCESS) {
        LOGE("OpenCL error [%d]", errcode);
        throw errcode;
    }
}