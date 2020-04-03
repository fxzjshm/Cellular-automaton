package com.entermoor.cellular_automaton.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.desktop.opencl.OpenCLUpdaterGenerator;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.OpenCLUpdater;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        CellularAutomaton main = new CellularAutomaton();
        CellularAutomaton.asyncExecutor.submit(() -> {
            main.updaters.addAll(OpenCLUpdaterGenerator.generateOpenCLUpdater(main));

            for (CellPoolUpdater updater : main.updaters) {
                if (updater instanceof OpenCLUpdater) {
                    main.updater = updater;
                }
            }
            return null;
        });
        new Lwjgl3Application(main, config);
    }
}
