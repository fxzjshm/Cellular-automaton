/*
 *   Stub libopencl that dlsyms into actual library based on environment variable
 *
 *   LIBOPENCL_SO_PATH      -- Path to opencl so that will be searched first
 *   LIBOPENCL_SO_PATH_2    -- Searched second
 *   LIBOPENCL_SO_PATH_3    -- Searched third
 *   LIBOPENCL_SO_PATH_4    -- Searched fourth
 *
 *   If none of these are set, default system paths will be considered
**/

#include "libopencl.h"


#if defined(__APPLE__) || defined(__MACOSX)
static const char *default_so_paths[] = {
  "libOpenCL.so"
  "/System/Library/Frameworks/OpenCL.framework/OpenCL"
};
#elif defined(__ANDROID__)
static const char *default_so_paths[] = {
  "/system/lib/libOpenCL.so",
  "/system/vendor/lib/libOpenCL.so",
  "/system/vendor/lib/egl/libGLES_mali.so",
  "/system/vendor/lib64/egl/libGLES_mali.so",
  "/system/vendor/lib64/egl/libGLES_1_mali.so", // MI 5C
  "/system/lib64/egl/libGLES_mali.so",
  "/system/vendor/lib/libPVROCL.so",
  "/data/data/org.pocl.libs/files/lib/libpocl.so",
  "libOpenCL.so"
};
#elif defined(_WIN32)
static const char *default_so_paths[] = {
  "OpenCL.dll"
};
#elif defined(__linux__)
static const char *default_so_paths[] = {
  "/usr/lib/libOpenCL.so",
  "/usr/local/lib/libOpenCL.so",
  "/usr/local/lib/libpocl.so",
  "/usr/lib64/libOpenCL.so",
  "/usr/lib32/libOpenCL.so",
  "libOpenCL.so"
};
#endif

static void *so_handle = NULL;


static int access_file(const char *filename)
{
  struct stat buffer;
  return (stat(filename, &buffer) == 0);
}

void* __dlopen(const char* __filename, int __flag){
#if defined(__ANDROID__)
  return ndk_dlopen(__filename,__flag);
#else
  return dlopen(__filename,__flag);
#endif
}

void* __dlsym(void* __handle, const char* __symbol){
#if defined(__ANDROID__)
  return ndk_dlsym(__handle, __symbol);
#else
  return dlsym(__handle, __symbol);
#endif
}

int __dlclose(void* __handle){
#if defined(__ANDROID__)
  return ndk_dlclose(__handle);
#else
  return dlclose(__handle);
#endif
}

static int open_libopencl_so()
{
  char *path = NULL, *str = NULL;
  int i;

  if((str=getenv("LIBOPENCL_SO_PATH")) && access_file(str)) {
    path = str;
  }
  else if((str=getenv("LIBOPENCL_SO_PATH_2")) && access_file(str)) {
    path = str;
  }
  else if((str=getenv("LIBOPENCL_SO_PATH_3")) && access_file(str)) {
    path = str;
  }
  else if((str=getenv("LIBOPENCL_SO_PATH_4")) && access_file(str)) {
    path = str;
  }

  if(path)
  {
    so_handle = __dlopen(path, RTLD_LAZY);
  }

  for(i=0; (!so_handle) && (i<(sizeof(default_so_paths) / sizeof(char*))); i++)
  {
    if(access_file(default_so_paths[i]))
    {
      path = (char *) default_so_paths[i];
      so_handle = __dlopen(path, RTLD_LAZY);
      if(so_handle)
      {
          return 0;
      }
    }
  }

  if(so_handle)
  {
    return 0;
  }
  else
  {
    return -1;
  }
}

void stubOpenclReset()
{
  if(so_handle)
    __dlclose(so_handle);

  so_handle = NULL;
}

cl_int
clGetPlatformIDs(cl_uint          num_entries,
                 cl_platform_id * platforms,
                 cl_uint *        num_platforms)
{
  f_clGetPlatformIDs func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetPlatformIDs) __dlsym(so_handle, "clGetPlatformIDs");

  if(func) {
    return func(num_entries, platforms, num_platforms);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clGetPlatformInfo(cl_platform_id   platform,
                  cl_platform_info param_name,
                  size_t           param_value_size,
                  void *           param_value,
                  size_t *         param_value_size_ret)
{
  f_clGetPlatformInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetPlatformInfo) __dlsym(so_handle, "clGetPlatformInfo");

  if(func) {
    return func(platform, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clGetDeviceIDs(cl_platform_id   platform,
               cl_device_type   device_type,
               cl_uint          num_entries,
               cl_device_id *   devices,
               cl_uint *        num_devices)
{
  f_clGetDeviceIDs func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetDeviceIDs) __dlsym(so_handle, "clGetDeviceIDs");

  if(func) {
    return func(platform, device_type, num_entries, devices, num_devices);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetDeviceInfo(cl_device_id    device,
                cl_device_info  param_name,
                size_t          param_value_size,
                void *          param_value,
                size_t *        param_value_size_ret)
{
  f_clGetDeviceInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetDeviceInfo) __dlsym(so_handle, "clGetDeviceInfo");

  if(func) {
    return func(device, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clCreateSubDevices(cl_device_id                         in_device,
                   const cl_device_partition_property * properties,
                   cl_uint                              num_devices,
                   cl_device_id *                       out_devices,
                   cl_uint *                            num_devices_ret)
{
  f_clCreateSubDevices func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateSubDevices) __dlsym(so_handle, "clCreateSubDevices");

  if(func) {
    return func(in_device, properties, num_devices, out_devices, num_devices_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clRetainDevice(cl_device_id device)
{
  f_clRetainDevice func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainDevice) __dlsym(so_handle, "clRetainDevice");

  if(func) {
    return func(device);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseDevice(cl_device_id device)
{
  f_clReleaseDevice func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseDevice) __dlsym(so_handle, "clReleaseDevice");

  if(func) {
    return func(device);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_context
clCreateContext(const cl_context_properties * properties,
                cl_uint                 num_devices,
                const cl_device_id *    devices,
                void (*pfn_notify)(const char *, const void *, size_t, void *),
                void *                  user_data,
                cl_int *                errcode_ret)
{
  f_clCreateContext func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateContext) __dlsym(so_handle, "clCreateContext");

  if(func) {
    return func(properties, num_devices, devices, pfn_notify, user_data, errcode_ret);
  } else {
    return NULL;
  }
}

cl_context
clCreateContextFromType(const cl_context_properties * properties,
                        cl_device_type          device_type,
                        void (*pfn_notify )(const char *, const void *, size_t, void *),
                        void *                  user_data,
                        cl_int *                errcode_ret)
{
  f_clCreateContextFromType func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateContextFromType) __dlsym(so_handle, "clCreateContextFromType");

  if(func) {
    return func(properties, device_type, pfn_notify, user_data, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainContext(cl_context context)
{
  f_clRetainContext func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainContext) __dlsym(so_handle, "clRetainContext");

  if(func) {
    return func(context);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseContext(cl_context context)
{
  f_clReleaseContext func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseContext) __dlsym(so_handle, "clReleaseContext");

  if(func) {
    return func(context);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetContextInfo(cl_context         context,
                 cl_context_info    param_name,
                 size_t             param_value_size,
                 void *             param_value,
                 size_t *           param_value_size_ret)
{
  f_clGetContextInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetContextInfo) __dlsym(so_handle, "clGetContextInfo");

  if(func) {
    return func(context, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_command_queue
clCreateCommandQueue(cl_context                     context,
                     cl_device_id                   device,
                     cl_command_queue_properties    properties,
                     cl_int *                       errcode_ret)
{
  f_clCreateCommandQueue func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateCommandQueue) __dlsym(so_handle, "clCreateCommandQueue");

  if(func) {
    return func(context, device, properties, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainCommandQueue(cl_command_queue command_queue)
{
  f_clRetainCommandQueue func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainCommandQueue) __dlsym(so_handle, "clRetainCommandQueue");

  if(func) {
    return func(command_queue);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseCommandQueue(cl_command_queue command_queue)
{
  f_clReleaseCommandQueue func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseCommandQueue) __dlsym(so_handle, "clReleaseCommandQueue");

  if(func) {
    return func(command_queue);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetCommandQueueInfo(cl_command_queue      command_queue,
                      cl_command_queue_info param_name,
                      size_t                param_value_size,
                      void *                param_value,
                      size_t *              param_value_size_ret)
{
  f_clGetCommandQueueInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetCommandQueueInfo) __dlsym(so_handle, "clGetCommandQueueInfo");

  if(func) {
    return func(command_queue, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_mem
clCreateBuffer(cl_context   context,
               cl_mem_flags flags,
               size_t       size,
               void *       host_ptr,
               cl_int *     errcode_ret)
{
  f_clCreateBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateBuffer) __dlsym(so_handle, "clCreateBuffer");

  if(func) {
    return func(context, flags, size, host_ptr, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateSubBuffer(cl_mem                   buffer,
                  cl_mem_flags             flags,
                  cl_buffer_create_type    buffer_create_type,
                  const void *             buffer_create_info,
                  cl_int *                 errcode_ret)
{
  f_clCreateSubBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateSubBuffer) __dlsym(so_handle, "clCreateSubBuffer");

  if(func) {
    return func(buffer, flags, buffer_create_type,
                buffer_create_info, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateImage(cl_context              context,
              cl_mem_flags            flags,
              const cl_image_format * image_format,
              const cl_image_desc *   image_desc,
              void *                  host_ptr,
              cl_int *                errcode_ret)
{
  f_clCreateImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateImage) __dlsym(so_handle, "clCreateImage");

  if(func) {
    return func(context, flags, image_format, image_desc,
                host_ptr, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainMemObject(cl_mem memobj)
{
  f_clRetainMemObject func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainMemObject) __dlsym(so_handle, "clRetainMemObject");

  if(func) {
    return func(memobj);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseMemObject(cl_mem memobj)
{
  f_clReleaseMemObject func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseMemObject) __dlsym(so_handle, "clReleaseMemObject");

  if(func) {
    return func(memobj);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetSupportedImageFormats(cl_context           context,
                           cl_mem_flags         flags,
                           cl_mem_object_type   image_type,
                           cl_uint              num_entries,
                           cl_image_format *    image_formats,
                           cl_uint *            num_image_formats)
{
  f_clGetSupportedImageFormats func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetSupportedImageFormats) __dlsym(so_handle, "clGetSupportedImageFormats");

  if(func) {
    return func(context, flags, image_type, num_entries,
                image_formats, num_image_formats);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetMemObjectInfo(cl_mem           memobj,
                   cl_mem_info      param_name,
                   size_t           param_value_size,
                   void *           param_value,
                   size_t *         param_value_size_ret)
{
  f_clGetMemObjectInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetMemObjectInfo) __dlsym(so_handle, "clGetMemObjectInfo");

  if(func) {
    return func(memobj, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetImageInfo(cl_mem           image,
               cl_image_info    param_name,
               size_t           param_value_size,
               void *           param_value,
               size_t *         param_value_size_ret)
{
  f_clGetImageInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetImageInfo) __dlsym(so_handle, "clGetImageInfo");

  if(func) {
    return func(image, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clSetMemObjectDestructorCallback(  cl_mem memobj,
                                   void (*pfn_notify)( cl_mem memobj, void* user_data),
                                   void * user_data )
{
  f_clSetMemObjectDestructorCallback func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clSetMemObjectDestructorCallback) __dlsym(so_handle, "clSetMemObjectDestructorCallback");

  if(func) {
    return func(memobj, pfn_notify, user_data);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_sampler
clCreateSampler(cl_context          context,
                cl_bool             normalized_coords,
                cl_addressing_mode  addressing_mode,
                cl_filter_mode      filter_mode,
                cl_int *            errcode_ret)
{
  f_clCreateSampler func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateSampler) __dlsym(so_handle, "clCreateSampler");

  if(func) {
    return func(context, normalized_coords, addressing_mode, filter_mode, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainSampler(cl_sampler sampler)
{
  f_clRetainSampler func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainSampler) __dlsym(so_handle, "clRetainSampler");

  if(func) {
    return func(sampler);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseSampler(cl_sampler sampler)
{
  f_clReleaseSampler func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseSampler) __dlsym(so_handle, "clReleaseSampler");

  if(func) {
    return func(sampler);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetSamplerInfo(cl_sampler         sampler,
                 cl_sampler_info    param_name,
                 size_t             param_value_size,
                 void *             param_value,
                 size_t *           param_value_size_ret)
{
  f_clGetSamplerInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetSamplerInfo) __dlsym(so_handle, "clGetSamplerInfo");

  if(func) {
    return func(sampler, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_program
clCreateProgramWithSource(cl_context        context,
                          cl_uint           count,
                          const char **     strings,
                          const size_t *    lengths,
                          cl_int *          errcode_ret)
{
  f_clCreateProgramWithSource func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateProgramWithSource) __dlsym(so_handle, "clCreateProgramWithSource");

  if(func) {
    return func(context, count, strings, lengths, errcode_ret);
  } else {
    return NULL;
  }
}



cl_program
clCreateProgramWithBinary(cl_context                     context,
                          cl_uint                        num_devices,
                          const cl_device_id *           device_list,
                          const size_t *                 lengths,
                          const unsigned char **         binaries,
                          cl_int *                       binary_status,
                          cl_int *                       errcode_ret)
{
  f_clCreateProgramWithBinary func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateProgramWithBinary) __dlsym(so_handle, "clCreateProgramWithBinary");

  if(func) {
    return func(context, num_devices, device_list, lengths, binaries, binary_status, errcode_ret);
  } else {
    return NULL;
  }
}

cl_program
clCreateProgramWithBuiltInKernels(cl_context            context,
                                  cl_uint               num_devices,
                                  const cl_device_id *  device_list,
                                  const char *          kernel_names,
                                  cl_int *              errcode_ret)
{
  f_clCreateProgramWithBuiltInKernels func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateProgramWithBuiltInKernels) __dlsym(so_handle, "clCreateProgramWithBuiltInKernels");

  if(func) {
    return func(context, num_devices, device_list, kernel_names, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainProgram(cl_program program)
{
  f_clRetainProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainProgram) __dlsym(so_handle, "clRetainProgram");

  if(func) {
    return func(program);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseProgram(cl_program program)
{
  f_clReleaseProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseProgram) __dlsym(so_handle, "clReleaseProgram");

  if(func) {
    return func(program);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clBuildProgram(cl_program           program,
               cl_uint              num_devices,
               const cl_device_id * device_list,
               const char *         options,
               void (*pfn_notify)(cl_program program, void * user_data),
               void *               user_data)
{
  f_clBuildProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clBuildProgram) __dlsym(so_handle, "clBuildProgram");

  if(func) {
    return func(program, num_devices, device_list, options, pfn_notify, user_data);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clCompileProgram(cl_program           program,
                 cl_uint              num_devices,
                 const cl_device_id * device_list,
                 const char *         options,
                 cl_uint              num_input_headers,
                 const cl_program *   input_headers,
                 const char **        header_include_names,
                 void (*pfn_notify)(cl_program program, void * user_data),
                 void *               user_data)
{
  f_clCompileProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCompileProgram) __dlsym(so_handle, "clCompileProgram");

  if(func) {
    return func(program, num_devices, device_list, options, num_input_headers, input_headers,
                header_include_names, pfn_notify, user_data);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_program
clLinkProgram(cl_context           context,
              cl_uint              num_devices,
              const cl_device_id * device_list,
              const char *         options,
              cl_uint              num_input_programs,
              const cl_program *   input_programs,
              void (*pfn_notify)(cl_program program, void * user_data),
              void *               user_data,
              cl_int *             errcode_ret)
{
  f_clLinkProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clLinkProgram) __dlsym(so_handle, "clLinkProgram");

  if(func) {
    return func(context, num_devices, device_list, options, num_input_programs,
                input_programs, pfn_notify, user_data, errcode_ret);
  } else {
    return NULL;
  }
}


cl_int
clUnloadPlatformCompiler(cl_platform_id platform)
{
  f_clUnloadPlatformCompiler func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clUnloadPlatformCompiler) __dlsym(so_handle, "clUnloadPlatformCompiler");

  if(func) {
    return func(platform);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetProgramInfo(cl_program         program,
                 cl_program_info    param_name,
                 size_t             param_value_size,
                 void *             param_value,
                 size_t *           param_value_size_ret)
{
  f_clGetProgramInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetProgramInfo) __dlsym(so_handle, "clGetProgramInfo");

  if(func) {
    return func(program, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetProgramBuildInfo(cl_program            program,
                      cl_device_id          device,
                      cl_program_build_info param_name,
                      size_t                param_value_size,
                      void *                param_value,
                      size_t *              param_value_size_ret)
{
  f_clGetProgramBuildInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetProgramBuildInfo) __dlsym(so_handle, "clGetProgramBuildInfo");

  if(func) {
    return func(program, device, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_kernel
clCreateKernel(cl_program      program,
               const char *    kernel_name,
               cl_int *        errcode_ret)
{
  f_clCreateKernel func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateKernel) __dlsym(so_handle, "clCreateKernel");

  if(func) {
    return func(program, kernel_name, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clCreateKernelsInProgram(cl_program     program,
                         cl_uint        num_kernels,
                         cl_kernel *    kernels,
                         cl_uint *      num_kernels_ret)
{
  f_clCreateKernelsInProgram func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateKernelsInProgram) __dlsym(so_handle, "clCreateKernelsInProgram");

  if(func) {
    return func(program, num_kernels, kernels, num_kernels_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clRetainKernel(cl_kernel    kernel)
{
  f_clRetainKernel func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainKernel) __dlsym(so_handle, "clRetainKernel");

  if(func) {
    return func(kernel);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseKernel(cl_kernel   kernel)
{
  f_clReleaseKernel func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseKernel) __dlsym(so_handle, "clReleaseKernel");

  if(func) {
    return func(kernel);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clSetKernelArg(cl_kernel    kernel,
               cl_uint      arg_index,
               size_t       arg_size,
               const void * arg_value)
{
  f_clSetKernelArg func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clSetKernelArg) __dlsym(so_handle, "clSetKernelArg");

  if(func) {
    return func(kernel, arg_index, arg_size, arg_value);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetKernelInfo(cl_kernel       kernel,
                cl_kernel_info  param_name,
                size_t          param_value_size,
                void *          param_value,
                size_t *        param_value_size_ret)
{
  f_clGetKernelInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetKernelInfo) __dlsym(so_handle, "clGetKernelInfo");

  if(func) {
    return func(kernel, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetKernelArgInfo(cl_kernel       kernel,
                   cl_uint         arg_indx,
                   cl_kernel_arg_info  param_name,
                   size_t          param_value_size,
                   void *          param_value,
                   size_t *        param_value_size_ret)
{
  f_clGetKernelArgInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetKernelArgInfo) __dlsym(so_handle, "clGetKernelArgInfo");

  if(func) {
    return func(kernel, arg_indx, param_name, param_value_size,
                param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetKernelWorkGroupInfo(cl_kernel                  kernel,
                         cl_device_id               device,
                         cl_kernel_work_group_info  param_name,
                         size_t                     param_value_size,
                         void *                     param_value,
                         size_t *                   param_value_size_ret)
{
  f_clGetKernelWorkGroupInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetKernelWorkGroupInfo) __dlsym(so_handle, "clGetKernelWorkGroupInfo");

  if(func) {
    return func(kernel, device, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clWaitForEvents(cl_uint             num_events,
                const cl_event *    event_list)
{
  f_clWaitForEvents func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clWaitForEvents) __dlsym(so_handle, "clWaitForEvents");

  if(func) {
    return func(num_events, event_list);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clGetEventInfo(cl_event         event,
               cl_event_info    param_name,
               size_t           param_value_size,
               void *           param_value,
               size_t *         param_value_size_ret)
{
  f_clGetEventInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetEventInfo) __dlsym(so_handle, "clGetEventInfo");

  if(func) {
    return func(event, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_event
clCreateUserEvent(cl_context    context,
                  cl_int *      errcode_ret)
{
  f_clCreateUserEvent func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateUserEvent) __dlsym(so_handle, "clCreateUserEvent");

  if(func) {
    return func(context, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clRetainEvent(cl_event event)
{
  f_clRetainEvent func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clRetainEvent) __dlsym(so_handle, "clRetainEvent");

  if(func) {
    return func(event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clReleaseEvent(cl_event event)
{
  f_clReleaseEvent func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clReleaseEvent) __dlsym(so_handle, "clReleaseEvent");

  if(func) {
    return func(event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clSetUserEventStatus(cl_event   event,
                     cl_int     execution_status)
{
  f_clSetUserEventStatus func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clSetUserEventStatus) __dlsym(so_handle, "clSetUserEventStatus");

  if(func) {
    return func(event, execution_status);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clSetEventCallback( cl_event    event,
                    cl_int      command_exec_callback_type,
                    void (*pfn_notify)(cl_event, cl_int, void *),
                    void *      user_data)
{
  f_clSetEventCallback func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clSetEventCallback) __dlsym(so_handle, "clSetEventCallback");

  if(func) {
    return func(event, command_exec_callback_type, pfn_notify, user_data);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetEventProfilingInfo(cl_event            event,
                        cl_profiling_info   param_name,
                        size_t              param_value_size,
                        void *              param_value,
                        size_t *            param_value_size_ret)
{
  f_clGetEventProfilingInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetEventProfilingInfo) __dlsym(so_handle, "clGetEventProfilingInfo");

  if(func) {
    return func(event, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clFlush(cl_command_queue command_queue)
{
  f_clFlush func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clFlush) __dlsym(so_handle, "clFlush");

  if(func) {
    return func(command_queue);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clFinish(cl_command_queue command_queue)
{
  f_clFinish func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clFinish) __dlsym(so_handle, "clFinish");

  if(func) {
    return func(command_queue);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clEnqueueReadBuffer(cl_command_queue    command_queue,
                    cl_mem              buffer,
                    cl_bool             blocking_read,
                    size_t              offset,
                    size_t              size,
                    void *              ptr,
                    cl_uint             num_events_in_wait_list,
                    const cl_event *    event_wait_list,
                    cl_event *          event)
{
  f_clEnqueueReadBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueReadBuffer) __dlsym(so_handle, "clEnqueueReadBuffer");

  if(func) {
    return func(command_queue, buffer, blocking_read, offset, size, ptr,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueReadBufferRect(cl_command_queue    command_queue,
                        cl_mem              buffer,
                        cl_bool             blocking_read,
                        const size_t *      buffer_offset,
                        const size_t *      host_offset,
                        const size_t *      region,
                        size_t              buffer_row_pitch,
                        size_t              buffer_slice_pitch,
                        size_t              host_row_pitch,
                        size_t              host_slice_pitch,
                        void *              ptr,
                        cl_uint             num_events_in_wait_list,
                        const cl_event *    event_wait_list,
                        cl_event *          event)
{
  f_clEnqueueReadBufferRect func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueReadBufferRect) __dlsym(so_handle, "clEnqueueReadBufferRect");

  if(func) {
    return func(command_queue, buffer, blocking_read, buffer_offset, host_offset, region,
                buffer_row_pitch, buffer_slice_pitch, host_row_pitch, host_slice_pitch, ptr,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueWriteBuffer(cl_command_queue   command_queue,
                     cl_mem             buffer,
                     cl_bool            blocking_write,
                     size_t             offset,
                     size_t             size,
                     const void *       ptr,
                     cl_uint            num_events_in_wait_list,
                     const cl_event *   event_wait_list,
                     cl_event *         event)
{
  f_clEnqueueWriteBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueWriteBuffer) __dlsym(so_handle, "clEnqueueWriteBuffer");

  if(func) {
    return func(command_queue, buffer, blocking_write, offset, size, ptr,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clEnqueueWriteBufferRect(cl_command_queue    command_queue,
                         cl_mem              buffer,
                         cl_bool             blocking_write,
                         const size_t *      buffer_offset,
                         const size_t *      host_offset,
                         const size_t *      region,
                         size_t              buffer_row_pitch,
                         size_t              buffer_slice_pitch,
                         size_t              host_row_pitch,
                         size_t              host_slice_pitch,
                         const void *        ptr,
                         cl_uint             num_events_in_wait_list,
                         const cl_event *    event_wait_list,
                         cl_event *          event)
{
  f_clEnqueueWriteBufferRect func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueWriteBufferRect) __dlsym(so_handle, "clEnqueueWriteBufferRect");

  if(func) {
    return func(command_queue, buffer, blocking_write, buffer_offset, host_offset, region,
                buffer_row_pitch, buffer_slice_pitch, host_row_pitch, host_slice_pitch,
                ptr, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clEnqueueFillBuffer(cl_command_queue   command_queue,
                    cl_mem             buffer,
                    const void *       pattern,
                    size_t             pattern_size,
                    size_t             offset,
                    size_t             size,
                    cl_uint            num_events_in_wait_list,
                    const cl_event *   event_wait_list,
                    cl_event *         event)
{
  f_clEnqueueFillBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueFillBuffer) __dlsym(so_handle, "clEnqueueFillBuffer");

  if(func) {
    return func(command_queue, buffer, pattern, pattern_size, offset, size,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueCopyBuffer(cl_command_queue    command_queue,
                    cl_mem              src_buffer,
                    cl_mem              dst_buffer,
                    size_t              src_offset,
                    size_t              dst_offset,
                    size_t              size,
                    cl_uint             num_events_in_wait_list,
                    const cl_event *    event_wait_list,
                    cl_event *          event)
{
  f_clEnqueueCopyBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueCopyBuffer) __dlsym(so_handle, "clEnqueueCopyBuffer");

  if(func) {
    return func(command_queue, src_buffer, dst_buffer, src_offset, dst_offset, size,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}



cl_int
clEnqueueCopyBufferRect(cl_command_queue    command_queue,
                        cl_mem              src_buffer,
                        cl_mem              dst_buffer,
                        const size_t *      src_origin,
                        const size_t *      dst_origin,
                        const size_t *      region,
                        size_t              src_row_pitch,
                        size_t              src_slice_pitch,
                        size_t              dst_row_pitch,
                        size_t              dst_slice_pitch,
                        cl_uint             num_events_in_wait_list,
                        const cl_event *    event_wait_list,
                        cl_event *          event)
{
  f_clEnqueueCopyBufferRect func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueCopyBufferRect) __dlsym(so_handle, "clEnqueueCopyBufferRect");

  if(func) {
    return func(command_queue, src_buffer, dst_buffer, src_origin, dst_origin, region, src_row_pitch,
                src_slice_pitch, dst_row_pitch, dst_slice_pitch, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueReadImage(cl_command_queue     command_queue,
                   cl_mem               image,
                   cl_bool              blocking_read,
                   const size_t *       origin,
                   const size_t *       region,
                   size_t               row_pitch,
                   size_t               slice_pitch,
                   void *               ptr,
                   cl_uint              num_events_in_wait_list,
                   const cl_event *     event_wait_list,
                   cl_event *           event)
{
  f_clEnqueueReadImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueReadImage) __dlsym(so_handle, "clEnqueueReadImage");

  if(func) {
    return func(command_queue, image, blocking_read, origin, region, row_pitch, slice_pitch,
                ptr, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueWriteImage(cl_command_queue    command_queue,
                    cl_mem              image,
                    cl_bool             blocking_write,
                    const size_t *      origin,
                    const size_t *      region,
                    size_t              input_row_pitch,
                    size_t              input_slice_pitch,
                    const void *        ptr,
                    cl_uint             num_events_in_wait_list,
                    const cl_event *    event_wait_list,
                    cl_event *          event)
{
  f_clEnqueueWriteImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueWriteImage) __dlsym(so_handle, "clEnqueueWriteImage");

  if(func) {
    return func(command_queue, image, blocking_write, origin, region, input_row_pitch, input_slice_pitch, ptr,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clEnqueueFillImage(cl_command_queue   command_queue,
                   cl_mem             image,
                   const void *       fill_color,
                   const size_t *     origin,
                   const size_t *     region,
                   cl_uint            num_events_in_wait_list,
                   const cl_event *   event_wait_list,
                   cl_event *         event)
{
  f_clEnqueueFillImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueFillImage) __dlsym(so_handle, "clEnqueueFillImage");

  if(func) {
    return func(command_queue, image, fill_color, origin, region, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueCopyImage(cl_command_queue     command_queue,
                   cl_mem               src_image,
                   cl_mem               dst_image,
                   const size_t *       src_origin,
                   const size_t *       dst_origin,
                   const size_t *       region,
                   cl_uint              num_events_in_wait_list,
                   const cl_event *     event_wait_list,
                   cl_event *           event)
{
  f_clEnqueueCopyImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueCopyImage) __dlsym(so_handle, "clEnqueueCopyImage");

  if(func) {
    return func(command_queue, src_image, dst_image, src_origin, dst_origin, region,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueCopyImageToBuffer(cl_command_queue command_queue,
                           cl_mem           src_image,
                           cl_mem           dst_buffer,
                           const size_t *   src_origin,
                           const size_t *   region,
                           size_t           dst_offset,
                           cl_uint          num_events_in_wait_list,
                           const cl_event * event_wait_list,
                           cl_event *       event)
{
  f_clEnqueueCopyImageToBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueCopyImageToBuffer) __dlsym(so_handle, "clEnqueueCopyImageToBuffer");

  if(func) {
    return func(command_queue, src_image, dst_buffer, src_origin, region, dst_offset,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_int
clEnqueueCopyBufferToImage(cl_command_queue command_queue,
                           cl_mem           src_buffer,
                           cl_mem           dst_image,
                           size_t           src_offset,
                           const size_t *   dst_origin,
                           const size_t *   region,
                           cl_uint          num_events_in_wait_list,
                           const cl_event * event_wait_list,
                           cl_event *       event)
{
  f_clEnqueueCopyBufferToImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueCopyBufferToImage) __dlsym(so_handle, "clEnqueueCopyBufferToImage");

  if(func) {
    return func(command_queue, src_buffer, dst_image, src_offset, dst_origin, region,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

void *
clEnqueueMapBuffer(cl_command_queue command_queue,
                   cl_mem           buffer,
                   cl_bool          blocking_map,
                   cl_map_flags     map_flags,
                   size_t           offset,
                   size_t           size,
                   cl_uint          num_events_in_wait_list,
                   const cl_event * event_wait_list,
                   cl_event *       event,
                   cl_int *         errcode_ret)
{
  f_clEnqueueMapBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueMapBuffer) __dlsym(so_handle, "clEnqueueMapBuffer");

  if(func) {
    return func(command_queue, buffer, blocking_map, map_flags, offset, size,
                num_events_in_wait_list, event_wait_list, event, errcode_ret);
  } else {
    return NULL;
  }
}

void *
clEnqueueMapImage(cl_command_queue  command_queue,
                  cl_mem            image,
                  cl_bool           blocking_map,
                  cl_map_flags      map_flags,
                  const size_t *    origin,
                  const size_t *    region,
                  size_t *          image_row_pitch,
                  size_t *          image_slice_pitch,
                  cl_uint           num_events_in_wait_list,
                  const cl_event *  event_wait_list,
                  cl_event *        event,
                  cl_int *          errcode_ret)
{
  f_clEnqueueMapImage func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueMapImage) __dlsym(so_handle, "clEnqueueMapImage");

  if(func) {
    return func(command_queue, image, blocking_map, map_flags, origin, region, image_row_pitch,
                image_slice_pitch, num_events_in_wait_list, event_wait_list, event, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clEnqueueUnmapMemObject(cl_command_queue command_queue,
                        cl_mem           memobj,
                        void *           mapped_ptr,
                        cl_uint          num_events_in_wait_list,
                        const cl_event *  event_wait_list,
                        cl_event *        event)
{
  f_clEnqueueUnmapMemObject func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueUnmapMemObject) __dlsym(so_handle, "clEnqueueUnmapMemObject");

  if(func) {
    return func(command_queue, memobj, mapped_ptr, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueMigrateMemObjects(cl_command_queue       command_queue,
                           cl_uint                num_mem_objects,
                           const cl_mem *         mem_objects,
                           cl_mem_migration_flags flags,
                           cl_uint                num_events_in_wait_list,
                           const cl_event *       event_wait_list,
                           cl_event *             event)
{
  f_clEnqueueMigrateMemObjects func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueMigrateMemObjects) __dlsym(so_handle, "clEnqueueMigrateMemObjects");

  if(func) {
    return func(command_queue, num_mem_objects, mem_objects, flags, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueNDRangeKernel(cl_command_queue command_queue,
                       cl_kernel        kernel,
                       cl_uint          work_dim,
                       const size_t *   global_work_offset,
                       const size_t *   global_work_size,
                       const size_t *   local_work_size,
                       cl_uint          num_events_in_wait_list,
                       const cl_event * event_wait_list,
                       cl_event *       event)
{
  f_clEnqueueNDRangeKernel func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueNDRangeKernel) __dlsym(so_handle, "clEnqueueNDRangeKernel");

  if(func) {
    return func(command_queue, kernel, work_dim, global_work_offset, global_work_size, local_work_size,
                num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueTask(cl_command_queue  command_queue,
              cl_kernel         kernel,
              cl_uint           num_events_in_wait_list,
              const cl_event *  event_wait_list,
              cl_event *        event)
{
  f_clEnqueueTask func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueTask) __dlsym(so_handle, "clEnqueueTask");

  if(func) {
    return func(command_queue, kernel, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueNativeKernel(cl_command_queue  command_queue,
                      void (*user_func)(void *),
                      void *            args,
                      size_t            cb_args,
                      cl_uint           num_mem_objects,
                      const cl_mem *    mem_list,
                      const void **     args_mem_loc,
                      cl_uint           num_events_in_wait_list,
                      const cl_event *  event_wait_list,
                      cl_event *        event)
{
  f_clEnqueueNativeKernel func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueNativeKernel) __dlsym(so_handle, "clEnqueueNativeKernel");

  if(func) {
    return func(command_queue, user_func, args, cb_args, num_mem_objects, mem_list,
                args_mem_loc, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueMarkerWithWaitList(cl_command_queue command_queue,
                            cl_uint           num_events_in_wait_list,
                            const cl_event *  event_wait_list,
                            cl_event *        event)
{
  f_clEnqueueMarkerWithWaitList func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueMarkerWithWaitList) __dlsym(so_handle, "clEnqueueMarkerWithWaitList");

  if(func) {
    return func(command_queue, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueBarrierWithWaitList(cl_command_queue command_queue,
                             cl_uint           num_events_in_wait_list,
                             const cl_event *  event_wait_list,
                             cl_event *        event)
{
  f_clEnqueueBarrierWithWaitList func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueBarrierWithWaitList) __dlsym(so_handle, "clEnqueueBarrierWithWaitList");

  if(func) {
    return func(command_queue, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

void *
clGetExtensionFunctionAddressForPlatform(cl_platform_id platform,
                                         const char *   func_name)
{
  f_clGetExtensionFunctionAddressForPlatform func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetExtensionFunctionAddressForPlatform) __dlsym(so_handle, "clGetExtensionFunctionAddressForPlatform");

  if(func) {
    return func(platform, func_name);
  } else {
    return NULL;
  }
}


cl_mem
clCreateImage2D(cl_context              context,
                cl_mem_flags            flags,
                const cl_image_format * image_format,
                size_t                  image_width,
                size_t                  image_height,
                size_t                  image_row_pitch,
                void *                  host_ptr,
                cl_int *                errcode_ret)
{
  f_clCreateImage2D func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateImage2D) __dlsym(so_handle, "clCreateImage2D");

  if(func) {
    return func(context, flags, image_format, image_width, image_height,
                image_row_pitch, host_ptr, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateImage3D(cl_context              context,
                cl_mem_flags            flags,
                const cl_image_format * image_format,
                size_t                  image_width,
                size_t                  image_height,
                size_t                  image_depth,
                size_t                  image_row_pitch,
                size_t                  image_slice_pitch,
                void *                  host_ptr,
                cl_int *                errcode_ret)
{
  f_clCreateImage3D func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateImage3D) __dlsym(so_handle, "clCreateImage3D");

  if(func) {
    return func(context, flags, image_format, image_width, image_height, image_depth,
                image_row_pitch, image_slice_pitch, host_ptr, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clEnqueueMarker(cl_command_queue    command_queue,
                cl_event *          event)
{
  f_clEnqueueMarker func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueMarker) __dlsym(so_handle, "clEnqueueMarker");

  if(func) {
    return func(command_queue, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueWaitForEvents(cl_command_queue command_queue,
                       cl_uint          num_events,
                       const cl_event * event_list)
{
  f_clEnqueueWaitForEvents func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueWaitForEvents) __dlsym(so_handle, "clEnqueueWaitForEvents");

  if(func) {
    return func(command_queue, num_events, event_list);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueBarrier(cl_command_queue command_queue)
{
  f_clEnqueueBarrier func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueBarrier) __dlsym(so_handle, "clEnqueueBarrier");

  if(func) {
    return func(command_queue);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clUnloadCompiler(void)
{
  f_clUnloadCompiler func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clUnloadCompiler) __dlsym(so_handle, "clUnloadCompiler");

  if(func) {
    return func();
  } else {
    return CL_INVALID_PLATFORM;
  }
}

void *
clGetExtensionFunctionAddress(const char * func_name)
{
  f_clGetExtensionFunctionAddress func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetExtensionFunctionAddress) __dlsym(so_handle, "clGetExtensionFunctionAddress");

  if(func) {
    return func(func_name);
  } else {
    return NULL;
  }
}


cl_mem
clCreateFromGLBuffer(cl_context     context,
                     cl_mem_flags   flags,
                     cl_GLuint      bufobj,
                     int *          errcode_ret)
{
  f_clCreateFromGLBuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateFromGLBuffer) __dlsym(so_handle, "clCreateFromGLBuffer");

  if(func) {
    return func(context, flags, bufobj, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateFromGLTexture(cl_context      context,
                      cl_mem_flags    flags,
                      cl_GLenum       target,
                      cl_GLint        miplevel,
                      cl_GLuint       texture,
                      cl_int *        errcode_ret)
{
  f_clCreateFromGLTexture func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateFromGLTexture) __dlsym(so_handle, "clCreateFromGLTexture");

  if(func) {
    return func(context, flags, target, miplevel, texture, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateFromGLRenderbuffer(cl_context   context,
                           cl_mem_flags flags,
                           cl_GLuint    renderbuffer,
                           cl_int *     errcode_ret)
{
  f_clCreateFromGLRenderbuffer func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateFromGLRenderbuffer) __dlsym(so_handle, "clCreateFromGLRenderbuffer");

  if(func) {
    return func(context, flags, renderbuffer, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clGetGLObjectInfo(cl_mem                memobj,
                  cl_gl_object_type *   gl_object_type,
                  cl_GLuint *           gl_object_name)
{
  f_clGetGLObjectInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetGLObjectInfo) __dlsym(so_handle, "clGetGLObjectInfo");

  if(func) {
    return func(memobj, gl_object_type, gl_object_name);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clGetGLTextureInfo(cl_mem               memobj,
                   cl_gl_texture_info   param_name,
                   size_t               param_value_size,
                   void *               param_value,
                   size_t *             param_value_size_ret)
{
  f_clGetGLTextureInfo func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetGLTextureInfo) __dlsym(so_handle, "clGetGLTextureInfo");

  if(func) {
    return func(memobj, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueAcquireGLObjects(cl_command_queue      command_queue,
                          cl_uint               num_objects,
                          const cl_mem *        mem_objects,
                          cl_uint               num_events_in_wait_list,
                          const cl_event *      event_wait_list,
                          cl_event *            event)
{
  f_clEnqueueAcquireGLObjects func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueAcquireGLObjects) __dlsym(so_handle, "clEnqueueAcquireGLObjects");

  if(func) {
    return func(command_queue, num_objects, mem_objects, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}

cl_int
clEnqueueReleaseGLObjects(cl_command_queue      command_queue,
                          cl_uint               num_objects,
                          const cl_mem *        mem_objects,
                          cl_uint               num_events_in_wait_list,
                          const cl_event *      event_wait_list,
                          cl_event *            event)
{
  f_clEnqueueReleaseGLObjects func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clEnqueueReleaseGLObjects) __dlsym(so_handle, "clEnqueueReleaseGLObjects");

  if(func) {
    return func(command_queue, num_objects, mem_objects, num_events_in_wait_list, event_wait_list, event);
  } else {
    return CL_INVALID_PLATFORM;
  }
}


cl_mem
clCreateFromGLTexture2D(cl_context      context,
                        cl_mem_flags    flags,
                        cl_GLenum       target,
                        cl_GLint        miplevel,
                        cl_GLuint       texture,
                        cl_int *        errcode_ret)
{
  f_clCreateFromGLTexture2D func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateFromGLTexture2D) __dlsym(so_handle, "clCreateFromGLTexture2D");

  if(func) {
    return func(context, flags, target, miplevel, texture, errcode_ret);
  } else {
    return NULL;
  }
}

cl_mem
clCreateFromGLTexture3D(cl_context      context,
                        cl_mem_flags    flags,
                        cl_GLenum       target,
                        cl_GLint        miplevel,
                        cl_GLuint       texture,
                        cl_int *        errcode_ret)
{
  f_clCreateFromGLTexture3D func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clCreateFromGLTexture3D) __dlsym(so_handle, "clCreateFromGLTexture3D");

  if(func) {
    return func(context, flags, target, miplevel, texture, errcode_ret);
  } else {
    return NULL;
  }
}

cl_int
clGetGLContextInfoKHR(const cl_context_properties * properties,
                      cl_gl_context_info            param_name,
                      size_t                        param_value_size,
                      void *                        param_value,
                      size_t *                      param_value_size_ret)
{
  f_clGetGLContextInfoKHR func = NULL;

  if(!so_handle)
    open_libopencl_so();

  if(so_handle)
    func = (f_clGetGLContextInfoKHR) __dlsym(so_handle, "clGetGLContextInfoKHR");

  if(func) {
    return func(properties, param_name, param_value_size, param_value, param_value_size_ret);
  } else {
    return CL_INVALID_PLATFORM;
  }
}