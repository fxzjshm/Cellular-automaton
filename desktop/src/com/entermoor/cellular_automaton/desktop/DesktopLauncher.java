package com.entermoor.cellular_automaton.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.entermoor.cellular_automaton.CellularAutomaton;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
        config.fullscreen = false;
        new LwjglApplication(new CellularAutomaton(), config);
    }
}
