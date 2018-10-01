package com.baru.survivor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.baru.survivor.backend.State;
import com.baru.survivor.frontend.GameUI;
import com.baru.survivor.frontend.MenuUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public class Survivor extends ApplicationAdapter{

	public static final int width = 48;
	public static final int height = 25;
	public static final int tileSize = 32;
	public static final int tickTime = 100;
	public static final int secondsPerDay = 24;
	public static final String spriteSheet = "spriteSheet-big.png";
	public static final int dayTicks = (1000/Survivor.tickTime) * Survivor.secondsPerDay;
	public static final boolean pheromoneDistanceLossOn = true;
	public static final boolean pathBlockingDisabled = true;
	public static final boolean renderPheromones = true;
	public static int agentSlots = 3;
	public static int villageSlots = 50000;
	
	private static State state;
	private Status status = Status.RUN;
	private static int inMenu = 1;
	private GameUI ui;
	private MenuUI menuUi;
	
	@Override
	public void create() {
		menuUi = new MenuUI();
		menuUi.create();	
	}

	public void dispose(){
		if (ui != null){
			ui.dispose();			
		}
		menuUi.dispose();
	}
	
	@Override
	public void render() {
		if (inMenu == 0){
			ui = new GameUI();
			ui.create(state);
			inMenu--;
		}else if (inMenu == 1){
			menuUi.render();
		}else{
			if(Gdx.input.isKeyPressed(Input.Keys.F1)){
	            status = Status.RUN;
	        }else if(Gdx.input.isKeyPressed(Input.Keys.F2)){
	            status = Status.STEP;
	        }else if(Gdx.input.isKeyPressed(Input.Keys.F3)){
	        	status = Status.PAUSE;
	        }else if(Gdx.input.isKeyPressed(Input.Keys.F4)){
	        	inMenu = 1;
	        	state.getLog().closeFiles();
	        }
			if (status != Status.PAUSE){
				state.nextState(System.currentTimeMillis());
				ui.render(state);
				if (status == Status.STEP){
					status = Status.PAUSE;
				}
			}	
		}			
	}
	

	private enum Status{
		PAUSE,
		STEP,
		RUN
	}


	public static void setState(State state) {
		Survivor.state = state;
		Survivor.inMenu = 0;
	}
}