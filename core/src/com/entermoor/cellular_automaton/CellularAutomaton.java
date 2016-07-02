package com.entermoor.cellular_automaton;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class CellularAutomaton extends ApplicationAdapter {

    public static /* final */ int pixmapLeftMargin, pixmapDownMargin;

    public SpriteBatch batch;
    //  public Texture img;
    public int width, height;
    public boolean[][] mapBool, oldMapBool;
    public Pixmap pixmap;
    public TextureRegion map;
    public Image image;
    public float scale = 6;
    public Random random = new Random();
    public InputMultiplexer input = new InputMultiplexer();
    public Skin skin;
    public Camera camera;
    public Viewport viewport;
    public Stage stage;
    public boolean isRunning = true;
    public ImageButton start, pause, restart, randomize, help;

    public short neighbourCount = 0;

    @Override
    public void create() {
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

        for (int i = 0; i < random.nextInt(width * height / 100 + 1) + width * height / 10; i++) {
            mapBool[random.nextInt(width)][random.nextInt(height)] = true;
        }

        /*for (int i = 0; i < 9; i++) {
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, true)));
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, false)));
        }*/
        if (null != Gdx.input.getInputProcessor())
            input.addProcessor(Gdx.input.getInputProcessor());
        Gdx.input.setInputProcessor(input);
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
                int x = getRealX((int) ((screenX - pixmapLeftMargin) / scale));
                int y = getRealY((int) ((screenY - pixmapDownMargin) / scale));
                mapBool[x][y] = !mapBool[x][y];
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

        image = new Image(map);
        image.setBounds(pixmapLeftMargin, pixmapDownMargin, (int) (width * scale), (int) (height * scale));
        stage.addActor(image);

        //TODO image buttons
//        pause = new ImageButton();
//        pause.setSkin(skin);
//        stage.addActor(pause);
    }

    @Override
    public void render() {
        if (isRunning) {
//        oldMapBool = mapBool.clone();
            System.arraycopy(mapBool, 0, oldMapBool, 0, mapBool.length);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    neighbourCount = 0;
                    if (oldMapBool[getRealX(x - 1)][getRealY(y - 1)]) neighbourCount++;
                    if (oldMapBool[getRealX(x)][getRealY(y - 1)]) neighbourCount++;
                    if (oldMapBool[getRealX(x + 1)][getRealY(y - 1)]) neighbourCount++;
                    if (oldMapBool[getRealX(x - 1)][getRealY(y)]) neighbourCount++;
                    if (oldMapBool[getRealX(x + 1)][getRealY(y)]) neighbourCount++;
                    if (oldMapBool[getRealX(x - 1)][getRealY(y + 1)]) neighbourCount++;
                    if (oldMapBool[getRealX(x)][getRealY(y + 1)]) neighbourCount++;
                    if (oldMapBool[getRealX(x + 1)][getRealY(y + 1)]) neighbourCount++;

                    if (3 == neighbourCount) mapBool[x][y] = true;
                    if (neighbourCount < 2 || neighbourCount > 3) mapBool[x][y] = false;

                /*if(oldMapBool[x][y]){
                    if(neighbourCount<2 || neighbourCount>3) mapBool[x][y] = false;
                    else mapBool[x][y] = true;
                }
                else{
                    if(neighbourCount==3) mapBool[x][y] = true;
                    else mapBool[x][y] = false;
                }*/

                    //Gdx.app.error("Cell (X:" + x + ", Y:" + y + ")", "Neighbour Count:" + neighbourCount + ", Is Alive:" + oldMapBool[x][y] + ", Will Be Alive:" + mapBool[x][y]);

                    if (mapBool[x][y]) {
                        pixmap.drawPixel(x, y, 0x87ceebff /*Color.SKY*/);
                    } else {
                        pixmap.drawPixel(x, y, 0x7f7f7fff /*Color.GREY*/);
//                  pixmap.drawPixel(x, y, 0x0000000f /*Color.CLEAR*/);
                    }
                }
            }
        }

        map = new TextureRegion(new Texture(pixmap));
//        map.setRegion(pixmapLeftMargin, pixmapDownMargin, (int) (width * scale), (int) (height * scale));
//        map.setTexture(new Texture(pixmap));
        image.setDrawable(new TextureRegionDrawable(map));
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        batch.begin();
//		batch.draw(img, 0, 0);
//        batch.draw(map, pixmapLeftMargin, pixmapDownMargin, 0, 0, map.getRegionWidth(), map.getRegionHeight(), scale, scale, 0);
//        batch.end();
        stage.draw();
    }

    public int getRealX(int x) {
        if (x < 0) {
            x = x + width;
            x = getRealX(x);
        }
        if (x > width - 1) {
            x = x - width;
            x = getRealX(x);
        }
        return x;
    }

    public int getRealY(int y) {
        if (y < 0) {
            y = y + height;
            y = getRealY(y);
        }
        if (y > height - 1) {
            y = y - height;
            y = getRealY(y);
        }
        return y;
    }

    /*public static boolean isLive(int neighbourCount, boolean isAlive) {
        if (3 == neighbourCount) return true;
        if (neighbourCount < 2 || neighbourCount > 3) return false;
        return isAlive;
    }*/
}
