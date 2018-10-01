package ar.edu.itba.sma.survivors.frontend.canvas;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.frontend.sprite.AnimatedSprite;
import ar.edu.itba.sma.survivors.frontend.sprite.SimpleSprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.*;

public class Layer {

    private SimpleSprite[][] tiles;

    public Layer() {
        tiles = new SimpleSprite[Survivor.width][Survivor.height];
    }

    public void setSprite(SimpleSprite newTile, Point point) {
        tiles[point.x][point.y] = newTile;
    }

    public SimpleSprite getSprite(Point point) {
        return tiles[point.x][point.y];
    }

    public void draw(SpriteBatch batch) {
        for (int x = 0; x < Survivor.width; x++) {
            for (int y = 0; y < Survivor.height; y++) {
                if (tiles[x][y] != null) {
                    if (tiles[x][y] instanceof AnimatedSprite) {
                        batch.draw(((AnimatedSprite) tiles[x][y]).getTextureRegion(),
                                x * Survivor.tileSize,
                                Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                    } else {
                        batch.draw(tiles[x][y].getTextureRegion(),
                                x * Survivor.tileSize,
                                Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                    }
                }
            }
        }
    }
}
