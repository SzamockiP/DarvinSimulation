package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParasiteTest {
    
    private SimulationConfig config;
    private LayerMap<Parasite> map;
    private Boundary boundary;

    @BeforeEach
    void setUp() {
         boundary = new Boundary(new Vector2d(0, 0), new Vector2d(10, 10));
         map = new LayerMap<>(boundary);
         config = new SimulationConfig(
             boundary,
             boundary,
             1, 1, 1, 1, 1, 
             20, // starting energy
             5, // genome len
             5, // energy loss parasite
             2 // energy loss panic
         );
    }

    @Test
    void testParasiteLossesEnergyInPanic() {
        // Given
        Parasite parasite = new Parasite(new Vector2d(5, 5), new Genotype(5), config);
        map.addEntity(parasite);

        // When
        parasite.move(map);

        // Then
        assertEquals(18, parasite.getEnergy());
    }

    @Test
    void testParasiteMovesWithHost() {
        // Given
        Animal host = new Animal(new Vector2d(5, 5), 50, new Genotype(5));
        host.setDirection(MapDirection.NORTH);
        
        Parasite parasite = new Parasite(new Vector2d(5, 5), new Genotype(5), config);
        parasite.setHost(host);
        
        // When
        parasite.move(map);

        // Then
        assertEquals(new Vector2d(5, 4), parasite.getPosition());
        assertEquals(45, host.getEnergy());
    }
}
