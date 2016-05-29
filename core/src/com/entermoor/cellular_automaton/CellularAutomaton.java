package com.entermoor.cellular_automaton;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Random;

public class CellularAutomaton extends ApplicationAdapter {
    public SpriteBatch batch;
    //  public Texture img;
    public int width, height;
    public boolean[][] mapBool,oldMapBool;
    public Pixmap pixmap;
    public TextureRegion map;
    public float scale = 6;
    public Random random = new Random();

    @Override
    public void create() {
        batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
        width = (int) (Gdx.graphics.getWidth() * 0.9 / scale);
        height = (int) (Gdx.graphics.getHeight() * 0.8 / scale);
        mapBool = new boolean[width][height];
        oldMapBool=mapBool.clone();
        pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        map = new TextureRegion(new Texture(pixmap));
//        map.getTexture().getTextureData().
        for (int i = 0; i < random.nextInt(width * height / 2) + width * height / 2; i++) {
            mapBool[random.nextInt(width)][random.nextInt(height)] = true;
        }
    }

    @Override
    public void render() {
        oldMapBool=mapBool.clone();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (mapBool[x][y]) {
                    pixmap.drawPixel(x, y, 0x87ceebff /*Color.SKY*/);
                } else {
                    pixmap.drawPixel(x, y, 0x7f7f7fff /*Color.GREY*/);
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (mapBool[x][y]) {
                    pixmap.drawPixel(x, y, 0x87ceebff /*Color.SKY*/);
                } else {
                    pixmap.drawPixel(x, y, 0x000000f /*Color.CLEAR*/);
                }
            }
        }
        map = new TextureRegion(new Texture(pixmap));
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
//		batch.draw(img, 0, 0);
        batch.draw(map, Gdx.graphics.getWidth() * 0.05F, Gdx.graphics.getWidth() * 0.025F, 0, 0, map.getRegionWidth(), map.getRegionHeight(), scale, scale, 0);
        batch.end();
    }

    public int getRealX(int x) {
        if (x < 0) {
            x = x + width;
            x = getRealX(x);
        }
        if (x > width) {
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
        if (y > width) {
            y = y - height;
            y = getRealY(y);
        }
        return y;
    }
}
