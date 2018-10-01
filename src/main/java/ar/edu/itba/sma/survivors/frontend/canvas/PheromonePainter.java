package ar.edu.itba.sma.survivors.frontend.canvas;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;
import ar.edu.itba.sma.survivors.backend.pheromones.Pheromones;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PheromonePainter {

    private Sprite pheromoneSelfishSprite;
    private Sprite pheromoneAlturisticSprite;
    private Pheromones pheromones;

    public PheromonePainter() {
        Pixmap pherAvaroPm = new Pixmap(Survivor.tileSize, Survivor.tileSize, Pixmap.Format.RGBA8888);
        pherAvaroPm.setColor(1f, 0f, 0f, 1f);
        pherAvaroPm.fillTriangle(0, 0, 0, 32, 32, 32);
        this.pheromoneSelfishSprite = new Sprite(new Texture(pherAvaroPm));

        Pixmap pherAlturistaPm = new Pixmap(Survivor.tileSize, Survivor.tileSize, Pixmap.Format.RGBA8888);
        pherAlturistaPm.setColor(0f, 0f, 1f, 1f);
        pherAlturistaPm.fillTriangle(0, 0, 32, 0, 32, 32);
        this.pheromoneAlturisticSprite = new Sprite(new Texture(pherAlturistaPm));
    }

    public void draw(SpriteBatch batch) {
        for (int x = 0; x < Survivor.width; x++) {
            for (int y = 0; y < Survivor.height; y++) {

                pheromoneSelfishSprite.setPosition(x * Survivor.tileSize, Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                pheromoneSelfishSprite.draw(batch, pheromones.getIntensity(x, y, AgentBuilderType.ALTRUISTIC));

                pheromoneAlturisticSprite.setPosition(x * Survivor.tileSize, Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                pheromoneAlturisticSprite.draw(batch, pheromones.getIntensity(x, y, AgentBuilderType.SELFISH));
            }
        }
    }

    public void dispose() {
        pheromoneSelfishSprite.getTexture().dispose();
    }

    public void update(Pheromones pheromones) {
        this.pheromones = pheromones;
    }
}
