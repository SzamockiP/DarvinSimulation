package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;

import java.util.*;
import java.util.stream.Collectors;

public class WorldMap<T extends Entity> {
    Boundary boundary;
    private final Map<Vector2d, List<T>> entitiesByPosition = new HashMap<Vector2d, List<T>>();

    public WorldMap(int width, int height) {
        this.boundary = new Boundary(new Vector2d(0, 0), new Vector2d(width, height));
    }

    public Collection<T> getEntities() {
        // Spłaszczamy mapę: bierzemy wszystkie listy i łączymy w jedną dużą listę
        List<T> allEntities = new ArrayList<>();
        for (List<T> list : entitiesByPosition.values()) {
            allEntities.addAll(list);
        }
        return allEntities;
    }

    public List<T> getEntitiesAt(Vector2d position) {
        // Zwracamy listę z mapy lub pustą listę, jeśli nic tam nie ma (żeby uniknąć null)
        return entitiesByPosition.getOrDefault(position, new ArrayList<>());
    }

    public boolean isOccupied(Vector2d position) {
        return entitiesByPosition.containsKey(position) && !entitiesByPosition.get(position).isEmpty();
    }

    public Boundary getCurrentBoundary() {
        return boundary;
    }

    public boolean canMoveTo(Vector2d position) {
        // Sprawdzamy tylko, czy nie wychodzimy poza wymiary mapy
        return position.follows(boundary.lowerLeft())
                && position.precedes(boundary.upperRight());
    }

}
