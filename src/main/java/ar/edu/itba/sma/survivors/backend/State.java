package ar.edu.itba.sma.survivors.backend;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;
import ar.edu.itba.sma.survivors.backend.agents.AgentManager;
import ar.edu.itba.sma.survivors.backend.agents.DayCycle;
import ar.edu.itba.sma.survivors.backend.log.Log;
import ar.edu.itba.sma.survivors.backend.map.TerrainGenerator;
import ar.edu.itba.sma.survivors.backend.map.TerrainManager;
import ar.edu.itba.sma.survivors.backend.pheromones.Pheromones;
import ar.edu.itba.sma.survivors.backend.resources.ReservoirManager;
import ar.edu.itba.sma.survivors.backend.village.Tribe;
import ar.edu.itba.sma.survivors.backend.village.TribeManager;

import java.awt.*;
import java.io.Serializable;

public class State implements Serializable {

    private TerrainManager terrainManager = new TerrainManager();
    private AgentManager agentManager = new AgentManager();
    private ReservoirManager resourceManager = new ReservoirManager();
    private TribeManager tribeManager = new TribeManager();
    private DayCycle cycle = DayCycle.DAY;
    private int tickCounter = 0;
    private long lastTick = System.currentTimeMillis();
    private transient Pheromones pheromones = new Pheromones(Survivor.width, Survivor.height);
    private transient Log log;

    public State(int tribesNum, int villagersPerTribe, int foodNum, int foodDur, int lakeNum, int lakeDur) {
        generateMap();
        generateFood(foodNum, foodDur);
        generateWater(lakeNum, lakeDur);
        generateTribes(tribesNum, villagersPerTribe);
        resetLog();
    }

    public void nextState(long curTime) {
        long elapsedTime = curTime - lastTick;
        if (elapsedTime > Survivor.tickTime) {
            if (tickCounter >= Survivor.dayTicks * 0.75) {
                cycle = DayCycle.NIGHT;
                if (tickCounter > Survivor.dayTicks) {
                    cycle = DayCycle.DAY;
                    tickCounter = 0;
                }
            }
            agentManager.tickTime(terrainManager, resourceManager, cycle, pheromones);
            log.tick(tribeManager, resourceManager);
            lastTick = curTime;
            tickCounter++;
        }
    }

    public void resetLog() {
        log = new Log(tribeManager);
    }

    public void resetPheromones() {
        pheromones = new Pheromones(Survivor.width, Survivor.height);
    }

    public Log getLog() {
        return log;
    }

    public void generateMap() {
        terrainManager = TerrainGenerator.generateMap();
    }

    public TerrainManager getTerrainManager() {
        return terrainManager;
    }

    public DayCycle getCycle() {
        return cycle;
    }

    private void generateTribes(int tribesNum, int villagersPerTribe) {
        for (int i = 0; i < tribesNum; i++) {
            boolean validLocation = false;
            Point tribeLocation = null;
            while (!validLocation) {
                tribeLocation = terrainManager.getSpawnablePoint();
                if (resourceManager.getReservoirAt(tribeLocation) == null) {
                    validLocation = true;
                }
            }
            Tribe tribe = new Tribe(tribeLocation, i % 2 == 0 ? AgentBuilderType.ALTRUISTIC : AgentBuilderType.SELFISH);
            tribeManager.addTribe(tribe);
            for (int j = 0; j < villagersPerTribe; j++) {
                agentManager.generateAgent(tribeLocation, tribe);
            }
        }
    }

    private void generateWater(int lakeNum, int lakeDur) {
        long curTime = System.currentTimeMillis();
        for (int i = 0; i < lakeNum; i++) {
            resourceManager.generateWater(terrainManager.getSpawnablePoint(), curTime, lakeDur);
        }
    }

    private void generateFood(int foodNum, int foodDur) {
        long curTime = System.currentTimeMillis();
        for (int i = 0; i < foodNum; i++) {
            resourceManager.generateFood(terrainManager.getSpawnablePoint(), curTime, foodDur);
        }
    }

    public AgentManager getAgentsManager() {
        return agentManager;
    }

    public ReservoirManager getResourceManager() {
        return resourceManager;
    }

    public TribeManager getTribeManager() {
        return tribeManager;
    }

    public Pheromones getPheromones() {
        return pheromones;
    }

}
