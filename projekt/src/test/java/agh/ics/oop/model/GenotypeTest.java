package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenotypeTest {

    @Test
    void testGenotypeSize() {
        int size = 10;
        Genotype genotype = new Genotype(size);
        assertEquals(size, genotype.getGenes().size());
    }

    @Test
    void testNextGeneCycle() {
        List<MoveDirection> genes = new ArrayList<>();
        genes.add(MoveDirection.FRONT);
        genes.add(MoveDirection.BACK);
        
        Genotype genotype = new Genotype(genes);
        
        // Starts at index 0 (default int init)
        
        assertEquals(MoveDirection.FRONT, genotype.nextGene());
        assertEquals(MoveDirection.BACK, genotype.nextGene());
        assertEquals(MoveDirection.FRONT, genotype.nextGene());
    }

    @Test
    void testCrossValidLength() {
        // Given
        int size = 8;
        Genotype g1 = new Genotype(size);
        Genotype g2 = new Genotype(size);
        
        // When
        Genotype child = g1.cross(g2, 50, 50);
        
        // Then
        assertEquals(size, child.getGenes().size());
    }
    
    @Test
    void testCrossStrongerMatchesLogic() {
        // Given
        List<MoveDirection> genes1 = new ArrayList<>();
        for(int i=0; i<10; i++) genes1.add(MoveDirection.FRONT);
        Genotype g1 = new Genotype(genes1);
        
        List<MoveDirection> genes2 = new ArrayList<>();
        for(int i=0; i<10; i++) genes2.add(MoveDirection.BACK);
        Genotype g2 = new Genotype(genes2);
        
        // When
        Genotype child = g1.cross(g2, 100, 0);

        // Then
        assertEquals(10, child.getGenes().size());
        assertNotNull(child.getGenes());
    }
}
