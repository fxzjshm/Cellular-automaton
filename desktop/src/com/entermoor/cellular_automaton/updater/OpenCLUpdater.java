package com.entermoor.cellular_automaton.updater;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.entermoor.cellular_automaton.CellularAutomaton;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLContextCallback;

import java.nio.IntBuffer;

import static org.lwjgl.demo.opencl.InfoUtil.checkCLError;
import static org.lwjgl.demo.opencl.InfoUtil.getProgramBuildInfoStringASCII;
import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class OpenCLUpdater extends CellPoolUpdater {
    public long clPlatform, clDevice;
    // public CLCapabilities platformCapabilities, deviceCapabilities;
    public IntBuffer errcode_ret;

    public long clContext;
    public CLContextCallback clContextCB;

    public long clQueue;

    public long updaterProgram;
    public long clKernel;
    public long oldMapMemory, mapMemory;

    public static String programSource = "";

    public OpenCLUpdater(CellularAutomaton main, long platform, long device/*,CLCapabilities platformCaps,CLCapabilities deviceCaps*/) {
        super(main);
        clPlatform = platform;
        clDevice = device;
    }

    @Override
    public void updateCellPool(int width, int height, boolean[][] oldMap, boolean[][] newMap) {
        // TODO implement updater
    }

    public void createContext() {
        // Create the context
        PointerBuffer ctxProps = BufferUtils.createPointerBuffer(7);
        ctxProps.put(CL_CONTEXT_PLATFORM).put(clPlatform).put(NULL).flip();

        clContextCB = CLContextCallback.create((errinfo, private_info, cb, user_data)
                -> System.out.printf("cl_context_callback\n\tInfo: %s", memUTF8(errinfo)));
        clContext = clCreateContext(ctxProps, clDevice, clContextCB, NULL, errcode_ret);
        checkCLError(errcode_ret);
    }

    public void createCommandQueue() {
        // create command queue
        clQueue = clCreateCommandQueue(clContext, clDevice, NULL, errcode_ret);
        checkCLError(errcode_ret);
    }

    public void createProgram() {
        loadProgramText();
        updaterProgram = clCreateProgramWithSource(clContext, programSource, errcode_ret);

        try {
            int errcode = clBuildProgram(updaterProgram, clDevice, "", null, NULL);
            checkCLError(errcode);
        }catch (RuntimeException e){
            System.err.println(getProgramBuildInfoStringASCII(updaterProgram,clDevice,CL_PROGRAM_BUILD_LOG));
            throw e;
        }
    }

    public void createKernel() {
        clKernel = clCreateKernel(updaterProgram, "update", errcode_ret);
        checkCLError(errcode_ret);
    }

    public void createMemory() {
    }

    public void init() {
        errcode_ret = BufferUtils.createIntBuffer(1);
        // since we have had platform id and device id, we don't need to detect them.
        createContext();
        createCommandQueue();
        createProgram();
        createKernel();
        createMemory();
    }

    public void loadProgramText() {
        if ("".equals(programSource)) {
            FileHandle programFile = new Lwjgl3FileHandle("OpenCLUpdater.cl", Files.FileType.Internal);
            if (!programFile.exists()) {
                throw new IllegalStateException("No program source file found.");
            }
            programSource = programFile.readString();
        }
    }
}
