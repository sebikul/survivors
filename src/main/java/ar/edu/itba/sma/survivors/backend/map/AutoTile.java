package ar.edu.itba.sma.survivors.backend.map;

import ar.edu.itba.sma.survivors.Survivor;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AutoTile implements Serializable {

	HashMap<Point, Byte> updateTiles = new HashMap<Point, Byte>();
	private byte[][] bitStatus;
	
	public AutoTile() {
        bitStatus = new byte[Survivor.width][Survivor.height];
        for (int i = 0; i < Survivor.width; i++) {
            for (int j = 0; j < Survivor.height; j++) {
				bitStatus[i][j] = 15;
			}
		}
	}
	
	public void addTile(int x, int y){
		bitStatus[x][y] = 0;
		updateNearbyBytes(x, y);
	}
	
	public Map<Point, Byte> getUpdateTiles(){
		return updateTiles;		
	}
	
	public void updateNearbyBytes(int x, int y) {		
		//Neighbours
		if (validPoint(x-1, y)){
			bitStatus[x-1][y] &= 5;
			updateTiles.put(new Point(x-1,  y), bitStatus[x-1][y]);
		}
		if (validPoint(x, y-1)){
			bitStatus[x][y-1] &= 3;
			updateTiles.put(new Point(x,  y-1), bitStatus[x][y-1]);
		}
		if (validPoint(x+1, y)){
			bitStatus[x+1][y] &= 10;
			updateTiles.put(new Point(x+1,  y), bitStatus[x+1][y]);
		}
		if (validPoint(x, y+1)){
			bitStatus[x][y+1] &= 12;
			updateTiles.put(new Point(x,  y+1), bitStatus[x][y+1]);
		}
		//Corners
		if (validPoint(x-1, y-1)){
			bitStatus[x-1][y-1] &= 7;	
			updateTiles.put(new Point(x-1,  y-1), bitStatus[x-1][y-1]);
		}
		if (validPoint(x+1, y-1)){
			bitStatus[x+1][y-1] &= 11;
			updateTiles.put(new Point(x+1,  y-1), bitStatus[x+1][y-1]);
		}
		if (validPoint(x-1, y+1)){
            bitStatus[x - 1][y + 1] &= 13;
			updateTiles.put(new Point(x-1,  y+1), bitStatus[x-1][y+1]);
		}
		if (validPoint(x+1, y+1)){
            bitStatus[x + 1][y + 1] &= 14;
			updateTiles.put(new Point(x+1,  y+1), bitStatus[x+1][y+1]);
		}
	}

	private boolean validPoint(int x, int y) {
        return (x >= 0 && x < Survivor.width) && (y >= 0 && y < Survivor.height);
	}

	public byte get(int x, int y) {
		return bitStatus[x][y];
	}
	
}
