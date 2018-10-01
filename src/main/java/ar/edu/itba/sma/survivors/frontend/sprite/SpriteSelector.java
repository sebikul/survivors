package ar.edu.itba.sma.survivors.frontend.sprite;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpriteSelector {

    protected List<SimpleSprite> possibleSprites = new ArrayList<>();
    private Random rand = new Random();

    public SpriteSelector(Texture spriteSheet, int[][] indexes) {
        for (int[] index : indexes) {
            if (index.length <= 1) {
                possibleSprites.add(new SimpleSprite(spriteSheet, index));
            } else {
                possibleSprites.add(new AnimatedSprite(spriteSheet, index));
            }
        }
    }

    public SimpleSprite getRandom() {
        int index = rand.nextInt(possibleSprites.size());
        return possibleSprites.get(index);
    }

    public SimpleSprite getIndex(int index) {
        return possibleSprites.get(index);
    }

}
