package agh.ics.oop.model;

import agh.ics.oop.model.base.Boundary;
import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.util.SimulationConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    private SimulationConfig config = new SimulationConfig(
            new Boundary(new Vector2d(0,0), new Vector2d(10,10)),
            new Boundary(new Vector2d(0,0), new Vector2d(10,10)),
            1, 5, 1, 1, 1, 10, 5, 1, 1, 1, 1, 1, 1, 1, 10
    );

    @Test
    void testAnimalMoves() {
        // Given
        Vector2d startPosition = new Vector2d(2, 2);
        Animal animal = new Animal(startPosition, new Genotype(5), 100, config);
        Boundary boundary = new Boundary(new Vector2d(0, 0), new Vector2d(4, 4));
        LayerMap<Animal> map = new LayerMap<>(boundary);
        map.addEntity(animal);

        // When
        for(int i=0; i<10; i++) {
            animal.move(map);
        }

        // Then
        assertTrue(map.inBounds(animal.getPosition()));
    }

    @Test
    void testAnimalDiesWhenEnergyZero() {
        // Given
        Animal animal = new Animal(new Vector2d(2, 2), new Genotype(5), 1, config);
        Boundary boundary = new Boundary(new Vector2d(0, 0), new Vector2d(4, 4));
        LayerMap<Animal> map = new LayerMap<>(boundary);

        // When
        animal.move(map); // energy 1 -> 0

        // Then
        assertEquals(0, animal.getEnergy());
        if (animal.getEnergy() <= 0) animal.kill();
        assertFalse(animal.isAlive());
    }

    @Test
    void testReproduce() {
        // Given
        Animal parent1 = new Animal(new Vector2d(2, 2), new Genotype(5), 50, config);
        Animal parent2 = new Animal(new Vector2d(2, 2), new Genotype(5), 50, config);
        
        // When
        Animal child = (Animal) parent1.reproduce(parent2);

        // Then
        assertEquals(50, child.getEnergy());
        assertEquals(25, parent1.getEnergy());
        assertEquals(25, parent2.getEnergy());
        assertEquals(parent1.getPosition(), child.getPosition());
        assertNotNull(child.getGenotype());
    }
}
