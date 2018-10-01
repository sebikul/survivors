package ar.edu.itba.sma.survivors.frontend.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedSprite extends SimpleSprite {

    private long lastTime = System.nanoTime();
    private int curFrame = 0;

    AnimatedSprite(Texture spriteSheet, int[] spriteIndexes) {
        super(spriteSheet, spriteIndexes);
    }

    public TextureRegion getTextureRegion() {
        updateCurrent();
        return getTextureRegionByIndex(curFrame);
    }

    private void updateCurrent() {
        long curTime = System.nanoTime();
        int elapsedNanos = 200000000;
        if (curTime - lastTime > elapsedNanos) {
            if (curFrame == spriteIndexes.length - 1) {
                curFrame = 0;
            } else {
                curFrame += 1;
            }
            lastTime = curTime;
        }
    }
}
