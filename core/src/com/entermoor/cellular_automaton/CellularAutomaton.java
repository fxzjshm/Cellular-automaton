package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.TimeUtils;
import com.entermoor.cellular_automaton.component.CellFlip;
import com.entermoor.cellular_automaton.component.UIImageButtons;
import com.entermoor.cellular_automaton.component.UIMain;
import com.entermoor.cellular_automaton.component.UpdaterChooser;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class CellularAutomaton extends ApplicationAdapter {

    public static /* final */ int pixmapLeftMargin, pixmapDownMargin;

    //  public Texture img;
    public int width, height;
    public float scale = 5;
    public int[] mapBool, oldMapBool;

    public Random random = new Random();
    public InputMultiplexer input = new InputMultiplexer();

    public boolean isRunning = true;

    public UIMain ui;
    public UIImageButtons uiImageButtons;

    public CellPoolUpdater updater;
    public Set<CellPoolUpdater> updaters = new LinkedHashSet<CellPoolUpdater>(2);
    public UpdateRateTester updateRateTester;
    public UpdaterChooser updaterChooser;


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

        ui = new UIMain(this);
        ui.create();

        random();
        /*for (int i = 0; i < 9; i++) {
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, true)));
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, false)));
        }*/
        if (null != Gdx.input.getInputProcessor())
            input.addProcessor(Gdx.input.getInputProcessor());
        Gdx.input.setInputProcessor(input);
        input.addProcessor(ui.stage);
        new CellFlip(this).create();

        uiImageButtons = new UIImageButtons(this);
        uiImageButtons.create();

        updaterChooser = new UpdaterChooser(this);
        updaterChooser.create();
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
}
