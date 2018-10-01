package com.baru.survivor.backend.agents;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baru.survivor.backend.map.TerrainManager;
import com.baru.survivor.backend.pheromones.Pheromones;
import com.baru.survivor.backend.resources.ReservoirManager;
import com.baru.survivor.backend.village.Tribe;

public class AgentManager implements Serializable{

	private List<Agent> agents = new ArrayList<Agent>();
	private Map<Agent, Tribe> tribes = new HashMap<Agent, Tribe>();

	public void tickTime(TerrainManager terrainManager, ReservoirManager reservoirManager, DayCycle cycle, Pheromones pheromones) {
		for (Agent agent: agents){
			if (!agent.isDead()){
				Point positionBeforeTurn = agent.position();
				Tribe tribe = tribes.get(agent);
				Point tribePosition = tribe.position();
				agent.addHungerThirst();
				if (cycle == DayCycle.NIGHT){
					if (agent.position().equals(tribePosition)){
						agent.pickUpFromTribeBag(tribe);
					}else{
						if (agent.getGoalState() != Status.NEST_NO_PHEROMONE){
							agent.setGoalPoint(tribePosition, Status.NEST_NO_PHEROMONE);
							agent.cleanPath();
						}
						agent.move(terrainManager, this, tribePosition, pheromones);
						if (agent.position().equals(tribePosition)){
							agent.depositInTribeBag(tribe);
						}
					}
				}else{
					if (agent.isDying()){
						if (agent.position().equals(tribePosition)){
							agent.pickUpFromTribeBag(tribe);
							if (!tribe.hasNeededResource(agent)){
								agent.setGoalPoint(null, Status.SEARCH_RESOURCE);
							}
						}else if (agent.getGoalState() != Status.NEST_NO_PHEROMONE){
							agent.setGoalPoint(tribePosition, Status.NEST_NO_PHEROMONE);
							agent.cleanPath();
						}						
					}else if (agent.getGoalState() == Status.GRAB_RESOURCE && agent.position().equals(agent.getGoalPoint())){
						agent.pickUp(reservoirManager);
						agent.setGoalPoint(tribePosition, Status.NEST_PHEROMONE);
						agent.cleanPath();
					}else if (agent.position().equals(tribePosition)){
						agent.depositInTribeBag(tribe);
						agent.pickUpFromTribeBag(tribe);
						agent.setGoalPoint(null, Status.SEARCH_RESOURCE);
					}else if (agent.getGoalState() == Status.SEARCH_RESOURCE){
						Point reservoirNearby = reservoirManager.getReservoirInRange(agent.position(), agent.getVision());
						if (reservoirNearby != null){
							agent.setGoalPoint(reservoirNearby, Status.GRAB_RESOURCE);							
						}
					}
					agent.move(terrainManager, this, tribePosition, pheromones);
				}
				agent.consumeFromBags();
				if (agent.getGoalState() == Status.NEST_PHEROMONE && 
						!agent.position().equals(tribePosition) &&
						(!positionBeforeTurn.equals(agent.position()))){
					pheromones.addPheromone(agent.position().x, agent.position().y, agent.getPheromoneIntensity(pheromones), agent.getType());
				}
			}
		}
		pheromones.evaporatePheromones(cycle);
	}
	
	public List<Agent> getAgents() {
		return agents;
	}

	public void generateAgent(Point position, Tribe tribe) {
		Agent agent = new Agent(position, tribe.getType());
		agents.add(agent);
		tribes.put(agent, tribe);
		tribe.addMember(agent);
	}
	
	public Agent getAgentAt(Point position) {
		for (Agent agent: agents) {
			if (agent.position().equals(position)) {
				return agent;
			}
		}
		return null;
	}

	public boolean noAgentsAt(Point newPosition) {
		for (Agent agent:agents){
			if (agent.position().equals(newPosition)){
				return false;
			}
		}
		return true;
	}
	
}
