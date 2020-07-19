package com.entermoor.cellular_automaton.updater;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLContextCallback;

import java.nio.IntBuffer;
import java.util.Locale;

import static org.lwjgl.demo.opencl.InfoUtil.checkCLError;
import static org.lwjgl.demo.opencl.InfoUtil.getDeviceInfoStringUTF8;
import static org.lwjgl.demo.opencl.InfoUtil.getPlatformInfoStringUTF8;
import static org.lwjgl.demo.opencl.InfoUtil.getProgramBuildInfoStringASCII;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_DEVICE_NAME;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_NAME;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL10.clEnqueueReadBuffer;
import static org.lwjgl.opencl.CL10.clSetKernelArg1i;
import static org.lwjgl.opencl.CL10.clSetKernelArg1p;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class DesktopOpenCLUpdater extends OpenCLUpdater {
    public long clPlatform, clDevice;
    // public CLCapabilities platformCapabilities, deviceCapabilities;
    public IntBuffer errcode_ret = BufferUtils.createIntBuffer(1);
    ;

    public long clContext;
    public CLContextCallback clContextCB;

    public long clQueue;

    public long updaterProgram;
    public long clKernel;
    public long oldMapMemory = -1, mapMemory = -1;
    public IntBuffer intBuffer;

    public int[] lastNewMap;

    public DesktopOpenCLUpdater(long platform, long device/*,CLCapabilities platformCaps,CLCapabilities deviceCaps*/) {
        clPlatform = platform;
        clDevice = device;
        platformName = getPlatformInfoStringUTF8(platform, CL_PLATFORM_NAME);
        deviceName = getDeviceInfoStringUTF8(device, CL_DEVICE_NAME);
        updaterName = String.format(Locale.getDefault(), "OpenCLUpdater(%s on %s)", deviceName, platformName);
    }

    @Override
    public void updateCellPool(int width, int height, int[] oldMap, int[] newMap) {
        while (preparing) {
            Thread.yield();
        }
        int ret;
        if (width != w || height != h) {
            releaseMemory();
            w = width;
            h = height;
            createMemory(oldMap);
        } else if (lastNewMap == oldMap) {
            // continuous updating, swap the memory address
            long tmp = oldMapMemory;
            oldMapMemory = mapMemory;
            mapMemory = tmp;
        } else {
            // don't need to re-create the buffer, just transfer the data
            // (as if I'm using dGPU.
            ret = CL10.clEnqueueWriteBuffer(clQueue, oldMapMemory, true, 0, oldMap, null, null);
            checkCLError(ret);
        }
        clSetKernelArg1p(clKernel, 0, oldMapMemory);
        clSetKernelArg1p(clKernel, 1, mapMemory);
        clSetKernelArg1i(clKernel, 2, width);
        clSetKernelArg1i(clKernel, 3, height);

        // What? You say optimization? In my dreams...
        final int dimensions = 1;
        PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions);
        globalWorkSize.put(0, w * h);
        ret = clEnqueueNDRangeKernel(clQueue, clKernel, dimensions, null, globalWorkSize, null,
                null, null);
        checkCLError(ret);
        ret = CL10.clFinish(clQueue);
        checkCLError(ret);

        ret = clEnqueueReadBuffer(clQueue, mapMemory, true, 0, intBuffer, null, null);
        checkCLError(ret);
        for (int i = 0; i < intBuffer.capacity(); i++) {
            newMap[i] = intBuffer.get(i);
        }
    }

    @Override
    public String getName() {
        return updaterName;
    }

    @Override
    public void createContext() {
        // Create the context
        PointerBuffer ctxProps = BufferUtils.createPointerBuffer(7);
        ctxProps.put(CL_CONTEXT_PLATFORM).put(clPlatform).put(NULL).flip();

        clContextCB = CLContextCallback.create((errinfo, private_info, cb, user_data)
                -> System.out.printf("cl_context_callback\n\tInfo: %s", memUTF8(errinfo)));
        clContext = clCreateContext(ctxProps, clDevice, clContextCB, NULL, errcode_ret);
        checkCLError(errcode_ret);
    }

    @Override
    public void createCommandQueue() {
        // create command queue
        clQueue = clCreateCommandQueue(clContext, clDevice, NULL, errcode_ret);
        checkCLError(errcode_ret);
    }

    @Override
    public void createProgram() {
        loadProgramText();
        updaterProgram = clCreateProgramWithSource(clContext, programSource, errcode_ret);

        try {
            int errcode = clBuildProgram(updaterProgram, clDevice, "", null, NULL);
            checkCLError(errcode);
        } catch (RuntimeException e) {
            System.err.println(getProgramBuildInfoStringASCII(updaterProgram, clDevice, CL_PROGRAM_BUILD_LOG));
            throw e;
        }
    }

    @Override
    public void createKernel() {
        clKernel = clCreateKernel(updaterProgram, "update", errcode_ret);
        checkCLError(errcode_ret);
    }

    @Override
    public void createMemory(int[] oldMap) {
        oldMapMemory = CL10.clCreateBuffer(clContext, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR,
                getBuffer(oldMap), errcode_ret);
        checkCLError(errcode_ret);
        // their size should be same, shouldn't they?
        mapMemory = CL10.clCreateBuffer(clContext, CL10.CL_MEM_READ_WRITE, Integer.toUnsignedLong(oldMap.length) << 2, errcode_ret);
        checkCLError(errcode_ret);

        intBuffer = BufferUtils.createIntBuffer(oldMap.length);
    }

    @Override
    public void releaseMemory() {
        int errcode;
        if (mapMemory != -1) {
            errcode = CL10.clReleaseMemObject(mapMemory);
            checkCLError(errcode);
        }
        if (oldMapMemory != -1) {
            errcode = CL10.clReleaseMemObject(oldMapMemory);
            checkCLError(errcode);
        }
    }

    public IntBuffer getBuffer(int[] map) {
        // Create float array from 0 to size-1.
        IntBuffer buff = BufferUtils.createIntBuffer(map.length);
        buff.put(map);
        buff.rewind();
        return buff;
    }

    /*
    public static void main(String[] args) {
        // Testing some common swap methods
        long a = 0x126232, b = 0x132535, t;
        int n = 10000;

        long tmp;
        t = TimeUtils.nanoTime();
        for (int i = 1; i <= n; i++) {
            tmp = a;
            a = b;
            b = tmp;
        }
        System.out.println(TimeUtils.nanoTime() - t);

        t = TimeUtils.nanoTime();
        for (int i = 1; i <= n; i++) {
            a = a + b;
            b = a - b;
            a = a - b;
        }
        System.out.println(TimeUtils.nanoTime() - t);

        t = TimeUtils.nanoTime();
        for (int i = 1; i <= n; i++) {
            a = a ^ b;
            b = a ^ b;
            a = a ^ b;
        }
        System.out.println(TimeUtils.nanoTime() - t);

        t = TimeUtils.nanoTime();
        for (int i = 1; i <= n; i++) {
            a = b + (b = a) * 0;
        }
        System.out.println(TimeUtils.nanoTime() - t);
    }
    */

}
