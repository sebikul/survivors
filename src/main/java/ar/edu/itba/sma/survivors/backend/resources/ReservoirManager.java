package ar.edu.itba.sma.survivors.backend.resources;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ReservoirManager implements Serializable {

    private Map<Point, Reservoir> reservoirs = new HashMap<Point, Reservoir>();

    public void generateFood(Point spawnablePoint, long curTime, int amount) {
        reservoirs.put(spawnablePoint, new Reservoir(ResourceType.FOOD, amount));
    }

    public void generateWater(Point spawnablePoint, long curTime, int amount) {
        reservoirs.put(spawnablePoint, new Reservoir(ResourceType.WATER, amount));
    }

    public Reservoir getReservoirAt(Point position) {
        if (reservoirs.containsKey(position)) {
            return reservoirs.get(position);
        } else {
            return null;
        }
    }

    public Point getReservoirInRange(Point position, int vision) {
        Point closestReservoir = null;
        for (Point reservoirPosition : reservoirs.keySet()) {
            if (reservoirPosition.x <= position.x + vision && reservoirPosition.x >= position.x - vision &&
                    reservoirPosition.y <= position.y + vision && reservoirPosition.y >= position.y - vision &&
                    reservoirs.get(reservoirPosition).hasResource()) {
                closestReservoir = reservoirPosition;
                break;
            }
        }
        return closestReservoir;
    }

}