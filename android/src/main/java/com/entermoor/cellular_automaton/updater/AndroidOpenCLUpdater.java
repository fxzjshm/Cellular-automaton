package com.entermoor.cellular_automaton.updater;

import java.util.Locale;

// TODO implement this
public class AndroidOpenCLUpdater extends OpenCLUpdater {
    public AndroidOpenCLUpdater() {
        super();
        updaterName = null;
    }

    @Override
    public void createContext() {
        createContext0();
    }

    public native void createContext0();

    @Override
    public void createCommandQueue() {
        createCommandQueue0();
    }

    public native void createCommandQueue0();

    @Override
    public void createProgram() {
        loadProgramText();
        createProgram0(programSource);
    }

    public native void createProgram0(String source);

    @Override
    public void createKernel() {
        createKernel0();
    }

    public native void createKernel0();

    @Override
    public void createMemory(int[] oldMap) {
        createMemory0(oldMap);
    }

    public native void createMemory0(int[] oldMap);

    @Override
    public void releaseMemory() {
        releaseMemory0();
    }

    public native void releaseMemory0();

    @Override
    public void updateCellPool(int width, int height, int[] oldMap, int[] newMap) {
        while (preparing) {
            Thread.yield();
        }
        // TODO implement updater
        if (width != w || height != h) {
            releaseMemory();
            w = width;
            h = height;
            createMemory(oldMap);
        } else {
            // don't need to re-create the buffer, just transfer the data
            // (as if I'm using dGPU.
            writeToOldMap0(oldMap);
        }
        setKernelArgs0(width,height);
        callKernel0(width, height);
        readMap0(newMap);
    }

    public native void writeToOldMap0(int[] oldMap);

    public native void setKernelArgs0(int width, int height);

    public native void callKernel0(int width, int height);

    public native void readMap0(int[] newMap);

    @Override
    public String getName() {
        if (null == updaterName) {
            platformName = getPlatformName0();
            deviceName = getDeviceName0();
            updaterName = String.format(Locale.getDefault(), "OpenCLUpdater(%s on %s)", deviceName, platformName);
        }
        return updaterName;
    }

    public native String getPlatformName0();

    public native String getDeviceName0();
}
