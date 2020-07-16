package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.entermoor.cellular_automaton.CellularAutomaton;

/**
 * Flip the cell when you click on it
 */
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
        main.ui.image.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent e = (InputEvent) event;
                    switch (e.getType()) {
                        case touchUp:
                            float screenX = e.getStageX(), screenY = e.getStageY();
                            int x = ((int) ((screenX - pixmapLeftMargin) / scale));
                            int y = ((int) (-(screenY - pixmapDownMargin) / scale + height));
                            Gdx.app.debug("touchUp", "x = " + x + ", y = " + y);
                            if (x >= 0 && x < width && y >= 0 && y < height) {
                                main.mapBool[x * height + y] = 1 - main.mapBool[x * height + y];
                                main.renderNow = true;
                            }
                            return false;
                        case touchDown:
                            return true;
                    }
                }
                return false;
            }
        });
    }
}
