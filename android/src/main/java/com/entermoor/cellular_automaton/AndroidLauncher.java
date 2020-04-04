package com.entermoor.cellular_automaton;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.entermoor.cellular_automaton.android.opencl.AndroidOpenCLLoader;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        AndroidOpenCLLoader.loadOpenCLLibrary();
        initialize(new CellularAutomaton(), config);
    }
}
