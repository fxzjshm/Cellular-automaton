package com.entermoor.cellular_automaton;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.entermoor.cellular_automaton.android.opencl.AndroidOpenCLLoader;
import com.entermoor.cellular_automaton.android.vulkan.AndroidVulkanLoader;
import com.entermoor.cellular_automaton.updater.AndroidOpenCLUpdater;

public class AndroidLauncher extends AndroidApplication {

    static {
        System.loadLibrary("cellular-automaton-android-jni");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final CellularAutomaton main = new CellularAutomaton();
        if (AndroidOpenCLLoader.loadOpenCLLibrary() == 0) {
            final AndroidOpenCLUpdater CLUpdater = new AndroidOpenCLUpdater(main);
            main.updaters.add(CLUpdater);
            CellularAutomaton.asyncExecutor.submit(new AsyncTask<Object>() {
                @Override
                public Object call() {
                    try {
                        CLUpdater.init();
                        main.updater = CLUpdater;
                    } catch (Exception e) {
                        synchronized (System.err) {
                            System.err.println("[AndroidLauncher] Cannot init device " + CLUpdater.deviceName
                                    + " on platform " + CLUpdater.platformName);
                            e.printStackTrace(System.err);
                            main.updaters.remove(CLUpdater);
                        }
                    }
                    return null;
                }
            });
        }
        if (AndroidVulkanLoader.loadVulkanLibrary() == 0) {

        }
        initialize(main, config);
    }
}
