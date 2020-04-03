package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

// TODO always remember to sync code between SingleThreadUpdater,  and OpenCLUpdater
public abstract class CellPoolUpdater {
    /**
     * of the number of cells
     */
    int width, height;
    public double updateRate;

    public CellPoolUpdater(CellularAutomaton main) {
        width = main.width;
        height = main.height;
    }

    /**
     * Update newMap in accordance with oldMap
     *
     * @param width  width of the pool
     * @param height height of the pool
     * @param oldMap the map to read from
     * @param newMap the map to write to
     */
    public abstract void updateCellPool(int width, int height, int[] oldMap, int[] newMap);

    public int getRealX(int x) {
        /*
        if (x < 0) {
            x = x + width;
            x = getRealX(x);
        }
        if (x > width - 1) {
            x = x - width;
            x = getRealX(x);
        }
        */
        while (x < 0) {
            x += width;
        }
        final int w1 = width - 1;
        while (x > w1) {
            x -= width;
        }
        return x;
    }

    public int getRealY(int y) {
        /*
        if (y < 0) {
            y = y + height;
            y = getRealY(y);
        }
        if (y > height - 1) {
            y = y - height;
            y = getRealY(y);
        }
        */
        while (y < 0) {
            y += height;
        }
        final int h1 = height - 1;
        while (y > h1) {
            y -= height;
        }
        return y;
    }

    public abstract String getName();


    /*public static boolean isLive(int neighbourCount, boolean isAlive) {
        if (3 == neighbourCount) return true;
        if (neighbourCount < 2 || neighbourCount > 3) return false;
        return isAlive;
    }*/
}
