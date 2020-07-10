package com.entermoor.cellular_automaton.updater;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.entermoor.cellular_automaton.CellularAutomaton;

public abstract class OpenCLUpdater extends CellPoolUpdater {
    public String platformName, deviceName, updaterName;
    public int w = 0, h = 0;

    public static String programSource = "";

    /**
     * A boolean to ensure this updater is fully initialized before used
     * @see OpenCLUpdater#init
     */
    public volatile boolean preparing = true;

    public OpenCLUpdater(CellularAutomaton main) {
        super(main);
    }

    /**
     * asynchronous init, aiming at not blocking the main thread.
     * @see OpenCLUpdater#preparing
     */
    public void init() {
        // since we have had platform id and device id, we don't need to detect them.
        createContext();
        createCommandQueue();
        loadProgramText();
        createProgram();
        createKernel();
        // haven't known the size yet
        // createMemory();
        preparing = false;
    }

    public abstract void createContext();

    public abstract void createCommandQueue();

    public abstract void createProgram();

    public abstract void createKernel();

    public abstract void createMemory(int[] oldMap);

    public abstract void releaseMemory();

    public void loadProgramText() {
        if ("".equals(programSource)) {
            while (Gdx.files == null) Thread.yield();
            FileHandle programFile = Gdx.files.getFileHandle("OpenCLUpdater.cl", Files.FileType.Internal);
            if (!programFile.exists()) {
                throw new IllegalStateException("No program source file found.");
            }
            programSource = programFile.readString();
        }
    }
}
