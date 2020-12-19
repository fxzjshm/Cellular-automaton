#include "AndroidVulkanUpdater.h"

VkDevice device;

VkPipeline pipeline;
VkPipelineLayout pipelineLayout;

VkShaderModule computeShaderModule;

VkCommandPool commandPool;
VkCommandBuffer commandBuffer;

VkDescriptorPool descriptorPool;
VkDescriptorSet descriptorSet;
VkDescriptorSetLayout descriptorSetLayout;

std::vector<const char *> enabledLayers;

VkQueue queue;
uint32_t queueFamilyIndex;

// Returns the index of a queue family that supports compute operations.
uint32_t getComputeQueueFamilyIndex() {
    uint32_t queueFamilyCount;

    vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, &queueFamilyCount, NULL);

    // Retrieve all queue families.
    std::vector<VkQueueFamilyProperties> queueFamilies(queueFamilyCount);
    vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, &queueFamilyCount,
                                             queueFamilies.data());

    // Now find a family that supports compute.
    uint32_t i = 0;
    for (; i < queueFamilies.size(); ++i) {
        VkQueueFamilyProperties props = queueFamilies[i];

        if (props.queueCount > 0 && (props.queueFlags & VK_QUEUE_COMPUTE_BIT)) {
            // found a queue with compute. We're done!
            break;
        }
    }

    if (i == queueFamilies.size()) {
        throw std::runtime_error("could not find a queue family that supports operations");
    }

    return i;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createDevice0(JNIEnv *env,
                                                                                  jobject thiz) {
    /*
        We create the logical device in this function.
        */

    /*
    When creating the device, we also specify what queues it has.
    */
    VkDeviceQueueCreateInfo queueCreateInfo = {};
    queueCreateInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
    queueFamilyIndex = getComputeQueueFamilyIndex(); // find queue family with compute capability.
    queueCreateInfo.queueFamilyIndex = queueFamilyIndex;
    queueCreateInfo.queueCount = 1; // create one queue in this family. We don't need more.
    float queuePriorities = 1.0;  // we only have one queue, so this is not that imporant.
    queueCreateInfo.pQueuePriorities = &queuePriorities;

    /*
    Now we create the logical device. The logical device allows us to interact with the physical
    device.
    */
    VkDeviceCreateInfo deviceCreateInfo = {};

    // Specify any desired device features here. We do not need any for this application, though.
    VkPhysicalDeviceFeatures deviceFeatures = {};

    deviceCreateInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
    deviceCreateInfo.enabledLayerCount = enabledLayers.size();  // need to specify validation layers here as well.
    deviceCreateInfo.ppEnabledLayerNames = enabledLayers.data();
    deviceCreateInfo.pQueueCreateInfos = &queueCreateInfo; // when creating the logical device, we also specify what queues it has.
    deviceCreateInfo.queueCreateInfoCount = 1;
    deviceCreateInfo.pEnabledFeatures = &deviceFeatures;

    VK_CHECK_RESULT(vkCreateDevice(physicalDevice, &deviceCreateInfo, NULL, &device)); // create logical device.

    // Get a handle to the only member of the queue family.
    vkGetDeviceQueue(device, queueFamilyIndex, 0, &queue);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createBuffer0(JNIEnv *env,
                                                                                  jobject thiz) {
    // TODO: implement createBuffer0()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createDescriptorSetLayout0(
        JNIEnv *env, jobject thiz) {
    // TODO: implement createDescriptorSetLayout0()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createDescriptorSet0(
        JNIEnv *env, jobject thiz) {
    // TODO: implement createDescriptorSet0()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createComputePipeline0(
        JNIEnv *env, jobject thiz) {
    // TODO: implement createComputePipeline0()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_entermoor_cellular_1automaton_updater_AndroidVulkanUpdater_createCommandBuffer0(
        JNIEnv *env, jobject thiz) {
    // TODO: implement createCommandBuffer0()
}