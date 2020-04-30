package com.entermoor.cellular_automaton;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.entermoor.cellular_automaton.android.opencl.AndroidOpenCLLoader;
import com.entermoor.cellular_automaton.updater.AndroidOpenCLUpdater;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        CellularAutomaton main = new CellularAutomaton();
        if (AndroidOpenCLLoader.loadOpenCLLibrary() == 0) {
            main.updaters.add(new AndroidOpenCLUpdater(main));
        }
        initialize(main, config);
    }
}
