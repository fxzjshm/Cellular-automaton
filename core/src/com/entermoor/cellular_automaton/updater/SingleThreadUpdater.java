package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public class SingleThreadUpdater extends CellPoolUpdater {

    public SingleThreadUpdater(CellularAutomaton main) {
        super(main);
    }

    @Override
    public void updateCellPool(int width, int height, int[] oldMapBool, int[] newMapBool) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                updateSingleCell(width, height, oldMapBool, newMapBool, x, y);
            }
        }
    }

    @Override
    public String getName() {
        return "SingleThreadUpdater";
    }
}
