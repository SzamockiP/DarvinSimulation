package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;

import java.util.*;

public class LayerMap<T extends Entity> {
    private final Boundary boundary;
    private final Map<Vector2d, List<T>> entitiesByPosition = new HashMap<>();


    public LayerMap(Boundary boundary) {
        this.boundary = boundary;
        for(int y = 0; y < boundary.upperRight().getY(); y++) {
            for (int x = 0; x < boundary.upperRight().getX(); x++) {
                entitiesByPosition.put(new Vector2d(x, y), new ArrayList<>());
            }
        }
    }

    public void addEntity(T entity) {
        entitiesByPosition.get(entity.getPosition()).add(entity);
    }
    
    public void removeEntity(T entity){
         entitiesByPosition.get(entity.getPosition()).remove(entity);
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

    public boolean inBounds(Vector2d position) {
        // Sprawdzamy tylko, czy nie wychodzimy poza wymiary mapy
        return position.follows(boundary.lowerLeft())
                && position.precedes(boundary.upperRight());
    }

    public void move(Creature creature) {
        Vector2d oldPosition = creature.getPosition();
        // Zakładamy, że zwierze może się tu poruszyć
        creature.move(this);

        if(!oldPosition.equals(creature.getPosition())) {
            // Need cast because method expects T but we have Creature
            // Ideally LayerMap should take Creature if we call move on it, or Creature should be T
            // This casts assumes the Creature is of type T.
            try {
                T entity = (T) creature;
                entitiesByPosition.get(oldPosition).remove(entity);
                entitiesByPosition.get(creature.getPosition()).add(entity);
            } catch (ClassCastException e) {
                // Ignore if creature is not T? Should not happen in correct usage
            }
        }
    }
}
