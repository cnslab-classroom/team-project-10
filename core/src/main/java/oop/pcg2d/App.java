package oop.pcg2d;
import oop.pcg2d.generator.CellularAutomata;
import oop.pcg2d.painter.Painter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class App extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture[] grass;
    private CellularAutomata ca = new CellularAutomata(100, 100, 0.45, false, 1);
    private Painter painter = new Painter();
    private int[][] map = ca.generate();
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        grass = new Texture[9];
        grass[0] = new Texture("grass_left_top.png");
        grass[1] = new Texture("grass_mid_top.png");
        grass[2] = new Texture("grass_right_top.png");
        grass[3] = new Texture("grass_left_mid.png");
        grass[4] = new Texture("grass_mid_mid.png");
        grass[5] = new Texture("grass_right_mid.png");
        grass[6] = new Texture("grass_left_bot.png");
        grass[7] = new Texture("grass_mid_bot.png");
        grass[8] = new Texture("grass_right_bot.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        painter.draw(map, batch, grass);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for(int i = 0; i < 9; i++) {
            grass[i].dispose();
        }
    }

    
}

