package ar.edu.itba.sma.survivors.backend.agents;

import ar.edu.itba.sma.survivors.backend.map.TerrainManager;
import ar.edu.itba.sma.survivors.backend.pheromones.Pheromones;
import ar.edu.itba.sma.survivors.backend.resources.ReservoirManager;
import ar.edu.itba.sma.survivors.backend.village.Tribe;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentManager implements Serializable {

    private List<Agent> agents = new ArrayList<>();
    private Map<Agent, Tribe> tribes = new HashMap<>();

    public void tickTime(TerrainManager terrainManager, ReservoirManager reservoirManager, DayCycle cycle, Pheromones pheromones) {
        for (Agent agent : agents) {
            if (!agent.isDead()) {
                // Si no se esta muriendo
                Point positionBeforeTurn = agent.position();
                Tribe tribe = tribes.get(agent);
                Point tribePosition = tribe.position();
                agent.addHungerThirst();
                if (cycle == DayCycle.NIGHT) {
                    // Si es de noche
                    if (agent.position().equals(tribePosition)) {
                        // Si está en su tribu
                        agent.pickUpFromTribeBag(tribe);
                    } else {
                        // No está en su tribu
                        if (agent.getGoalState() != Status.NEST_NO_PHEROMONE) {
                            // Volve a la tribu sin dejar feromona
                            agent.setGoalPoint(tribePosition, Status.NEST_NO_PHEROMONE);
                            agent.cleanPath();
                        }
                        agent.move(terrainManager, this, tribePosition, pheromones);
                        if (agent.position().equals(tribePosition)) {
                            // Deposita lo que tengas
                            agent.depositInTribeBag(tribe);
                        }
                    }
                } else {
                    // Si es de día
                    if (agent.isDying()) {
                        // Si se está muriendo
                        if (agent.position().equals(tribePosition)) {
                            // Si está en la tribu
                            agent.pickUpFromTribeBag(tribe);
                            if (!tribe.hasNeededResource(agent)) {
                                // Si no hay de ese recurso, anda a buscar
                                agent.setGoalPoint(null, Status.SEARCH_RESOURCE);
                            }
                        } else if (agent.getGoalState() != Status.NEST_NO_PHEROMONE) {
                            // Anda a la tribu sin dejar feromona
                            agent.setGoalPoint(tribePosition, Status.NEST_NO_PHEROMONE);
                            agent.cleanPath();
                        }
                    } else if (agent.getGoalState() == Status.GRAB_RESOURCE && agent.position().equals(agent.getGoalPoint())) {
                        // Si no se está muriendo y está agarrando recursos
                        agent.pickUp(reservoirManager);
                        // volve a la tribu dejando feromona
                        agent.setGoalPoint(tribePosition, Status.NEST_PHEROMONE);
                        agent.cleanPath();
                    } else if (agent.position().equals(tribePosition)) {
                        // Si no se esta muriendo y llegó a la tribu
                        agent.depositInTribeBag(tribe);
                        agent.pickUpFromTribeBag(tribe);
                        agent.setGoalPoint(null, Status.SEARCH_RESOURCE);
                    } else if (agent.getGoalState() == Status.SEARCH_RESOURCE) {
                        // Si no se está muriendo y tiene que ir a buscar
                        Point reservoirNearby = reservoirManager.getReservoirInRange(agent.position(), agent.getVision());
                        if (reservoirNearby != null) {

                            if (agent.getAgentType() == AgentType.EXPLORER) {
                                agent.setGoalPoint(reservoirNearby, Status.GRAB_RESOURCE);
//                                agent.cleanPath();
                            } else if (agent.getAgentType() == AgentType.GRABBER) {
                                agent.setGoalPoint(reservoirNearby, Status.GRAB_RESOURCE);
                            }

                        }
                    }
                    agent.move(terrainManager, this, tribePosition, pheromones);
                }
                agent.consumeFromBags();
                if (agent.getGoalState() == Status.NEST_PHEROMONE &&
                        !agent.position().equals(tribePosition) &&
                        (!positionBeforeTurn.equals(agent.position()))) {
                    pheromones.addPheromone(agent.position().x, agent.position().y, agent.getPheromoneIntensity(pheromones), agent.getType());
                }
            }
        }
        pheromones.evaporatePheromones(cycle);
        reservoirManager.addTime();
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void generateAgent(int agentIndex, Point position, Tribe tribe) {
        Agent agent = new Agent(agentIndex, position, tribe);
        agents.add(agent);
        tribes.put(agent, tribe);
        tribe.addMember(agent);
    }

    public Agent getAgentAt(Point position) {
        for (Agent agent : agents) {
            if (agent.position().equals(position)) {
                return agent;
            }
        }
        return null;
    }

    public boolean noAgentsAt(Point newPosition) {
        for (Agent agent : agents) {
            if (agent.position().equals(newPosition)) {
                return false;
            }
        }
        return true;
    }

}
