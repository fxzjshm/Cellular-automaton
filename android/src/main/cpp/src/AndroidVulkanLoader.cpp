#include "AndroidVulkanLoader.h"

// Reference: https://github.com/Erkaman/vulkan_minimal_compute/blob/master/src/main.cpp

VkInstance instance;
std::vector<const char *> enabledExtensions;
VkPhysicalDevice physicalDevice;

extern "C" JNIEXPORT jint JNICALL
Java_com_entermoor_cellular_1automaton_android_vulkan_AndroidVulkanLoader_loadVulkanLibrary0(
        JNIEnv *env, jclass clazz) {
// TODO: implement loadVulkanLibrary0()
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

        bool foundExtension = false;
        for (VkExtensionProperties prop : extensionProperties) {
            if (strcmp(VK_KHR_VARIABLE_POINTERS_EXTENSION_NAME, prop.extensionName) == 0) {
                foundExtension = true;
                break;
            }

        }

        if (!foundExtension) {
            return -1;
        }
        enabledExtensions.push_back(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        enabledExtensions.push_back(VK_KHR_STORAGE_BUFFER_STORAGE_CLASS_EXTENSION_NAME);

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
        createInfo.enabledLayerCount = 0;
        createInfo.ppEnabledLayerNames = NULL;
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
        physicalDevice = devices[0];

    } catch (...) {
        return -1;
    }
    return 0;

}