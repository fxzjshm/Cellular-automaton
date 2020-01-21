package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;

import java.util.Random;

public class UpdateRateTester {
    CellularAutomaton main;
    int w = 800, h = 600, n = 1, seed = 20191231, correctHash = 702163960;
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
        double result = 1000 * n / duration;
        Gdx.app.debug("testUpdateRate", ClassReflection.getSimpleName(updater.getClass()) + ":" + result);
        StringBuilder sb = new StringBuilder(w * h);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                sb.append((mapBool[i][j]) ? 1 : 0);
            }
        }
        int hash = sb.toString().hashCode();
        Gdx.app.debug("testUpdateRate", Integer.toString(hash));
        if (hash == correctHash) return result;
        else {
            Gdx.app.debug("testUpdateRate", "Something went wrong when testing " + ClassReflection.getSimpleName(updater.getClass()));
            return -1;
        }
    }
}
