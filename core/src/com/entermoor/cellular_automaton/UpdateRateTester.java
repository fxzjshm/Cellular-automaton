package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;

import java.util.Random;

public class UpdateRateTester {
    CellularAutomaton main;
    int w = 800, h = 600, n = 10, seed = 20191231, correctHash = -283375405;
    boolean[][] oldMapBool = new boolean[w][h], mapBool = oldMapBool.clone();

    public UpdateRateTester(CellularAutomaton main) {
        this.main = main;
    }

    public void testUpdateRate() {
        for (CellPoolUpdater updater : main.updaters) {
            updater.updateRate = testUpdateRate(updater);
        }
    }

    public double testUpdateRate(CellPoolUpdater updater) {
        Random r = new Random(seed);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                mapBool[i][j] = r.nextBoolean();
            }
        }
        long startTime;
        startTime = TimeUtils.millis();
        for (int i = 0; i < n; i++) {
            for (int x = 0; x < w; x++) {
                if (h >= 0) System.arraycopy(mapBool[x], 0, oldMapBool[x], 0, h);
            }
            updater.updateCellPool(w, h, oldMapBool, mapBool);
        }
        long duration = TimeUtils.timeSinceMillis(startTime);
        double result = 1.0 * duration / n;

        int hash = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                hash += ((mapBool[i][j]) ? 1 : 0) * r.nextInt();
            }
        }

        Gdx.app.debug("testUpdateRate", String.format("%s:%.3fms, result hash %d", ClassReflection.getSimpleName(updater.getClass()), result,hash));

        if (hash != correctHash) {
            Gdx.app.debug("testUpdateRate", "Something went wrong when testing " + ClassReflection.getSimpleName(updater.getClass()));
            // TODO check hash value here
            // return -1;
        }
        return result;


    }
}
