package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;

import java.util.Locale;
import java.util.Random;

public class UpdateRateTester {
    CellularAutomaton main;
    int w = 1920, h = 1080, n = 5, seed = 20200411, correctHash = -1017684658;
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
                oldMapBool[i * h + j] = (r.nextBoolean() ? 1 : 0);
            }
        }
        updater.updateCellPool(w, h, oldMapBool, mapBool); // Make sure the updater has initialized
        pc.reset();
        // System.out.println("Current updater: " + updater.getName());
        for (int i = 0; i < n; i++) {
            pc.start();
            System.arraycopy(mapBool, 0, oldMapBool, 0, w * h);
            updater.updateCellPool(w, h, oldMapBool, mapBool);
            /*
            System.out.println("Round "+i);
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    System.out.print(mapBool[x * h + y]);
                    System.out.print(' ');
                }
                System.out.println();
            }
            */
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
            return -1;
        }
        return result;


    }
}
