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
                if (oldMapBool[getRealX(x - 1)][getRealY(y - 1)]) neighbourCount++;
                if (oldMapBool[getRealX(x)][getRealY(y - 1)]) neighbourCount++;
                if (oldMapBool[getRealX(x + 1)][getRealY(y - 1)]) neighbourCount++;
                if (oldMapBool[getRealX(x - 1)][getRealY(y)]) neighbourCount++;
                if (oldMapBool[getRealX(x + 1)][getRealY(y)]) neighbourCount++;
                if (oldMapBool[getRealX(x - 1)][getRealY(y + 1)]) neighbourCount++;
                if (oldMapBool[getRealX(x)][getRealY(y + 1)]) neighbourCount++;
                if (oldMapBool[getRealX(x + 1)][getRealY(y + 1)]) neighbourCount++;

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
