#pragma once

#include <jni.h>
#include "libopencl.h"
#include "android_log_print.h"
#include "CLUtil.h"

extern "C" {
extern cl_platform_id platformId;
extern cl_device_id deviceId;
extern cl_context clContext;
extern cl_command_queue clCommandQueue;
extern cl_program updaterProgram;
extern cl_kernel clKernel;
extern cl_mem oldMapMemory, mapMemory;
}