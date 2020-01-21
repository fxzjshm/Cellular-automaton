package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public abstract class CellPoolUpdater {
    /** Parameter of the window (not the pool)*/
    int width, height;
    public double updateRate;

    public CellPoolUpdater(CellularAutomaton main) {
        width = main.width;
        height = main.height;
    }

    /**
     * Update newMap in accordance with oldMap
     * @param width width of the pool
     * @param height height of the pool
     * @param oldMap the map to read from
     * @param newMap the map to write to
     */
    public abstract void updateCellPool(int width, int height, boolean[][] oldMap, boolean[][] newMap);

    public int getRealX(int x) {
        if (x < 0) {
            x = x + width;
            x = getRealX(x);
        }
        if (x > width - 1) {
            x = x - width;
            x = getRealX(x);
        }
        return x;
    }

    public int getRealY(int y) {
        if (y < 0) {
            y = y + height;
            y = getRealY(y);
        }
        if (y > height - 1) {
            y = y - height;
            y = getRealY(y);
        }
        return y;
    }
}
