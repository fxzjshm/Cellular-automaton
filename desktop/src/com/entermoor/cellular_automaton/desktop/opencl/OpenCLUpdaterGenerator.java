package com.entermoor.cellular_automaton.desktop.opencl;

import com.badlogic.gdx.Gdx;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.updater.OpenCLUpdater;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.lwjgl.demo.opencl.InfoUtil.checkCLError;
import static org.lwjgl.demo.opencl.InfoUtil.getDeviceInfoStringUTF8;
import static org.lwjgl.demo.opencl.InfoUtil.getPlatformInfoStringUTF8;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_DEVICE_NAME;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_ALL;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_NAME;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;
import static org.lwjgl.system.MemoryStack.stackPush;

public class OpenCLUpdaterGenerator {

    public static Set<OpenCLUpdater> generateOpenCLUpdater(CellularAutomaton main) {
        Set<OpenCLUpdater> updaters;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            checkCLError(clGetPlatformIDs(null, pi));
            int platformCount = pi.get(0);
            updaters = new LinkedHashSet<>(platformCount * 2);
            if (platformCount == 0) {
                // throw new RuntimeException("No OpenCL platforms found.");
                Gdx.app.log(OpenCLUpdaterGenerator.class.getSimpleName(), "No OpenCL platforms found.");
                return updaters;
            }

            PointerBuffer platforms = stack.mallocPointer(pi.get(0));
            checkCLError(clGetPlatformIDs(platforms, (IntBuffer) null));

            PointerBuffer ctxProps = stack.mallocPointer(7);
            ctxProps.put(0, CL_CONTEXT_PLATFORM).put(2, MemoryUtil.NULL);

            IntBuffer errcode_ret = stack.callocInt(1);
            for (int p = 0; p < platforms.capacity(); p++) {

                long platform = platforms.get(p);
                ctxProps.put(1, platform);
                try {
                    // CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

                    checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, null, pi));

                    PointerBuffer devices = stack.mallocPointer(pi.get(0));
                    checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, devices, (IntBuffer) null));

                    for (int d = 0; d < devices.capacity(); d++) {
                        long device = devices.get(d);

                        // CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);
                        try {
                            OpenCLUpdater updater = new OpenCLUpdater(main, platform, device);
                            updater.init();
                            updaters.add(updater);
                        } catch (Exception e) {
                            System.err.println("[OpenCLUpdaterGenerator] Cannot init device " + getDeviceInfoStringUTF8(device,CL_DEVICE_NAME)
                                    + " on platform " + getPlatformInfoStringUTF8(platform,CL_PLATFORM_NAME));
                            e.printStackTrace(System.err);
                        }
                    }
                } catch (RuntimeException e) {
                    // problematic platform (e.g. no device available)
                    System.err.println("[OpenCLUpdaterGenerator] Problematic platform : " + getPlatformInfoStringUTF8(platform,CL_PLATFORM_NAME));
                    e.printStackTrace(System.err);
                }
            }
        }
        return updaters;
    }

}
