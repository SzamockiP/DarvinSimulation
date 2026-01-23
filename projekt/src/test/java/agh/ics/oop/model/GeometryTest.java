package agh.ics.oop.model;

import agh.ics.oop.model.base.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeometryTest {

    @Test
    void testVectorAdd() {
        Vector2d v1 = new Vector2d(1, 2);
        Vector2d v2 = new Vector2d(2, 3);
        assertEquals(new Vector2d(3, 5), v1.add(v2));
    }
    
    @Test
    void testFollowsAndPrecedes() {
        Vector2d lowerLeft = new Vector2d(0, 0);
        Vector2d upperRight = new Vector2d(10, 10);
        
        Vector2d inside = new Vector2d(5, 5);
        Vector2d outside = new Vector2d(11, 5);
        
        assertTrue(inside.follows(lowerLeft) && inside.precedes(upperRight));
        assertFalse(outside.precedes(upperRight));
    }
    
    @Test
    void testMapDirectionRotate() {
        assertEquals(MapDirection.SOUTH, MapDirection.NORTH.rotate(MoveDirection.BACK));
        assertEquals(MapDirection.EAST, MapDirection.NORTH.rotate(MoveDirection.RIGHT));
        assertEquals(MapDirection.WEST, MapDirection.NORTH.rotate(MoveDirection.LEFT));
        assertEquals(MapDirection.NORTH, MapDirection.WEST.rotate(MoveDirection.RIGHT));
    }
}
