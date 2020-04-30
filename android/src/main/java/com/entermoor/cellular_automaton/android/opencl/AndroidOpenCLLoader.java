package com.entermoor.cellular_automaton.android.opencl;

public class AndroidOpenCLLoader {

    static {
        System.loadLibrary("cellular-automaton-android-jni");
    }

    public static native int loadOpenCLLibrary0();

    public static int loadOpenCLLibrary() {
        return loadOpenCLLibrary0();
    }
}
