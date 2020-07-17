package com.entermoor.cellular_automaton.updater;

import com.badlogic.gdx.utils.async.AsyncTask;
import com.badlogic.gdx.utils.async.ThreadUtils;
import com.entermoor.cellular_automaton.CellularAutomaton;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadUpdater extends CellPoolUpdater {

    public int nThread;
    public String name;

    public MultiThreadUpdater() {
        nThread = Runtime.getRuntime().availableProcessors();
        name = String.format(Locale.getDefault(), "MultiThreadUpdater(%d thread(s))", nThread);
    }

    @Override
    public void updateCellPool(final int width, final int height, final int[] oldMapBool, final int[] newMapBool) {
        final int N = width * height;
        final AtomicInteger finishedThreadCount = new AtomicInteger();
        for (int i = 0; i < nThread; i++) {
            final int id = i;
            CellularAutomaton.asyncExecutor.submit(new AsyncTask<Object>() {
                @Override
                public Object call() {
                    for (int gid = id; gid < N; gid += nThread) {
                        int x = gid / height, y = gid % height;
                        updateSingleCell(width, height, oldMapBool, newMapBool, x, y);
                    }
                    finishedThreadCount.incrementAndGet();
                    return null;
                }
            });
        }
        while (finishedThreadCount.get() < nThread) ThreadUtils.yield();
    }

    @Override
    public String getName() {
        return name;
    }
}
