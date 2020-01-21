package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public class OpenCLUpdater extends CellPoolUpdater{
    public OpenCLUpdater(CellularAutomaton main){
        super(main);
    }

    @Override
    public void updateCellPool(int width, int height, boolean[][] oldMap, boolean[][] newMap) {

    }
}
