package agh.ics.oop.model.interfaces;

import agh.ics.oop.model.map.WorldMap;

public interface IMapChangeListener {
    void mapChanged(WorldMap worldMap, String message);
}
