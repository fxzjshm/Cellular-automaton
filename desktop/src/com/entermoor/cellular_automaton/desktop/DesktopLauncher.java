package com.entermoor.cellular_automaton.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.desktop.opencl.DesktopOpenCLUpdaterGenerator;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.DesktopOpenCLUpdater;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        CellularAutomaton main = new CellularAutomaton();
        CellularAutomaton.asyncExecutor.submit(() -> {
            main.updaters.addAll(DesktopOpenCLUpdaterGenerator.generateOpenCLUpdater());

            for (CellPoolUpdater updater : main.updaters) {
                if (updater instanceof DesktopOpenCLUpdater) {
                    main.updater = updater;
                }
            }
            return null;
        });
        new Lwjgl3Application(main, config);
    }
}
