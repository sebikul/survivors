package ar.edu.itba.sma.survivors.backend.map;

import java.io.Serializable;


public class Tile implements Serializable {

    private boolean blocked;
    private TileType type;

    public Tile(TileType type, boolean blocked) {
        this.type = type;
        this.blocked = blocked;
    }

    boolean isBlocked() {
        return blocked;
    }

    TileType getType() {
        return type;
    }

}
