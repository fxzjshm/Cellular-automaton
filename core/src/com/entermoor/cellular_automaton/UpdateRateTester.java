package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;

import java.util.Locale;
import java.util.Random;

public class UpdateRateTester {
    CellularAutomaton main;
    int w = 800, h = 600, n = 10, seed = 20191231, correctHash = -283375405;
    int[] oldMapBool = new int[w * h], mapBool = oldMapBool.clone();
    PerformanceCounter pc = new PerformanceCounter("UpdateRatePerformanceCounter", n);

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
                mapBool[i * h + j] = (r.nextBoolean() ? 1 : 0);
            }
        }
        pc.reset();
        for (int i = 0; i < n; i++) {
            pc.start();
            System.arraycopy(mapBool, 0, oldMapBool, 0, w * h);
            updater.updateCellPool(w, h, oldMapBool, mapBool);
            pc.stop();
            pc.tick();
        }
        double result = pc.time.average * 1000f;

        r.setSeed(seed);
        int hash = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                hash += (mapBool[i * h + j]) * r.nextInt();
            }
        }

        Gdx.app.debug("testUpdateRate", String.format(Locale.getDefault(), "%s:%.3fms, result hash %d", updater.getName(), result, hash));

        if (hash != correctHash) {
            Gdx.app.debug("testUpdateRate", "Something went wrong when testing " + ClassReflection.getSimpleName(updater.getClass()));
            // TODO check hash value here
            // return -1;
        }
        return result;


    }
}
