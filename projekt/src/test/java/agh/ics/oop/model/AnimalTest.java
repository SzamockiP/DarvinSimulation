package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void testAnimalMoves() {
        // Given
        Vector2d startPosition = new Vector2d(2, 2);
        Animal animal = new Animal(startPosition, 100);
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
        Animal animal = new Animal(new Vector2d(2, 2), 1);
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
        Animal parent1 = new Animal(new Vector2d(2, 2), 50);
        Animal parent2 = new Animal(new Vector2d(2, 2), 50);
        
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
