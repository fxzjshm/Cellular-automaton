#include <vulkan_wrapper.h>
#include <jni.h>
#include <stdexcept>
#include <vector>
#include <sstream>
#include <string>
#include <iostream>
#include "android_log_print.h"

// Used for validating return values of Vulkan API calls.
#define VK_CHECK_RESULT(f)                                                             \
{                                                                                      \
    VkResult res = (f);                                                                \
    if (res != VK_SUCCESS)                                                             \
    {                                                                                  \
        std::ostringstream oss;                                                        \
        oss << "Fatal : VkResult is " << res << " in " << __FILE__ << " at line " << __LINE__ << std::endl ; \
        std::string s = oss.str();                                                     \
        LOGE(s.c_str(), nullptr);                                                      \
        throw new std::runtime_error(s);                                               \
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