package ar.edu.itba.sma.survivors.backend.pheromones;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;
import ar.edu.itba.sma.survivors.backend.agents.AgentManager;
import ar.edu.itba.sma.survivors.backend.agents.DayCycle;
import ar.edu.itba.sma.survivors.backend.agents.Direction;
import ar.edu.itba.sma.survivors.backend.map.TerrainManager;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Pheromones implements Serializable {

    private final float minPheromones = Float.parseFloat(System.getProperty("minPheromones"));
    private final float maxPheromones = Float.parseFloat(System.getProperty("maxPheromones"));
    private final float stepPheromone = Float.parseFloat(System.getProperty("stepPheromone"));
    private final float pheromoneLoss = Float.parseFloat(System.getProperty("pheromoneLoss"));
    private final float interestCoeff = Float.parseFloat(System.getProperty("interestCoeff"));
    private final float pheromoneCoeff = Float.parseFloat(System.getProperty("pheromoneCoeff"));
    private final float nightMultiplier = Float.parseFloat(System.getProperty("nightMultiplier"));
    private Pheromone pheromones[][];
    private int width;
    private int height;

    public Pheromones(int width, int height) {
        this.pheromones = new Pheromone[width][height];
        this.width = width;
        this.height = height;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pheromones[x][y] = new Pheromone(minPheromones);
            }
        }
    }

    public void addPheromone(int x, int y, float intensity, AgentBuilderType agentType) {
        pheromones[x][y].addIntensity(agentType, intensity, stepPheromone, maxPheromones);
    }

    public void evaporatePheromones(DayCycle cycle) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pheromones[x][y].evaporateIntensity(pheromoneLoss * (cycle == DayCycle.NIGHT ? nightMultiplier : 1), minPheromones);
            }
        }
    }

    public Direction getDirFrom(AgentBuilderType agentType, AgentManager agentManager, Point tribePosition, TerrainManager terrainManager, Point position, Point lastPosition, Point goal) {
        Map<Direction, Double> directionsValues = new HashMap<Direction, Double>();
        double totalValue = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }


                Direction dir = Direction.valueOf(x, y);
                Point target = new Point(position.x + x, position.y + y);
                double difference;
                if (lastPosition == null) {
                    difference = 1;
                } else {
                    difference = eulerDist(terrainManager, lastPosition, target);
                }
                double interest = (goal == null) ? difference : difference / eulerDist(terrainManager, target, goal);
                if ((Survivor.pathBlockingDisabled || agentManager.noAgentsAt(target) || target.equals(tribePosition)) &&
                        TerrainManager.isValidPoint(target) && !terrainManager.isBlocked(target)) {
                    if (interest == Double.POSITIVE_INFINITY || Double.isNaN(interest)) {
                        return dir;
                    }
                    double dirValue = Math.pow(pheromones[target.x][target.y].getIntensity(agentType), pheromoneCoeff) * Math.pow(interest, interestCoeff);
                    directionsValues.put(dir, dirValue);
                    totalValue += dirValue;

                }
            }
            ;
        }

        return throwDiceForDirection(directionsValues, totalValue);
    }

    private Direction throwDiceForDirection(
            Map<Direction, Double> directionsValues, double totalValue) {

        Random rnd = new Random();
        double diceValue = rnd.nextFloat() * totalValue;
        double sum = 0;
        for (Direction dir : directionsValues.keySet()) {
            Double value = directionsValues.get(dir);
            sum += value;
            if (diceValue <= sum) {
                return dir;
            }
        }
        return null;
    }

    private double eulerDist(TerrainManager tm, Point from, Point to) {
        if (tm.isBlocked(from)) {
            return Double.MAX_VALUE;
        }
        return Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
    }

    public float getIntensity(int x, int y, AgentBuilderType agentType) {
        return pheromones[x][y].getIntensity(agentType);
    }

    public Pheromone getPheromone(int x, int y) {
        return pheromones[x][y];
    }

    public float getMaxPheromones() {
        return maxPheromones;
    }

    public float getPhereomoneConstant() {
        return stepPheromone;
    }
}
