#define LOG_TAG "AndroidOpenCLUpdater"

#include "AndroidOpenCLCommmons.h"

extern "C" void
AndroidOpenCLUpdater_contextFallback(const char *errinfo, void const *private_info, size_t cb,
                                     void *user_data);
extern void (*p_contextFallback)(const char *, const void *, size_t,
                                 void *);