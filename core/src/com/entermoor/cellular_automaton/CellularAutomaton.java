package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.TimeUtils;
import com.entermoor.cellular_automaton.ui.UIImageButtons;
import com.entermoor.cellular_automaton.ui.UIMain;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.SingleThreadUpdater;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class CellularAutomaton extends ApplicationAdapter {

    public static /* final */ int pixmapLeftMargin, pixmapDownMargin;

    //  public Texture img;
    public int width, height;
    public float scale = 5;
    public boolean[][] mapBool, oldMapBool;

    public Random random = new Random();
    public InputMultiplexer input = new InputMultiplexer();

    public boolean isRunning = true;

    public UIMain ui;
    public UIImageButtons uiImageButtons;

    public CellPoolUpdater updater;
    public Set<CellPoolUpdater> updaters = new LinkedHashSet<CellPoolUpdater>(2);
    public UpdateRateTester updateRateTester;
    public long lastRefreshTime = TimeUtils.millis();
    public boolean renderNow = false;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        width = (int) (Gdx.graphics.getWidth() * 0.9 / scale);
        height = (int) (Gdx.graphics.getHeight() * 0.8 / scale);

        pixmapLeftMargin = (int) (Gdx.graphics.getWidth() * 0.05F);
        pixmapDownMargin = (int) (Gdx.graphics.getHeight() * 0.025F);
        mapBool = new boolean[width][height];
        oldMapBool = new boolean[width][height];

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
        input.addProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                int x = ((int) ((screenX - pixmapLeftMargin) / scale));
                int y = ((int) ((screenY + pixmapDownMargin - Gdx.graphics.getHeight()) / scale + height));
                Gdx.app.debug("touchUp", "x = " + x + ", y = " + y);
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    mapBool[x][y] = !mapBool[x][y];
                    renderNow = true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        });

        uiImageButtons = new UIImageButtons(this);
        uiImageButtons.create();

        SingleThreadUpdater defaultUpdater = new SingleThreadUpdater(this);
        updaters.add(defaultUpdater);
        updateRateTester = new UpdateRateTester(this);
        updateRateTester.testUpdateRate();
        Set<CellPoolUpdater> wrongUpdaters = new LinkedHashSet<CellPoolUpdater>(updaters.size() / 10);
        for (CellPoolUpdater updater : updaters) {
            if (updater.updateRate < 0) wrongUpdaters.add(updater);
        }
        for (CellPoolUpdater updater : wrongUpdaters) {
            updaters.remove(updater);
        }

        if (null == updater) updater = defaultUpdater;
    }

    @Override
    public void render() {
        if (isRunning && TimeUtils.timeSinceMillis(lastRefreshTime) >= 200) {
//        oldMapBool = mapBool.clone();
            renderNow = true;
            for (int x = 0; x < width; x++) {
                if (height >= 0) System.arraycopy(mapBool[x], 0, oldMapBool[x], 0, height);
            }
            updater.updateCellPool(width, height, oldMapBool, mapBool);
            lastRefreshTime = TimeUtils.millis();
        }
        ui.render();


    }

    public void random() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapBool[i][j] = random.nextBoolean();
            }
        }
        renderNow = true;
        Gdx.app.debug("randomize", "randomized");
    }
}
