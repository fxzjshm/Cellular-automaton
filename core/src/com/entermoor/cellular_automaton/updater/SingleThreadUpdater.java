package com.entermoor.cellular_automaton.updater;

import com.entermoor.cellular_automaton.CellularAutomaton;

public class SingleThreadUpdater extends CellPoolUpdater {

    public SingleThreadUpdater(CellularAutomaton main) {
        super(main);
    }

    @Override
    public void updateCellPool(int width, int height, boolean[][] oldMapBool, boolean[][] newMapBool) {
        short neighbourCount = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                neighbourCount = 0;
                int xm1 = getRealX(x - 1), xp1 = getRealX(x + 1),
                        ym1 = getRealY(y - 1), yp1 = getRealY(y + 1);
                // TODO Skip formatter in settings
                // @off
                // @formatter:off
                if (oldMapBool[xm1][ym1]) neighbourCount++;
                if (oldMapBool[ x ][ym1]) neighbourCount++;
                if (oldMapBool[xp1][ym1]) neighbourCount++;
                if (oldMapBool[xm1][ y ]) neighbourCount++;
                if (oldMapBool[xp1][ y ]) neighbourCount++;
                if (oldMapBool[xm1][yp1]) neighbourCount++;
                if (oldMapBool[ x ][yp1]) neighbourCount++;
                if (oldMapBool[xp1][yp1]) neighbourCount++;
                // @on
                // @formatter:on

                if (3 == neighbourCount) newMapBool[x][y] = true;
                if (neighbourCount < 2 || neighbourCount > 3) newMapBool[x][y] = false;

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
