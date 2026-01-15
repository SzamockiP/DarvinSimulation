package agh.ics.oop.model;

import agh.ics.oop.model.WorldMap;

public class Parasite extends Creature implements Movable{
    Animal host;
    boolean panicking;
    int daysWithHost;

    public Parasite(Vector2d position, int initialEnergy, int genomeSize) {
        super(position, initialEnergy);

    }

    public void setHost(Animal host) {
        this.host = host;
        this.panicking = false;
        this.daysWithHost = 0;
    }
    
    public Animal getHost() {
        return host;
    }

    @Override
    public void addEnergy(int delta){
        this.setEnergy( this.getEnergy() + delta);
    }


    @Override
    public void move(WorldMap<?> map){
        if(this.isDead()) return;

        if (this.host != null) {
            if (this.host.isDead()) { //Host umarł - zaczynamy panikować i tracimy referencję
                this.host = null;
                this.panicking = true;
            } else {
                this.daysWithHost++;
            }
        }

        if (this.host != null) {
            Vector2d hostPos = this.host.getPosition();
            int startDirectionIndex = this.host.getDirection().getValue();
            Vector2d anyValidSpot = null;

            for (int i = 0; i < 8; i++) {
                int checkIndex = (startDirectionIndex + i) % 8;

                Vector2d unitVector = MoveDirection.values()[checkIndex].toUnitVector();
                Vector2d potentialPos = hostPos.add(unitVector);

                if (map.canMoveTo(potentialPos)) {
                    // Puste miejsce
                    if (!map.isOccupied(potentialPos)) {
                        this.setPosition(potentialPos);
                        host.addEnergy(-1);
                        return;
                    }

                    // Zapisz jakiekolwiek poprawne miejsce na później
                    if (anyValidSpot == null) {
                        anyValidSpot = potentialPos;
                    }
                }
            }
            if (anyValidSpot != null) {
                this.setPosition(anyValidSpot);
            }

            host.addEnergy(-1);
        } else { // nie ma hosta
            MapDirection rotationGene = this.genotype.nextGene();

            this.setDirection(this.getDirection().rotate(rotationGene.getValue()));

            if (rotationGene.equals(MapDirection.NORTH)) {
                int directionIndex = this.getDirection().getValue(); // wartość aktualnego kierunku

                Vector2d unitVector = MoveDirection.values()[directionIndex].toUnitVector(); // wybieramy wektor, który pasuje dla tego kierunku
                Vector2d currentPos = this.getPosition();
                Vector2d newPos = currentPos.add(unitVector);

                if (map.canMoveTo(newPos)) {
                    this.setPosition(newPos);
                } else { // odbijamy od ściany
                    this.setDirection(this.getDirection().rotate(4)); // obrót

                    Vector2d bounceVector = MoveDirection.values()[this.getDirection().getValue()].toUnitVector();
                    Vector2d bouncePos = currentPos.add(bounceVector);

                    if (map.canMoveTo(bouncePos)) {
                        this.setPosition(bouncePos);
                    }
                }
            }
            if(this.panicking){
                this.addEnergy(-1);
            }
            this.addEnergy(-1);
        }
    }
}
