package com.entermoor.cellular_automaton;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.entermoor.cellular_automaton.updater.CellPoolUpdater;
import com.entermoor.cellular_automaton.updater.SingleThreadUpdater;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import io.github.fxzjshm.gdx.svg2pixmap.Svg2Pixmap;

public class CellularAutomaton extends ApplicationAdapter {

    public static /* final */ int pixmapLeftMargin, pixmapDownMargin;

    public SpriteBatch batch;
    //  public Texture img;
    public int width, height;
    public boolean[][] mapBool, oldMapBool;
    public Pixmap pixmap;
    public TextureRegion map;
    public Image image;
    public float scale = 5;
    public Random random = new Random();
    public InputMultiplexer input = new InputMultiplexer();
    public Skin skin;
    public Camera camera;
    public Viewport viewport;
    public Stage stage;
    public boolean isRunning = true;
    public ImageButton start, pause, restart, randomize, help;

    public CellPoolUpdater updater;
    public Set<CellPoolUpdater> updaters = new LinkedHashSet<CellPoolUpdater>(2);
    public long lastRefreshTime = TimeUtils.millis();
    public boolean renderNow = false;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        batch = new SpriteBatch();
        width = (int) (Gdx.graphics.getWidth() * 0.9 / scale);
        height = (int) (Gdx.graphics.getHeight() * 0.8 / scale);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        stage = new Stage(viewport, batch);
        pixmapLeftMargin = (int) (Gdx.graphics.getWidth() * 0.05F);
        pixmapDownMargin = (int) (Gdx.graphics.getHeight() * 0.025F);
        mapBool = new boolean[width][height];
//        oldMapBool = mapBool.clone();
//        System.arraycopy(mapBool, 0, oldMapBool, 0, mapBool.length);
        oldMapBool = new boolean[width][height];
//        try {
//            clone__Array__(mapBool);
//        } catch (ReflectionException e) {
//            e.printStackTrace();
//        }
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        map = new TextureRegion(new Texture(pixmap));
        map.setRegion(/*pixmapLeftMargin, pixmapDownMargin*/0, 0, (int) (width * scale), (int) (height * scale));
//        map.getTexture().getTextureData().

        random();
        /*for (int i = 0; i < 9; i++) {
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, true)));
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, false)));
        }*/
        if (null != Gdx.input.getInputProcessor())
            input.addProcessor(Gdx.input.getInputProcessor());
        Gdx.input.setInputProcessor(input);
        input.addProcessor(stage);
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


        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        final float actualWidth = width * scale, actualHeight = height * scale, heightLeft = Gdx.graphics.getHeight() - actualHeight - pixmapDownMargin;
        image = new Image(map);
        image.setBounds(pixmapLeftMargin, pixmapDownMargin, actualWidth, actualHeight);
        stage.addActor(image);

//        TODO image buttons

        pause = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("pause.svg").readString()))));

        pause.setSkin(skin);
        pause.setBounds(pixmapLeftMargin, pixmapDownMargin + actualHeight, actualWidth / 4, heightLeft);
        pause.addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent && ((InputEvent) event).getType().equals(InputEvent.Type.touchDown)) {
                    isRunning = false;
                    Gdx.app.debug("pause", "paused");
                }
                return false;
            }
        });
        stage.addActor(pause);

        start = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("caret-right.svg").readString()))));

        start.setSkin(skin);
        start.setBounds(pixmapLeftMargin + actualWidth / 4, pixmapDownMargin + actualHeight, actualWidth / 4, heightLeft);
        start.addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent && ((InputEvent) event).getType().equals(InputEvent.Type.touchDown)) {
                    isRunning = true;
                    Gdx.app.debug("start", "resued");
                }
                return false;
            }
        });
        stage.addActor(start);


        randomize = new ImageButton(new TextureRegionDrawable(new Texture(Svg2Pixmap.svg2Pixmap(Gdx.files.internal("reload.svg").readString()))));

        randomize.setSkin(skin);
        randomize.setBounds(pixmapLeftMargin + actualWidth / 2, pixmapDownMargin + actualHeight, actualWidth / 4, heightLeft);
        randomize.addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent && ((InputEvent) event).getType().equals(InputEvent.Type.touchDown)) {
                    random();
                }
                return false;
            }
        });
        stage.addActor(randomize);

        updater = new SingleThreadUpdater(this);
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
        if (renderNow) {
            renderNow = false;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (mapBool[x][y]) {
                        pixmap.drawPixel(x, y, 0x87ceebff /*Color.SKY*/);
                    } else {
                        pixmap.drawPixel(x, y, 0x7f7f7fff /*Color.GREY*/);
//                  pixmap.drawPixel(x, y, 0x0000000f /*Color.CLEAR*/);
                    }
                }
            }

            /*
            // This isn't working properly when scale != 1
            // a new TextureRegion() is necessary, but why?
            map.getTexture().load(map.getTexture().getTextureData());
            */
            map.getTexture().dispose();
            map = new TextureRegion(new Texture(pixmap));
            ((TextureRegionDrawable) (image.getDrawable())).setRegion(map);
        }

        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

    }

    /*public static boolean isLive(int neighbourCount, boolean isAlive) {
        if (3 == neighbourCount) return true;
        if (neighbourCount < 2 || neighbourCount > 3) return false;
        return isAlive;
    }*/

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
