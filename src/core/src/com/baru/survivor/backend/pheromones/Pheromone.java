package com.baru.survivor.backend.pheromones;


import com.baru.survivor.backend.agents.AgentBuilderType;

public class Pheromone {

    private float selfishIntensity;
    private float altruisticIntensity;

    public Pheromone(float initialIntensity) {
        this.selfishIntensity = initialIntensity;
        this.altruisticIntensity = initialIntensity;
    }

    public float getIntensity(AgentBuilderType agentType) {
        switch (agentType) {
            case SELFISH:
                return selfishIntensity;
            default:
                return altruisticIntensity;
        }
    }

    public void addIntensity(AgentBuilderType agentType, float newIntensity, float stepPheromone, float maxPheromones) {
        switch (agentType) {
            case SELFISH:
                addSelfishIntensity(stepPheromone, maxPheromones, newIntensity);
                break;
            default:
                addSelfishIntensity(stepPheromone, maxPheromones, newIntensity);
                addAltruisticIntensity(stepPheromone, maxPheromones, newIntensity);
                break;
        }
    }

    private void addSelfishIntensity(float stepPheromone, float maxPheromones, float newIntensity) {
        this.selfishIntensity =
                (this.selfishIntensity + stepPheromone > maxPheromones) ?
                        maxPheromones :
                        this.selfishIntensity + (stepPheromone * newIntensity);
    }

    private void addAltruisticIntensity(float stepPheromone, float maxPheromones, float newIntensity) {
        this.altruisticIntensity =
                (this.altruisticIntensity + stepPheromone > maxPheromones) ?
                        maxPheromones :
                        this.altruisticIntensity + (stepPheromone * newIntensity);
    }

    public void evaporateIntensity(float pheromoneLoss, float minPheromones) {
        this.selfishIntensity =
                (this.selfishIntensity - pheromoneLoss < minPheromones) ?
                        minPheromones :
                        this.selfishIntensity - pheromoneLoss;

        this.altruisticIntensity =
                (this.altruisticIntensity - pheromoneLoss < minPheromones) ?
                        minPheromones :
                        this.altruisticIntensity - pheromoneLoss;
    }

}
