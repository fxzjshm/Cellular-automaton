package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.entermoor.cellular_automaton.CellularAutomaton;

import io.github.fxzjshm.gdx.svg2pixmap.Svg2Pixmap;

public class UIImageButtons extends ApplicationAdapter {
    public CellularAutomaton main;
    public ImageButton start, pause, restart, randomize, help;

    public UIImageButtons(CellularAutomaton main) {
        this.main = main;
    }

    @Override
    public void create() {
        float gHeight = main.ui.gHeight, gWidth = main.ui.gWidth;
        final float heightLeft = Gdx.graphics.getHeight() - gHeight - CellularAutomaton.pixmapDownMargin;
        Stage stage = main.ui.stage;
        Skin skin = main.ui.skin;

        // TODO image buttons

        pause = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("pause.svg").readString()))));

        pause.setSkin(skin);
        pause.setBounds(CellularAutomaton.pixmapLeftMargin, CellularAutomaton.pixmapDownMargin + gHeight, gHeight / 4, heightLeft);
        pause.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent e = (InputEvent) event;
                    switch (e.getType()) {
                        case touchUp:
                            main.isRunning = false;
                            Gdx.app.debug("pause", "paused");
                            return false;
                        case touchDown:
                            return true;
                    }
                }
                return false;
            }
        });
        stage.addActor(pause);

        start = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("caret-right.svg").readString()))));

        start.setSkin(skin);
        start.setBounds(CellularAutomaton.pixmapLeftMargin + gWidth / 4, CellularAutomaton.pixmapDownMargin + gHeight, gHeight / 4, heightLeft);
        start.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent e = (InputEvent) event;
                    switch (e.getType()) {
                        case touchUp:
                            main.isRunning = true;
                            Gdx.app.debug("start", "resumed");
                            return false;
                        case touchDown:
                            return true;
                    }
                }
                return false;
            }
        });
        stage.addActor(start);


        randomize = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("reload.svg").readString()))));

        randomize.setSkin(skin);
        randomize.setBounds(CellularAutomaton.pixmapLeftMargin + gWidth / 2, CellularAutomaton.pixmapDownMargin + gHeight, gHeight / 4, heightLeft);
        randomize.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    InputEvent e = (InputEvent) event;
                    switch (e.getType()) {
                        case touchUp:
                            main.random();
                            return false;
                        case touchDown:
                            return true;
                    }
                }
                return false;
            }
        });
        stage.addActor(randomize);
    }
}
