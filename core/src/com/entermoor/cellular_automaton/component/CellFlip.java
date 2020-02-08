package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.entermoor.cellular_automaton.CellularAutomaton;

public class CellFlip extends ApplicationAdapter {

    public CellularAutomaton main;

    public CellFlip(CellularAutomaton main) {
        this.main = main;
    }

    @Override
    public void create() {
        final int width = main.width, height = main.height,
                pixmapLeftMargin = CellularAutomaton.pixmapLeftMargin, pixmapDownMargin = CellularAutomaton.pixmapDownMargin;
        final float scale = main.scale;
        main.input.addProcessor(new InputProcessor() {
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
                    main.mapBool[x][y] = 1 - main.mapBool[x][y];
                    main.renderNow = true;
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
    }
}
