package com.baru.survivor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.baru.survivor.Survivor;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Survivor";
		config.width = 48 * 32;
		config.height = 25 * 32;
		config.resizable = false;

		setProperties();
		
		new LwjglApplication(new Survivor(), config);
	}

	private static void setProperties() {
		// Pheromones
		System.setProperty("minPheromones", "0.0001f");
		System.setProperty("maxPheromones", "1f");
		System.setProperty("stepPheromone", "0.2f");
		System.setProperty("pheromoneLoss", "0.006f");
		System.setProperty("interestCoeff", "20f");
		System.setProperty("pheromoneCoeff", "1f");
		System.setProperty("nightMultiplier", "5f");
	}
}
