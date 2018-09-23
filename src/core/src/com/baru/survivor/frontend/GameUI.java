package com.baru.survivor.frontend;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.baru.survivor.Survivor;
import com.baru.survivor.backend.State;
import com.baru.survivor.backend.agents.Agent;
import com.baru.survivor.backend.agents.AgentBuilderType;
import com.baru.survivor.backend.agents.DayCycle;
import com.baru.survivor.backend.pheromones.Pheromones;
import com.baru.survivor.backend.resources.Reservoir;
import com.baru.survivor.backend.village.Tribe;
import com.baru.survivor.frontend.canvas.Grid;
import com.baru.survivor.frontend.canvas.PheromonePainter;

public class GameUI {

	private SpriteBatch batch;
	private Grid grid;
	private PheromonePainter pherPainter;
	private Sprite night;

	private BitmapFont font;

	private Texture text;
	private TextureRegion barBg;
	private TextureRegion barHg;
	private TextureRegion barTh;
	
	public void create(State status) {

		grid = new Grid();		
		grid.fillTerrainLayers(status.getTerrainManager(), status.getTribeManager());
		grid.fillAgentVisuals(status.getAgentsManager());
		
		pherPainter = new PheromonePainter();
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 10;
		parameter.color = Color.WHITE;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 1;
		parameter.borderStraight = true;
		font = generator.generateFont(parameter);
		generator.dispose();
		text = new Texture(Gdx.files.internal(Survivor.spriteSheet));
		barBg = new TextureRegion(text, 32, 896, 32, 10);
		barHg = new TextureRegion(text, 0, 896, 32, 5);
		barTh = new TextureRegion(text, 0, 901, 32, 5);
		
		
		Pixmap nightPm = new Pixmap(Survivor.width * Survivor.tileSize, Survivor.height * Survivor.tileSize, Pixmap.Format.RGBA8888);
		nightPm.setColor(0.3f, 0.25f, 0.34f, 1.0f);
		nightPm.fill();
		night = new Sprite(new Texture(nightPm));

		batch = new SpriteBatch();
	}

	public void render(State status) {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		grid.updateLayer(status.getAgentsManager(), status.getResourceManager(), status.getPheromones());
		grid.draw(batch);
		if (status.getCycle() == DayCycle.NIGHT){
			night.draw(batch, 0.5f);			
		}
		if (Survivor.renderPheromones){
			Pheromones updatePheromones = status.getPheromones();
			pherPainter.update(updatePheromones);
			pherPainter.draw(batch);
			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
				// FIXME: It will be 'painted' according altruistic intensity
				font.draw(batch, String.valueOf(updatePheromones.getIntensity(Gdx.input.getX()/Survivor.tileSize, (Gdx.input.getY()) / Survivor.tileSize, AgentBuilderType.ALTRUISTIC)),
						Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			}
		}
		/*if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			int x=Gdx.input.getX()/Survivor.tileSize;
			int y=Gdx.input.getX()/Survivor.tileSize;
			status.getTerrainManager().addMountain(x, y);
		}*/
		for (int x = 0; x < Survivor.width; x++) {
			for (int y = 0; y < Survivor.height; y++) {
				Point position = new Point(x, y);
				Agent agent = status.getAgentsManager().getAgentAt(position);
				if (agent != null) {
					drawAgentName(agent, x, y);
					drawAgentBars(agent, x, y);
				}
				Reservoir reservoir = status.getResourceManager().getReservoirAt(position);
				if (reservoir != null){
					drawResourceLimits(reservoir, x, y);
				}
			}			
		}
		for (Tribe curTribe: status.getTribeManager().getVillages()){
			drawTribeResources(curTribe);
		}
		batch.end();
	}

	private void drawResourceLimits(Reservoir reservoir, int x, int y) {
		int usesLeft = reservoir.getUsesLeft();
		if (usesLeft > 0){
		font.draw(batch, String.valueOf(usesLeft), x * Survivor.tileSize + 20,
				Gdx.graphics.getHeight() - ((y+1) * Survivor.tileSize) + 10);
		}
	}
	
	private void drawTribeResources(Tribe tribe) {
		Point tribePos = tribe.position();
		int food = tribe.getFoodVault().usedSlots();
		int water = tribe.getWaterVault().usedSlots();
		font.draw(batch, String.valueOf(food) +" F", tribePos.x * Survivor.tileSize + 20,
				Gdx.graphics.getHeight() - ((tribePos.y+1) * Survivor.tileSize));
		font.draw(batch, String.valueOf(water)+" W", tribePos.x * Survivor.tileSize + 20,
				Gdx.graphics.getHeight() - ((tribePos.y+1) * Survivor.tileSize) + 10);
	}

	private void drawAgentBars(Agent agent, int x, int y) {
		if (!agent.isDead()) {
			batch.draw(barBg, x*Survivor.tileSize, Gdx.graphics.getHeight()-(y+1)*Survivor.tileSize-10);
			batch.draw(barHg, x*Survivor.tileSize, Gdx.graphics.getHeight()-(y+1)*Survivor.tileSize-5,
					32 * agent.getHunger(), 5);
			batch.draw(barTh, x*Survivor.tileSize, Gdx.graphics.getHeight()-(y+1)*Survivor.tileSize-10,
					32 * agent.getThirst(), 5);
		}
	}

	private void drawAgentName(Agent agent, int x, int y) {
		font.draw(batch, agent.name()+"("+agent.foodStg()+"/"+agent.waterStg()+")", x * Survivor.tileSize - 20,
				Gdx.graphics.getHeight() - ((y+1) * Survivor.tileSize) + 36);
	}

	public void dispose() {
		grid.dispose();
		font.dispose();
		text.dispose();
		pherPainter.dispose();
		batch.dispose();
	}

}