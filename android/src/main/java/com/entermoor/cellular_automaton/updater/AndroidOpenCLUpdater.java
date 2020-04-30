package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

// TODO implement this
public class AndroidOpenCLUpdater extends OpenCLUpdater {
    public AndroidOpenCLUpdater(CellularAutomaton main) {
        super(main);
    }

    @Override
    public void createContext() {

    }

    @Override
    public void createCommandQueue() {

    }

    @Override
    public void createProgram() {

    }

    @Override
    public void createKernel() {

    }

    @Override
    public void createMemory(int[] oldMap) {

    }

    @Override
    public void releaseMemory() {

    }

    @Override
    public void updateCellPool(int width, int height, int[] oldMap, int[] newMap) {

    }

    @Override
    public String getName() {
        return null;
    }
}
