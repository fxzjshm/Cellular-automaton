package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public class SingleThreadUpdater extends CellPoolUpdater {

    public SingleThreadUpdater(CellularAutomaton main) {
        super(main);
    }

    @Override
    public void updateCellPool(int width, int height, int[] oldMapBool, int[] newMapBool) {
        short neighbourCount;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                neighbourCount = 0;
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
        }
    }
}
