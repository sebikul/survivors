package ar.edu.itba.sma.survivors.backend.pheromones;


import ar.edu.itba.sma.survivors.backend.agents.AgentBuilderType;

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

    void addIntensity(AgentBuilderType agentType, float newIntensity, float stepPheromone, float maxPheromones) {
        switch (agentType) {
            case SELFISH:
                addSelfishIntensity(stepPheromone, maxPheromones, newIntensity);
                break;
            case ALTRUISTIC:
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
