package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    private WorldMap map;
    private SimulationConfig config;

    @BeforeEach
    void setUp() {
        Boundary boundary = new Boundary(new Vector2d(0, 0), new Vector2d(10, 10));
        map = new WorldMap(boundary);
        config = new SimulationConfig(
                boundary,
                boundary,
                1, 
                5, // energyPerPlant
                1, 1, 1, 
                10, // startingEnergy
                5, 1, 1
        );
    }

    @Test
    void testFeedAnimalsManager() {
        // Given
        FeedAnimalsManager manager = new FeedAnimalsManager();
        Vector2d pos = new Vector2d(2, 2);
        
        Animal animal = new Animal(pos, 10, new Genotype(5));
        Plant plant = new Plant(pos);
        
        map.getAnimals().addEntity(animal);
        map.getPlants().addEntity(plant);

        // When
        manager.step(map, config);

        // Then
        assertEquals(15, animal.getEnergy());
        assertTrue(map.getPlants().getEntitiesAt(pos).isEmpty());
    }

    @Test
    void testKillAnimalsManager() {
        // Given
        KillAnimalsManager manager = new KillAnimalsManager();
        Vector2d pos = new Vector2d(3, 3);
        
        Animal deadAnimal = new Animal(pos, 10, new Genotype(5));
        deadAnimal.kill();
        
        Animal liveAnimal = new Animal(pos, 10, new Genotype(5));
        
        map.getAnimals().addEntity(deadAnimal);
        map.getAnimals().addEntity(liveAnimal);

        // When
        manager.step(map, config);

        // Then
        assertFalse(map.getAnimals().getEntities().contains(deadAnimal));
        assertTrue(map.getDeadAnimals().contains(deadAnimal));
        assertTrue(map.getAnimals().getEntities().contains(liveAnimal));
    }
    @Test
    void testSetHostsManager() {
        // Given
        SetHostsManager manager = new SetHostsManager();
        Vector2d pos = new Vector2d(1, 1);
        
        Animal animal = new Animal(pos, 100);
        Parasite parasite = new Parasite(pos, new Genotype(5), config);
        
        map.getAnimals().addEntity(animal);
        map.getParasites().addEntity(parasite);
        
        // When
        manager.step(map, config);
        
        // Then
        assertEquals(animal, parasite.getHost());

        Vector2d otherPos = new Vector2d(2,2);
        Parasite otherParasite = new Parasite(otherPos, new Genotype(5), config);
        map.getParasites().addEntity(otherParasite);
        manager.step(map, config);
        assertNull(otherParasite.getHost());
    }
    
    @Test
    void testReproduceAnimalsManager() {
        // Given
        ReproduceAnimalsManager manager = new ReproduceAnimalsManager();
        Vector2d pos = new Vector2d(5, 5);
        
        Animal strong = new Animal(pos, 100);
        Animal weak = new Animal(pos, 10);
        Animal medium = new Animal(pos, 50);
        
        map.getAnimals().addEntity(strong);
        map.getAnimals().addEntity(weak); 
        map.getAnimals().addEntity(medium); 
        
        // When
        manager.step(map, config);
        
        // Then
        assertEquals(4, map.getAnimals().getEntities().size());
        
        assertEquals(50, strong.getEnergy());
        assertEquals(25, medium.getEnergy());
        assertEquals(10, weak.getEnergy());
    }
}
