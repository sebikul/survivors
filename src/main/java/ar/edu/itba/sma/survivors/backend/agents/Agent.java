package ar.edu.itba.sma.survivors.backend.agents;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.map.TerrainManager;
import ar.edu.itba.sma.survivors.backend.pheromones.Pheromones;
import ar.edu.itba.sma.survivors.backend.resources.Reservoir;
import ar.edu.itba.sma.survivors.backend.resources.ReservoirManager;
import ar.edu.itba.sma.survivors.backend.resources.Resource;
import ar.edu.itba.sma.survivors.backend.resources.ResourceType;
import ar.edu.itba.sma.survivors.backend.village.Tribe;
import com.badlogic.gdx.Gdx;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Agent implements Serializable {

    private String name;
    private Point position;
    private Point lastPosition;
    private List<Point> path;
    private float hunger;
    private float thirst;
    private int visionRange;
    private float kindness;
    private Bag foodBag;
    private Bag waterBag;
    private Point goal;
    private Status goalState;
    private float pathSize;
    private AgentBuilderType type;

    private AgentType agentType;

    public Agent(Point position, AgentBuilderType type) {
        Random rand = new Random();
        this.kindness = rand.nextFloat();

        this.agentType = rand.nextBoolean() ? AgentType.GRABBER : AgentType.EXPLORER;

        this.foodBag = new Bag(Survivor.agentSlots);
        this.waterBag = new Bag(Survivor.agentSlots);
        this.hunger = 1;
        this.path = new LinkedList<Point>();
        this.path.add(position);
        this.thirst = 1;
        this.visionRange = 2;
        this.position = position;
        NameGenerator ng;
        this.type = type;
        try {
            ng = new NameGenerator(Gdx.files.internal("assets/roman.syl").reader(15));
            name = ng.compose(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CharSequence name() {
        return name;
    }

    public void consumeFromBags() {
        if (isHungry()) {
            consumeFromFoodBag();
        }
        if (isThirsty()) {
            consumeFromWaterBag();
        }
    }

    public boolean isHungry() {
        if (kindness > 0.8) {
            if (hunger < 0.2) {
                return true;
            }
            return false;
        } else if (kindness > 0.4) {
            if (hunger < 0.5) {
                return true;
            }
            return false;
        } else {
            if (hunger < 0.8) {
                return true;
            }
            return false;
        }
    }

    public boolean isThirsty() {
        if (kindness > 0.8) {
            if (thirst < 0.2) {
                return true;
            }
            return false;
        } else if (kindness > 0.4) {
            if (thirst < 0.5) {
                return true;
            }
            return false;
        } else {
            if (thirst < 0.8) {
                return true;
            }
            return false;
        }
    }

    private void consumeFromFoodBag() {
        Resource resource = foodBag.getResource();
        if (resource != null) {
            hunger += 0.2;
        }
    }

    private void consumeFromWaterBag() {
        Resource resource = waterBag.getResource();
        if (resource != null) {
            thirst += 0.2;
        }
    }

    public void addHungerThirst() {
        float amountToReduce = 0.5f / (Survivor.secondsPerDay * (1000 / Survivor.tickTime));
        if (hunger > 0) {
            hunger -= amountToReduce;
            if (hunger <= 0) {
                thirst = 0;
            }
        }
        if (thirst > 0) {
            thirst -= amountToReduce;
            if (thirst <= 0) {
                hunger = 0;
            }
        }
    }

    public void move(TerrainManager terrainManager, AgentManager agentManager, Point tribePosition, Pheromones pheromones) {
        if (goalState == Status.NEST_NO_PHEROMONE || goalState == Status.NEST_PHEROMONE) {
            if (path.size() > 0) {
                Point newPosition = path.get(0);
                lastPosition = position;
                position = new Point(newPosition);
                if (!newPosition.equals(tribePosition)) {
                    path.remove(0);
                } else {
                    path.clear();
                    lastPosition = null;
                    position = tribePosition;
                    path.add(0, tribePosition);
                }
            } else {
                lastPosition = null;
                position = tribePosition;
                path.add(0, tribePosition);
            }
        } else {
            Direction dir = pheromones.getDirFrom(type, agentManager, tribePosition, terrainManager, position, lastPosition, goal);
            if (dir != null) {
                Point newPosition = new Point(position.x + dir.getX(), position.y + dir.getY());
                lastPosition = position;
                position = new Point(newPosition);
                path.add(0, newPosition);
            }
        }
    }

    public boolean pickUp(ReservoirManager reservoirManager) {
        Reservoir reservoir = reservoirManager.getReservoirAt(position);
        if (reservoir != null && reservoir.hasResource()) {
            consumeResourceTillFull(reservoir);
            fillBagWithResource(reservoir);
            return true;
        }
        return false;
    }

    private void fillBagWithResource(Reservoir reservoir) {
        Bag sameTypeBag = getSameTypeBag(reservoir);
        int resourcesToGrab = sameTypeBag.getAvailableSlots();
        while (resourcesToGrab > 0 && reservoir.hasResource()) {
            Resource resourceToGrab = reservoir.getResource();
            sameTypeBag.addresource(resourceToGrab);
            resourcesToGrab--;
        }
    }

    private void consumeResourceTillFull(Reservoir reservoir) {
        ResourceType type = reservoir.type();
        while (reservoir.hasResource()) {
            if (type == ResourceType.FOOD) {
                if (hunger < 0.8) {
                    reservoir.getResource();
                    hunger += 0.2;
                } else {
                    return;
                }
            }
            if (type == ResourceType.WATER) {
                if (thirst < 0.8) {
                    reservoir.getResource();
                    thirst += 0.2;
                } else {
                    return;
                }
            }
        }
    }

    private Bag getSameTypeBag(Reservoir reservoir) {
        if (reservoir.type() == ResourceType.FOOD) {
            return foodBag;
        } else {
            return waterBag;
        }
    }

    public Point position() {
        return position;
    }

    public float getKindness() {
        return kindness;
    }

    public void addResource(Resource resource) {
        if (resource.getResourceType() == ResourceType.FOOD) {
            foodBag.addresource(resource);
        } else {
            waterBag.addresource(resource);
        }

    }

    public Resource getFoodFromBag() {
        return foodBag.getResource();
    }

    public Resource getWaterFromBag() {
        return waterBag.getResource();
    }

    public boolean isDead() {
        return thirst <= 0 || hunger <= 0;
    }

    public float getHunger() {
        return hunger;
    }

    public float getThirst() {
        return thirst;
    }

    public AgentBuilderType getType() {
        return type;
    }

    public void removeHunger() {
        hunger = 1;
    }

    public void removeThirst() {
        thirst = 1;
    }

    public String foodStg() {
        return ((Integer) foodBag.usedSlots()).toString();
    }

    public String waterStg() {
        return ((Integer) waterBag.usedSlots()).toString();
    }

    public void depositInTribeBag(Tribe tribe) {
        int foodToShare = (int) (getPercentageToDeposit() * foodBag.usedSlots());
        int waterToShare = (int) (getPercentageToDeposit() * waterBag.usedSlots());
        while (foodToShare > 0) {
            Resource food = foodBag.getResource();
            tribe.getFoodVault().addresource(food);
            foodToShare--;
        }
        while (waterToShare > 0) {
            Resource water = waterBag.getResource();
            tribe.getWaterVault().addresource(water);
            waterToShare--;
        }
    }

    public float getPercentageToDeposit() {
        if (kindness > 0.6) {
            return 1;
        } else if (kindness > 0.3) {
            return 0.5f;
        } else {
            return 0;
        }
    }

    public void pickUpFromTribeBag(Tribe tribe) {
        if (isHungry()) {
            Resource food = tribe.getFoodVault().getResource();
            if (food != null) {
                foodBag.addresource(food);
            }
        }
        if (isThirsty()) {
            Resource water = tribe.getWaterVault().getResource();
            if (water != null) {
                waterBag.addresource(water);
            }
        }
    }

    public int getVision() {
        return visionRange;
    }

    public void setGoalPoint(Point goalPoint, Status status) {
        goal = goalPoint;
        goalState = status;
        pathSize = 1f / path.size();
    }

    public Status getGoalState() {
        return goalState;
    }

    public void cleanPath() {
        path.remove(0);
        List<Point> visited = new LinkedList<Point>();
        findRepeats(0, visited);
    }

    private void findRepeats(int curIndex, List<Point> visited) {
        if (curIndex == path.size()) {
            return;
        }
        Point curPoint = path.get(curIndex);
        if (visited.contains(curPoint)) {
            curIndex = removeUntil(curPoint, curIndex - 1, visited);
        } else {
            visited.add(curPoint);
        }
        findRepeats(curIndex + 1, visited);
    }

    private int removeUntil(Point origPoint, int i, List<Point> visited) {
        Point removedPoint = path.remove(i);
        visited.remove(removedPoint);
        if (removedPoint.equals(origPoint)) {
            return i;
        }
        return removeUntil(origPoint, i - 1, visited);
    }

    public int getPathLength() {
        return path.size();
    }

    public Point getGoalPoint() {
        return goal;
    }

    public boolean stillNeedsFood() {
        if (hunger < 0.5) {
            return true;
        }
        return false;
    }

    public boolean stillNeedsWater() {
        if (thirst < 0.5) {
            return true;
        }
        return false;
    }

    public boolean isDyingFromHunger() {
        float amountToReduce = 1.0f / (Survivor.secondsPerDay * (1000 / Survivor.tickTime));
        if (hunger - path.size() * amountToReduce + foodBag.usedSlots() * amountToReduce <= 0) {
            return true;
        }
        return false;
    }

    public boolean isDyingFromThirst() {
        float amountToReduce = 1.0f / (Survivor.secondsPerDay * (1000 / Survivor.tickTime));
        if (thirst - path.size() * amountToReduce + waterBag.usedSlots() * amountToReduce <= 0) {
            return true;
        }
        return false;
    }

    public boolean isDying() {
        return isDyingFromHunger() || isDyingFromHunger();
    }

    public float getPheromoneIntensity(Pheromones pheromones) {
        if (Survivor.pheromoneDistanceLossOn) {
            System.out.println(pathSize);
            return pathSize * 15 * kindness;
        } else {
            return pheromones.getPhereomoneConstant() * kindness;
        }
    }

    public AgentType getAgentType() {
        return agentType;
    }
}
