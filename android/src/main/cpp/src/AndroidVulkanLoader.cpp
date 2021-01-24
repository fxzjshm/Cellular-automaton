#include "AndroidVulkanLoader.h"

// Reference: https://github.com/Erkaman/vulkan_minimal_compute/blob/master/src/main.cpp

VkInstance instance;

VkPhysicalDevice physicalDevice;

std::vector<const char *> enabledLayers;
std::vector<const char *> enabledExtensions;

extern "C" {

inline bool checkVkApiVersion(VkPhysicalDevice vkPhysicalDevice, int requiredApiVersion) {
    VkPhysicalDeviceProperties physicalDeviceProperties;
    vkGetPhysicalDeviceProperties(vkPhysicalDevice, &physicalDeviceProperties);
    return physicalDeviceProperties.apiVersion > (requiredApiVersion);
}

inline bool checkVkExtension(VkPhysicalDevice vkPhysicalDevice, const char *extensionName) {
    uint32_t deviceExtensionCount;
    vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, NULL, &deviceExtensionCount, NULL);
    std::vector<VkExtensionProperties> deviceExtensionProperties(deviceExtensionCount);
    vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, NULL, &deviceExtensionCount,
                                         deviceExtensionProperties.data());
    for (VkExtensionProperties prop : deviceExtensionProperties) {
        if (strcmp(extensionName, prop.extensionName) == 0) {
            return true;
        }
    }
    return false;
}

}

extern "C" JNIEXPORT jint JNICALL
Java_com_entermoor_cellular_1automaton_android_vulkan_AndroidVulkanLoader_loadVulkanLibrary0(
        JNIEnv *env, jclass clazz) {
    try {
        int ret = InitVulkan();
        if (ret == 0) {
            return -1;
        }

        /* Check extensions required by clspv, that is VK_KHR_variable_pointers and VK_KHR_storage_buffer_storage_class
         * According to the document, check the former is enough
         * */

        uint32_t extensionCount;

        vkEnumerateInstanceExtensionProperties(NULL, &extensionCount, NULL);
        std::vector<VkExtensionProperties> extensionProperties(extensionCount);
        vkEnumerateInstanceExtensionProperties(NULL, &extensionCount, extensionProperties.data());

        bool found_VK_KHR_VARIABLE_POINTERS_EXTENSION = false;
        for (VkExtensionProperties prop : extensionProperties) {
            if (strcmp(VK_KHR_VARIABLE_POINTERS_EXTENSION_NAME, prop.extensionName) == 0) {
                found_VK_KHR_VARIABLE_POINTERS_EXTENSION = true;
                break;
            }
        }

        /*
        Next, we actually create the instance.
        */

        /*
        Contains application info. This is actually not that important.
        The only real important field is apiVersion.
        */
        VkApplicationInfo applicationInfo = {};
        applicationInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
        applicationInfo.pApplicationName = "CellularAutomaton";
        applicationInfo.applicationVersion = 0;
        applicationInfo.pEngineName = "CellularAutomaton_vulkan";
        applicationInfo.engineVersion = 0;
        applicationInfo.apiVersion = VK_API_VERSION_1_0;

        VkInstanceCreateInfo createInfo = {};
        createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
        createInfo.flags = 0;
        createInfo.pApplicationInfo = &applicationInfo;

        // Give our desired layers and extensions to vulkan.
        createInfo.enabledLayerCount = enabledLayers.size();
        createInfo.ppEnabledLayerNames = enabledLayers.data();
        createInfo.enabledExtensionCount = enabledExtensions.size();
        createInfo.ppEnabledExtensionNames = enabledExtensions.data();

        /*
        Actually create the instance.
        Having created the instance, we can actually start using vulkan.
        */
        VK_CHECK_RESULT(vkCreateInstance(&createInfo, NULL, &instance));



        /*
        So, first we will list all physical devices on the system with vkEnumeratePhysicalDevices .
        */
        uint32_t deviceCount;
        vkEnumeratePhysicalDevices(instance, &deviceCount, NULL);
        if (deviceCount == 0) {
            throw std::runtime_error("could not find a device with vulkan support");
        }

        std::vector<VkPhysicalDevice> devices(deviceCount);
        vkEnumeratePhysicalDevices(instance, &deviceCount, devices.data());

        for (VkPhysicalDevice vkPhysicalDevice:devices) {
            // VK_KHR_variable_pointers has been promoted to Vulkan 1.1
            // the extensions may be on the device instead of the instance
            if (checkVkApiVersion(vkPhysicalDevice, VK_MAKE_VERSION(1, 1, 0)) ||
                checkVkExtension(vkPhysicalDevice, VK_KHR_VARIABLE_POINTERS_EXTENSION_NAME)) {
                found_VK_KHR_VARIABLE_POINTERS_EXTENSION = true;
                physicalDevice = vkPhysicalDevice;
                break;
            }
        }

        if ((!found_VK_KHR_VARIABLE_POINTERS_EXTENSION) || (physicalDevice == NULL)) {
            return -1;
        }
    } catch (...) {
        return -1;
    }
    return 0;

}