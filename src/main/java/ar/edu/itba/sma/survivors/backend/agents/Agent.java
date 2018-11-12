package ar.edu.itba.sma.survivors.backend.agents;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.map.TerrainManager;
import ar.edu.itba.sma.survivors.backend.pheromones.Pheromones;
import ar.edu.itba.sma.survivors.backend.resources.Reservoir;
import ar.edu.itba.sma.survivors.backend.resources.ReservoirManager;
import ar.edu.itba.sma.survivors.backend.resources.Resource;
import ar.edu.itba.sma.survivors.backend.resources.ResourceType;
import ar.edu.itba.sma.survivors.backend.village.Tribe;

import java.awt.*;
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

    Agent(int agentIndex, Point position, Tribe tribe) {
        Random rand = new Random();

        this.agentType = agentIndex % 2 == 0 ? AgentType.GRABBER : AgentType.EXPLORER;

        this.foodBag = new Bag(Survivor.agentSlots);
        this.waterBag = new Bag(Survivor.agentSlots);
        this.hunger = 1;
        this.path = new LinkedList<>();
        this.path.add(position);
        this.thirst = 1;
        this.visionRange = agentType == AgentType.EXPLORER ? 4 : 2;
        this.position = position;
        this.type = tribe.getType();

        if (this.type == AgentBuilderType.SELFISH) {
            this.kindness = rand.nextFloat() * 0.5f;
        } else {
            this.kindness = rand.nextFloat() * (1 - 0.5f) + 0.5f;
            ;
        }
        this.name = String.format("T(%s,%s)-%s (%s)", tribe.position().getX(), tribe.position().getY(), agentIndex, type.name().charAt(0));

//        try {
//            ng = new NameGenerator(Gdx.files.internal("assets/roman.syl").reader(15));
//            name = ng.compose(3);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public CharSequence name() {
        return name;
    }

    void consumeFromBags() {
        if (isHungry()) {
            consumeFromFoodBag();
        }
        if (isThirsty()) {
            consumeFromWaterBag();
        }
    }

    private boolean isNeeded(float need) {
        if (kindness > 0.75) {
            return need < 0.2;
        } else if (kindness > 0.5) {
            return need < 0.4;
        } else if (kindness > 0.25) {
            return need < 0.6;
        } else {
            return need < 0.8;
        }
    }

    private boolean isHungry() {
        return isNeeded(hunger);
    }

    private boolean isThirsty() {
        return isNeeded(thirst);
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

    void addHungerThirst() {
        float amountToReduce = 0.5f / (Survivor.secondsPerDay * (1000.0f / Survivor.tickTime));
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

    void move(TerrainManager terrainManager, AgentManager agentManager, Point tribePosition, Pheromones pheromones) {
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
            Direction dir = pheromones.getDirFrom(type, agentManager, tribePosition, terrainManager, position, lastPosition, goal, agentType);
            if (dir != null) {
                Point newPosition = new Point(position.x + dir.getX(), position.y + dir.getY());
                lastPosition = position;
                position = new Point(newPosition);
                path.add(0, newPosition);
            }
        }
    }

    boolean pickUp(ReservoirManager reservoirManager) {
        Reservoir reservoir = reservoirManager.getReservoirAt(position);
        reservoir.stepped();
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
                if (this.type == AgentBuilderType.ALTRUISTIC) {
                    if (isHungry()) {
                        reservoir.getResource();
                        hunger += 0.2;
                    } else {
                        return;
                    }
                } else {
                    if (hunger < 0.8) {
                        reservoir.getResource();
                        hunger += 0.2;
                    } else {
                        return;
                    }
                }
            }
            if (type == ResourceType.WATER) {
                if (this.type == AgentBuilderType.ALTRUISTIC) {
                    if (isThirsty()) {
                        reservoir.getResource();
                        thirst += 0.2;
                    } else {
                        return;
                    }
                } else {
                    if (thirst < 0.8) {
                        reservoir.getResource();
                        thirst += 0.2;
                    } else {
                        return;
                    }
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

    AgentBuilderType getType() {
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

    void depositInTribeBag(Tribe tribe) {
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

    private float getPercentageToDeposit() {
        if (this.type == AgentBuilderType.ALTRUISTIC) {
            return 1;
        } else if (kindness > 0.25f) {
            return 0.5f;
        } else {
            return 0;
        }
    }

    void pickUpFromTribeBag(Tribe tribe) {
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

    int getVision() {
        return visionRange;
    }

    void setGoalPoint(Point goalPoint, Status status) {
        goal = goalPoint;
        goalState = status;
        pathSize = 1f / path.size();
    }

    Status getGoalState() {
        return goalState;
    }

    void cleanPath() {
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

    Point getGoalPoint() {
        return goal;
    }

    public boolean stillNeedsFood() {
        return hunger < 0.5;
    }

    public boolean stillNeedsWater() {
        return thirst < 0.5;
    }

    private boolean isDyingFromHunger() {
        double amountToReduce = 1.0f / (Survivor.secondsPerDay * (1000.0 / Survivor.tickTime));
        return hunger - path.size() * amountToReduce + foodBag.usedSlots() * amountToReduce <= 0;
    }

    public boolean isDyingFromThirst() {
        double amountToReduce = 1.0f / (Survivor.secondsPerDay * (1000.0 / Survivor.tickTime));
        return thirst - path.size() * amountToReduce + waterBag.usedSlots() * amountToReduce <= 0;
    }

    boolean isDying() {
        return isDyingFromHunger() || isDyingFromThirst();
    }

    float getPheromoneIntensity(Pheromones pheromones) {

        float multiplier = this.isHungry() || this.isThirsty() ? 1.5f : 1;

//        if (this.agentType == AgentType.EXPLORER) {
//            multiplier = 3.0f;
//        }

        if (Survivor.pheromoneDistanceLossOn) {
            return pathSize * 15 * kindness * multiplier;
        } else {
            return pheromones.getPhereomoneConstant() * kindness * multiplier;
        }
    }

    AgentType getAgentType() {
        return agentType;
    }
}
