package com.entermoor.cellular_automaton.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.entermoor.cellular_automaton.CellularAutomaton;

import org.lwjgl.opencl.CL;

import java.io.File;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        CellularAutomaton main = new CellularAutomaton();
        CL.getICD();
        new Lwjgl3Application(main, config);
    }
}
