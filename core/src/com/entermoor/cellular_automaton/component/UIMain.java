package com.entermoor.cellular_automaton.component;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.entermoor.cellular_automaton.CellularAutomaton;

public class UIMain extends ApplicationAdapter {
    public CellularAutomaton main;
    public SpriteBatch batch;
    public Pixmap pixmap;
    public TextureRegion map;
    public Image image;
    public Skin skin;
    public Camera camera;
    public Viewport viewport;
    public Stage stage;

    public float gWidth, gHeight; // prefix g- means graphic

    public UIMain(CellularAutomaton main) {
        this.main = main;
    }

    @Override
    public void create() {
        final int width = main.width, height = main.height,
                pixmapLeftMargin = CellularAutomaton.pixmapLeftMargin, pixmapDownMargin = CellularAutomaton.pixmapDownMargin;
        final float scale = main.scale;

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        stage = new Stage(viewport, batch);
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        map = new TextureRegion(new Texture(pixmap));
        map.setRegion(/*pixmapLeftMargin, pixmapDownMargin*/0, 0, (int) (width * scale), (int) (height * scale));

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        gWidth = width * scale;
        gHeight = height * scale;
        image = new Image(map);
        image.setBounds(pixmapLeftMargin, pixmapDownMargin, gWidth, gHeight);
        stage.addActor(image);

        // stage.setDebugAll(true);
    }

    @Override
    public void render() {
        final int width = main.width, height = main.height;
        if (main.renderNow) {
            main.renderNow = false;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (main.mapBool[x][y] == 1) {
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
}
