package com.baru.survivor.backend.village;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baru.survivor.Survivor;
import com.baru.survivor.backend.agents.Agent;
import com.baru.survivor.backend.agents.AgentBuilderType;
import com.baru.survivor.backend.agents.Bag;
import com.baru.survivor.backend.resources.Resource;
import com.baru.survivor.backend.resources.ResourceType;

public class Tribe implements Serializable{

	private List<Agent> members = new ArrayList<Agent>();
	private Bag villageFoodVault = new Bag(Survivor.villageSlots);
	private Bag villageWaterVault = new Bag(Survivor.villageSlots);
	private Point villageLocation;
	private AgentBuilderType type;

	public Tribe(Point villageLocation, AgentBuilderType type) {
		this.villageLocation = villageLocation;
		this.type = type;
	}
	
	public Bag getWaterVault(){
		return villageWaterVault;
	}
	
	public Bag getFoodVault(){
		return villageFoodVault;
	}
	
	public void addMember(Agent agent) {
		members.add(agent);
	}

	public Point position() {
		return villageLocation;
	}
	
	public void depositResource(Resource resource){
		if (resource.getResourceType() == ResourceType.FOOD && !villageFoodVault.isFull()){
			villageFoodVault.addresource(resource);
		}else if(resource.getResourceType() == ResourceType.WATER && !villageWaterVault.isFull()){
			villageWaterVault.addresource(resource);
		}
	}
	
	public Resource getResource(ResourceType type){
		if (type == ResourceType.FOOD){
			return villageFoodVault.getResource();
		}else{
			return villageWaterVault.getResource();
		}
	}

	public List<Agent> getAgents() {
		return members;
	}

	public boolean hasNeededResource(Agent agent) {
		if (agent.stillNeedsFood() && villageFoodVault.usedSlots() > 0){
			return true;
		}
		if (agent.stillNeedsWater() && villageWaterVault.usedSlots() > 0){
			return true;
		}
		return false;
	}

	public AgentBuilderType getType() {
		return type;
	}
}
