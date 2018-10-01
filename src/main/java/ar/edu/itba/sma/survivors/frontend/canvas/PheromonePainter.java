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

    Sprite pheromoneAvaroSprite;
    Sprite pheromoneAlturistaSprite;
    Pheromones pheromones;

    public PheromonePainter() {
        Pixmap pherAvaroPm = new Pixmap(Survivor.tileSize, Survivor.tileSize, Pixmap.Format.RGBA8888);
        pherAvaroPm.setColor(1f, 0f, 0f, 1f);
        pherAvaroPm.fillTriangle(0, 0, 0, 32, 32, 32);
        this.pheromoneAvaroSprite = new Sprite(new Texture(pherAvaroPm));

        Pixmap pherAlturistaPm = new Pixmap(Survivor.tileSize, Survivor.tileSize, Pixmap.Format.RGBA8888);
        pherAlturistaPm.setColor(0f, 0f, 1f, 1f);
        pherAlturistaPm.fillTriangle(0, 0, 32, 0, 32, 32);
        this.pheromoneAlturistaSprite = new Sprite(new Texture(pherAlturistaPm));
    }

    public void draw(SpriteBatch batch) {
        for (int x = 0; x < Survivor.width; x++) {
            for (int y = 0; y < Survivor.height; y++) {

                pheromoneAvaroSprite.setPosition(x * Survivor.tileSize, Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                pheromoneAvaroSprite.draw(batch, pheromones.getIntensity(x, y, AgentBuilderType.ALTRUISTIC));

                pheromoneAlturistaSprite.setPosition(x * Survivor.tileSize, Gdx.graphics.getHeight() - (y + 1) * Survivor.tileSize);
                pheromoneAlturistaSprite.draw(batch, pheromones.getIntensity(x, y, AgentBuilderType.SELFISH));
            }
        }
    }

    public void dispose() {
        pheromoneAvaroSprite.getTexture().dispose();
    }

    public void update(Pheromones pheromones) {
        this.pheromones = pheromones;
    }
}
