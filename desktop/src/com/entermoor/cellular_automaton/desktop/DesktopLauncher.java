package com.entermoor.cellular_automaton.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.entermoor.cellular_automaton.CellularAutomaton;
import com.entermoor.cellular_automaton.desktop.opencl.OpenCLUpdaterGenerator;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        CellularAutomaton main = new CellularAutomaton();
        main.updaters.addAll(OpenCLUpdaterGenerator.generateOpenCLUpdater(main));
        new Lwjgl3Application(main, config);
    }
}
