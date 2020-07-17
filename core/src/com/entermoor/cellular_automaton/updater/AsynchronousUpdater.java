package com.entermoor.cellular_automaton.updater;

public abstract class AsynchronousUpdater extends CellPoolUpdater {
    public String platformName, deviceName, updaterName;
    public int w = 0, h = 0;

    /**
     * A boolean to ensure this updater is fully initialized before used
     *
     * @see AsynchronousUpdater#init
     */
    public volatile boolean preparing = true;

    /**
     * asynchronous init, aiming at not blocking the main thread.
     * Remember to set {@link AsynchronousUpdater#preparing } to false!
     *
     * @see AsynchronousUpdater#preparing
     */
    public abstract void init();

    public abstract void destroy();
}
