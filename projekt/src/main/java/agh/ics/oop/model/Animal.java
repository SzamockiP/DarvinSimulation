package agh.ics.oop.model;

public class Animal extends Creature implements Movable {
    int childrenAmount;
    int age;

    public Animal(Vector2d position, int initialEnergy) {
        super(position, initialEnergy);
    }

    public void makeOlder(){
        this.age++;
    }

    public void addEnergy(int delta){
        this.setEnergy( this.getEnergy() + delta);
    }


    @Override
    public void move(WorldMap map)){
        if(this.isDead()) return;
        MapDirection rotationGene = this.genotype.nextGene();

        this.direction = this.direction.rotate(rotationGene.getValue());

        if (rotationGene.equals(MapDirection.NORTH)) {
            int directionIndex = this.getDirection().getValue(); // wartość aktualnego kierunku

            Vector2d unitVector = MoveDirection.values()[directionIndex].toUnitVector(); // wybieramy wektor, który pasuje dla tego kierunku

            Vector2d currentPos = this.getPosition();
            Vector2d newPos = currentPos.add(unitVector);

            if (map.canMoveTo(newPos)) {
                this.setPosition(newPos);
            } else { // odbijamy od ściany

                this.setDirection(this.getDirection().rotate(4)); // obrót

                int newDirectionIndex = this.getDirection().getValue();
                Vector2d bounceVector = MoveDirection.values()[newDirectionIndex].toUnitVector();

                Vector2d bouncePos = currentPos.add(bounceVector);

                if (map.canMoveTo(bouncePos)) {
                    this.setPosition(bouncePos);
                }
            }
        }

        this.addEnergy(-1);
        this.makeOlder();

    }
}
