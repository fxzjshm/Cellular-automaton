package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.UpdateRateTester;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.MultiThreadUpdater;
import com.entermoor.cellular_automaton.updater.SingleThreadUpdater;

import java.util.LinkedHashSet;
import java.util.Set;

public class UpdaterChooser extends ApplicationAdapter {
    // TODO UI for swiching updater

    public CellularAutomaton main;
    public UpdateRateTester updateRateTester;

    public UpdaterChooser(CellularAutomaton main) {
        this.main = main;
    }

    @Override
    public void create() {
        SingleThreadUpdater singleThreadUpdater = new SingleThreadUpdater(main);
        main.updaters.add(singleThreadUpdater);
        MultiThreadUpdater multiThreadUpdater = new MultiThreadUpdater(main);
        main.updaters.add(multiThreadUpdater);

        updateRateTester = new UpdateRateTester(main);
        updateRateTester.testUpdateRate();
        Set<CellPoolUpdater> wrongUpdaters = new LinkedHashSet<CellPoolUpdater>(main.updaters.size() / 10 + 1);
        for (CellPoolUpdater updater : main.updaters) {
            if (updater.updateRate < 0) wrongUpdaters.add(updater);
        }
        for (CellPoolUpdater updater : wrongUpdaters) {
            main.updaters.remove(updater);
        }

        for (CellPoolUpdater updater : main.updaters) {
            if (updater instanceof AsynchronousUpdater) {
                main.updater = updater;
                break;
            }
        }
        if (null == main.updater || !(main.updaters.contains(main.updater))) {
            if (main.updaters.contains(multiThreadUpdater)) main.updater = multiThreadUpdater;
            else main.updater = singleThreadUpdater;
        }
        Gdx.app.debug("UpdaterChooser", "Chosen " + main.updater.getName());
    }
}
