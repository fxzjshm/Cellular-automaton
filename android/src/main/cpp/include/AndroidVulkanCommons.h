#include <vulkan_wrapper.h>
#include <jni.h>
#include <vector>
#include "android_log_print.h"

// Used for validating return values of Vulkan API calls.
#define VK_CHECK_RESULT(f)                                                             \
{                                                                                      \
    VkResult res = (f);                                                                \
    if (res != VK_SUCCESS)                                                             \
    {                                                                                  \
        LOGE("Fatal : VkResult is %d in %s at line %d\n", res,  __FILE__, __LINE__); \
        throw res;                                                  \
    }                                                                                  \
}

extern "C" {
extern VkInstance instance;

extern VkPhysicalDevice physicalDevice;
extern VkDevice device;

extern VkPipeline pipeline;
extern VkPipelineLayout pipelineLayout;

extern VkShaderModule computeShaderModule;

extern VkCommandPool commandPool;
extern VkCommandBuffer commandBuffer;

extern VkDescriptorPool descriptorPool;
extern VkDescriptorSet descriptorSet;
extern VkDescriptorSetLayout descriptorSetLayout;

extern VkBuffer buffer;
extern VkDeviceMemory bufferMemory;
extern uint32_t bufferSize;

extern std::vector<const char *> enabledLayers;
extern std::vector<const char *> enabledExtensions;

extern VkQueue queue; // a queue supporting compute operations.
extern uint32_t queueFamilyIndex;
}