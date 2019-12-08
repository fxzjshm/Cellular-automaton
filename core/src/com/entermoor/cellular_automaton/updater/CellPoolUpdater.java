package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public abstract class CellPoolUpdater {
    int width, height;

    public CellPoolUpdater(CellularAutomaton main) {
        width = main.width;
        height = main.height;
    }

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
