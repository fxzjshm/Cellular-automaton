package com.entermoor.cellular_automaton.desktop.opencl;

import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.updater.DesktopOpenCLUpdater;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.demo.opencl.InfoUtil.checkCLError;
import static org.lwjgl.demo.opencl.InfoUtil.getPlatformInfoStringUTF8;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_ALL;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_DEFAULT;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_NAME;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;
import static org.lwjgl.system.MemoryStack.stackPush;

public class DesktopOpenCLUpdaterGenerator {

    public static Set<DesktopOpenCLUpdater> generateOpenCLUpdater() {
        Set<DesktopOpenCLUpdater> updaters = ConcurrentHashMap.newKeySet();
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetPlatformIDs(null, pi));
            int platformCount = pi.get(0);
            if (platformCount == 0) {
                // throw new RuntimeException("No OpenCL platforms found.");
                System.err.println("[OpenCLUpdaterGenerator] No OpenCL platforms found.");
                return updaters;
            }

            PointerBuffer platforms = stack.mallocPointer(pi.get(0));
            checkCLError(clGetPlatformIDs(platforms, (IntBuffer) null));

            PointerBuffer ctxProps = stack.mallocPointer(7);
            ctxProps.put(0, CL_CONTEXT_PLATFORM).put(2, MemoryUtil.NULL);

            IntBuffer errcode_ret = stack.callocInt(1);

            AtomicInteger count = new AtomicInteger();
            for (int p = 0; p < platforms.capacity(); p++) {

                long platform = platforms.get(p);
                ctxProps.put(1, platform);
                try {
                    // CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

                    int cl_device_type = CL_DEVICE_TYPE_ALL;
                    if (getPlatformInfoStringUTF8(platform, CL_PLATFORM_NAME).contains("clvk")) {
                        // at 2020-12-19, using CL_DEVICE_TYPE_ALL will be given CL_DEVICE_NOT_FOUND on clvk
                        cl_device_type = CL_DEVICE_TYPE_DEFAULT;
                    }
                    // CL_DEVICE_TYPE_ALL-> CL_DEVICE_TYPE_DEFAULT for clvk
                    checkCLError(clGetDeviceIDs(platform, cl_device_type, null, pi));

                    PointerBuffer devices = stack.mallocPointer(pi.get(0));
                    checkCLError(clGetDeviceIDs(platform, cl_device_type, devices, (IntBuffer) null));

                    for (int d = 0; d < devices.capacity(); d++) {
                        long device = devices.get(d);

                        DesktopOpenCLUpdater updater = new DesktopOpenCLUpdater(platform, device);
                        // CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);
                        count.incrementAndGet();
                        CellularAutomaton.asyncExecutor.submit(() -> {
                            try {
                                updater.init();
                                updaters.add(updater);
                            } catch (Exception e) {
                                synchronized (System.err) {
                                    System.err.println("[OpenCLUpdaterGenerator] Cannot init device " + updater.deviceName
                                            + " on platform " + updater.platformName);
                                    e.printStackTrace(System.err);
                                    // updaters.remove(updater);
                                }
                            } finally {
                                count.decrementAndGet();
                            }
                            return null;
                        });
                    }
                } catch (RuntimeException e) {
                    // problematic platform (e.g. no device available)
                    System.err.println("[OpenCLUpdaterGenerator] cannot use platform : " + getPlatformInfoStringUTF8(platform, CL_PLATFORM_NAME));
                    e.printStackTrace(System.err);
                }
            }
            while (count.get() != 0) {
                Thread.yield();
            }
        }
        return updaters;
    }

}
