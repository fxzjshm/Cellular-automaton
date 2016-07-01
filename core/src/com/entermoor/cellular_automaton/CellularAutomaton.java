package com.entermoor.cellular_automaton;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.Random;

public class CellularAutomaton extends ApplicationAdapter {
    public SpriteBatch batch;
    //  public Texture img;
    public int width, height;
    public boolean[][] mapBool, oldMapBool;
    public Pixmap pixmap;
    public TextureRegion map;
    public float scale = 6;
    public Random random = new Random();

    public short neighbourCount = 0;

    public static Object[] clone__Array__(Object[] array) throws ReflectionException {
        Object[] newArray = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
//            newArray[i]=array[i];
            newArray[i] = ClassReflection.getDeclaredMethod(array[i].getClass(), "clone").invoke(array[i]);
        }
        return newArray;
    }

    @Override
    public void create() {
        //Gdx.graphics.setWindowedMode(64, 48);
        //Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
        width = (int) (Gdx.graphics.getWidth() * 0.9 / scale);
        height = (int) (Gdx.graphics.getHeight() * 0.8 / scale);
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
//        map.getTexture().getTextureData().
        for (int i = 0; i < random.nextInt(width * height / 100 + 1) + width * height / 10; i++) {
            mapBool[random.nextInt(width)][random.nextInt(height)] = true;
        }

        /*for (int i = 0; i < 9; i++) {
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, true)));
            Gdx.app.error("Is cell alive: " + i, String.valueOf(isLive(i, false)));
        }*/
    }

    @Override
    public void render() {
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
