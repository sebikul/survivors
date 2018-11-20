package ar.edu.itba.sma.survivors.backend.pheromones;


import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;
import ar.edu.itba.sma.survivors.backend.agents.AgentType;

class Pheromone {

    private float selfishIntensity;
    private float altruisticIntensity;

    Pheromone(float initialIntensity) {
        this.selfishIntensity = initialIntensity;
        this.altruisticIntensity = initialIntensity;
    }

    float getIntensity(AgentBuilderType agentType) {
        switch (agentType) {
            case SELFISH:
                return selfishIntensity;
            default:
                return altruisticIntensity;
        }
    }

    void addIntensity(AgentBuilderType agentType, float newIntensity, float stepPheromone, float maxPheromones, AgentType type) {
        switch (agentType) {
            case SELFISH:
                addSelfishIntensity(stepPheromone, maxPheromones, newIntensity, type);
                break;
            case ALTRUISTIC:
                addAltruisticIntensity(stepPheromone, maxPheromones, newIntensity, type);
                break;
        }
    }

    private void addSelfishIntensity(float stepPheromone, float maxPheromones, float newIntensity, AgentType type) {

        if (type == AgentType.EXPLORER) {
            this.selfishIntensity = maxPheromones;
        } else {
            this.selfishIntensity =
                    (this.selfishIntensity + stepPheromone > maxPheromones) ?
                            maxPheromones :
                            this.selfishIntensity + (stepPheromone * newIntensity);

        }
    }

    private void addAltruisticIntensity(float stepPheromone, float maxPheromones, float newIntensity, AgentType type) {

        if (type == AgentType.EXPLORER) {
            this.altruisticIntensity = maxPheromones;
        } else {
            this.altruisticIntensity =
                    (this.altruisticIntensity + stepPheromone > maxPheromones) ?
                            maxPheromones :
                            this.altruisticIntensity + (stepPheromone * newIntensity);
        }
    }

    void evaporateIntensity(float pheromoneLoss, float minPheromones) {
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
