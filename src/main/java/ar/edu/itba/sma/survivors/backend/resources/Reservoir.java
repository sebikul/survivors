package ar.edu.itba.sma.survivors.backend.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reservoir implements Serializable {

    private List<Resource> resources;
    private ResourceType type;
    private int maxResources;
    private int timeToRegenerate = 0;

    public Reservoir(ResourceType type, int maxResources) {
        this.type = type;
        this.maxResources = maxResources;
        this.resources = new ArrayList<>(maxResources);
        addResources(maxResources);
    }

    public Resource getResource() {
        int resourcesAmount = resources.size();
        if (resourcesAmount > 0) {
            return resources.remove(resourcesAmount - 1);
        } else {
            return null;
        }
    }

    private void addResources(int amount) {
        if (amount + resources.size() > maxResources) {
            return;
        }
        while (amount > 0) {
            resources.add(new Resource(type));
            amount--;
        }
    }

    public ResourceType type() {
        return type;
    }

    public boolean hasResource() {
        return !resources.isEmpty();
    }

    public int getUsesLeft() {
        return resources.size();
    }

    public void stepped() {
        this.timeToRegenerate = 0;
    }

    public void addTickTime() {
        this.timeToRegenerate++;
        if (timeToRegenerate > 500 && timeToRegenerate % 50 == 0) {
            addResources(1);
        }
    }

}
