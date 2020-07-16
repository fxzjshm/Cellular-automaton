package com.entermoor.cellular_automaton.updater;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.entermoor.cellular_automaton.CellularAutomaton;

public abstract class OpenCLUpdater extends AsynchronousUpdater {

    public static String programSource = "";

    public OpenCLUpdater(CellularAutomaton main) {
        super(main);
    }

    @Override
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

    @Override
    public void destroy() {
        releaseMemory();
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
