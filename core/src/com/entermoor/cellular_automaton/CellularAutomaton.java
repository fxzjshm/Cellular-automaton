package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.entermoor.cellular_automaton.component.CellFlip;
import com.entermoor.cellular_automaton.component.UIImageButtons;
import com.entermoor.cellular_automaton.component.UIMain;
import com.entermoor.cellular_automaton.component.UpdaterChooser;
import com.entermoor.cellular_automaton.updater.AsynchronousUpdater;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.kotcrab.vis.ui.VisUI;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class CellularAutomaton extends ApplicationAdapter {

    public static /* final */ int pixmapLeftMargin, pixmapDownMargin;
    public static AsyncExecutor asyncExecutor = new AsyncExecutor(Runtime.getRuntime().availableProcessors());

    //  public Texture img;
    public int width, height;
    public float scale = 5;
    public int[] mapBool, oldMapBool;

    public Random random = new Random();
    public InputMultiplexer input = new InputMultiplexer();

    public boolean isRunning = true;

    public CellFlip cellFlip;
    public UIMain ui;
    public UIImageButtons uiImageButtons;
    public UpdaterChooser updaterChooser;

    public CellPoolUpdater updater;
    public Set<CellPoolUpdater> updaters = new LinkedHashSet<CellPoolUpdater>(2);

    public long lastRefreshTime = TimeUtils.millis();
    public boolean renderNow = false;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        width = (int) (Gdx.graphics.getWidth() * 0.9 / scale);
        height = (int) (Gdx.graphics.getHeight() * 0.8 / scale);

        pixmapLeftMargin = (int) (Gdx.graphics.getWidth() * 0.05F);
        pixmapDownMargin = (int) (Gdx.graphics.getHeight() * 0.025F);
        mapBool = new int[width * height];
        oldMapBool = new int[width * height];

        VisUI.load();

        ui = new UIMain(this);
        cellFlip = new CellFlip(this);
        uiImageButtons = new UIImageButtons(this);
        updaterChooser = new UpdaterChooser(this);

        ui.create();
        cellFlip.create();
        uiImageButtons.create();
        updaterChooser.create();

        random();
        /*for (int i = 0; i < 9; i++) {
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, true)));
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, false)));
        }*/
        if (null != Gdx.input.getInputProcessor())
            input.addProcessor(Gdx.input.getInputProcessor());
        Gdx.input.setInputProcessor(input);
        input.addProcessor(0,ui.stage);
    }

    @Override
    public void render() {
        if (isRunning && TimeUtils.timeSinceMillis(lastRefreshTime) >= 200) {
//        oldMapBool = mapBool.clone();
            renderNow = true;
            System.arraycopy(mapBool, 0, oldMapBool, 0, width * height);
            updater.updateCellPool(width, height, oldMapBool, mapBool);
            lastRefreshTime = TimeUtils.millis();
        }
        ui.render();
        cellFlip.render();
        uiImageButtons.render();
        updaterChooser.render();
    }

    public void random() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapBool[i * height + j] = (random.nextBoolean() ? 1 : 0);
            }
        }
        renderNow = true;
        Gdx.app.debug("randomize", "randomized");
    }

    @Override
    public void resize(int width, int height) {
        ui.resize(width, height);
        cellFlip.resize(width, height);
        uiImageButtons.resize(width, height);
        updaterChooser.resize(width, height);
    }

    @Override
    public void pause() {
        ui.pause();
        cellFlip.pause();
        uiImageButtons.pause();
        updaterChooser.pause();
        isRunning = false;
    }

    @Override
    public void resume() {
        ui.resume();
        cellFlip.resume();
        uiImageButtons.resume();
        updaterChooser.resume();
        isRunning = true;
    }

    @Override
    public void dispose() {
        for (CellPoolUpdater updater : updaters) {
            if (updater instanceof AsynchronousUpdater) {
                ((AsynchronousUpdater) updater).destroy();
            }
        }
        ui.dispose();
        cellFlip.dispose();
        uiImageButtons.dispose();
        updaterChooser.dispose();
        VisUI.dispose();
    }
}
