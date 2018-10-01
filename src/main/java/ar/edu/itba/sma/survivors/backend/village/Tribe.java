package ar.edu.itba.sma.survivors.backend.village;

import ar.edu.itba.sma.survivors.Survivor;
import ar.edu.itba.sma.survivors.backend.agents.Agent;
import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;
import ar.edu.itba.sma.survivors.backend.agents.Bag;
import ar.edu.itba.sma.survivors.backend.resources.Resource;
import ar.edu.itba.sma.survivors.backend.resources.ResourceType;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tribe implements Serializable {

    private List<Agent> members = new ArrayList<>();
    private Bag villageFoodVault = new Bag(Survivor.villageSlots);
    private Bag villageWaterVault = new Bag(Survivor.villageSlots);
    private Point villageLocation;
    private AgentBuilderType type;

    public Tribe(Point villageLocation, AgentBuilderType type) {
        this.villageLocation = villageLocation;
        this.type = type;
    }

    public Bag getWaterVault() {
        return villageWaterVault;
    }

    public Bag getFoodVault() {
        return villageFoodVault;
    }

    public void addMember(Agent agent) {
        members.add(agent);
    }

    public Point position() {
        return villageLocation;
    }

    public void depositResource(Resource resource) {
        if (resource.getResourceType() == ResourceType.FOOD && villageFoodVault.isEmpty()) {
            villageFoodVault.addresource(resource);
        } else if (resource.getResourceType() == ResourceType.WATER && villageWaterVault.isEmpty()) {
            villageWaterVault.addresource(resource);
        }
    }

    public Resource getResource(ResourceType type) {
        if (type == ResourceType.FOOD) {
            return villageFoodVault.getResource();
        } else {
            return villageWaterVault.getResource();
        }
    }

    public List<Agent> getAgents() {
        return members;
    }

    public boolean hasNeededResource(Agent agent) {
        if (agent.stillNeedsFood() && villageFoodVault.usedSlots() > 0) {
            return true;
        }
        return agent.stillNeedsWater() && villageWaterVault.usedSlots() > 0;
    }

    public AgentBuilderType getType() {
        return type;
    }
}
