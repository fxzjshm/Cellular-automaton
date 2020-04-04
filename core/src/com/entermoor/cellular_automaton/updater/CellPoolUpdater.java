package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

// TODO always remember to sync code between SingleThreadUpdater, MultiThreadUpdater and OpenCLUpdater
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

    public void updateSingleCell(int width, int height, int[] oldMapBool, int[] newMapBool, int x, int y) {
        short neighbourCount = 0;
        int xm1 = getRealX(x - 1), xp1 = getRealX(x + 1),
                ym1 = getRealY(y - 1), yp1 = getRealY(y + 1),
                xm1Xh = xm1 * height, xXh = x * height, xp1Xh = xp1 * height,
                gid = xXh + y;
        // TODO Skip formatter in settings
        // @off
        // @formatter:off
        if (oldMapBool[xm1Xh + ym1] == 1) neighbourCount++;
        if (oldMapBool[ xXh  + ym1] == 1) neighbourCount++;
        if (oldMapBool[xp1Xh + ym1] == 1) neighbourCount++;
        if (oldMapBool[xm1Xh +  y ] == 1) neighbourCount++;
        if (oldMapBool[xp1Xh +  y ] == 1) neighbourCount++;
        if (oldMapBool[xm1Xh + yp1] == 1) neighbourCount++;
        if (oldMapBool[ xXh  + yp1] == 1) neighbourCount++;
        if (oldMapBool[xp1Xh + yp1] == 1) neighbourCount++;
        // @on
        // @formatter:on

        if (3 == neighbourCount) newMapBool[gid] = 1;
        else if (2 == neighbourCount) newMapBool[gid] = oldMapBool[gid];
        else newMapBool[gid] = 0;

        // Another version of the rules
                    /*
                    if(oldMapBool[x][y]){
                        if(neighbourCount<2 || neighbourCount>3) mapBool[x][y] = false;
                        else mapBool[x][y] = true;
                    }
                    else{
                        if(neighbourCount==3) mapBool[x][y] = true;
                        else mapBool[x][y] = false;
                    }
                    */
    }

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
