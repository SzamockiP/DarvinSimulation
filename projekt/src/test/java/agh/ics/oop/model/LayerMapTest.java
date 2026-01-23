package agh.ics.oop.model;

import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.util.SimulationConfig;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayerMapTest {

    @Test
    void testMapUpdatesPositionOnMove() {
        // Given
        Boundary boundary = new Boundary(new Vector2d(0, 0), new Vector2d(5, 5));
        LayerMap<Animal> map = new LayerMap<>(boundary);
        
        Vector2d startPos = new Vector2d(2, 2);
        
        Animal animal = new Animal(startPos, new Genotype(5), 100, new SimulationConfig(new Boundary(new Vector2d(0,0), new Vector2d(10,10)), new Boundary(new Vector2d(0,0), new Vector2d(10,10)), 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)); 
        map.addEntity(animal);
        
        assertTrue(map.getEntitiesAt(startPos).contains(animal));
        
        // When
        DeterministicAnimal detAnimal = new DeterministicAnimal(startPos, 100);
        map.addEntity(detAnimal);
        
        map.move(detAnimal); // This will move it to (2,3)
        
        // Then
        assertFalse(map.getEntitiesAt(startPos).contains(detAnimal));
        assertTrue(map.getEntitiesAt(new Vector2d(2, 3)).contains(detAnimal));
    }
    
    // Helper class for testing
    class DeterministicAnimal extends Animal {
        public DeterministicAnimal(Vector2d position, int initialEnergy) {
            super(position, new Genotype(5), initialEnergy, new SimulationConfig(new Boundary(new Vector2d(0,0), new Vector2d(10,10)), new Boundary(new Vector2d(0,0), new Vector2d(10,10)), 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0));
            this.setDirection(MapDirection.NORTH);
        }
        
        @Override
        public void move(LayerMap map) {
            // Force move FRONT (NORTH)
            Vector2d oldPos = getPosition();
            Vector2d newPos = oldPos.add(MapDirection.NORTH.getUnitVector());
            setPosition(newPos);
        }
    }
}
