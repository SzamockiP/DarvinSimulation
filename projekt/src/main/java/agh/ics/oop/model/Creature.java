package agh.ics.oop.model;

import agh.ics.oop.model.base.Entity;
import agh.ics.oop.model.base.MapDirection;
import agh.ics.oop.model.base.Vector2d;
import agh.ics.oop.model.base.MoveDirection;
import agh.ics.oop.model.base.Boundary;
import agh.ics.oop.model.interfaces.IAlive;
import agh.ics.oop.model.interfaces.IMove;
import agh.ics.oop.model.map.LayerMap;
import agh.ics.oop.model.interfaces.IReproduce;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.util.SimulationConfig;

public abstract class Creature extends Entity implements IAlive, IMove,IReproduce{
    private MapDirection direction;
    private int energy;
    private boolean isAlive;
    Genotype genotype;
    private final SimulationConfig simulationConfig;


    public Creature(Vector2d position, int initialEnergy, Genotype genotype,  SimulationConfig simulationConfig) {
        super(position);
        this.energy = initialEnergy;
        this.genotype = genotype;
        this.direction = MapDirection.NORTH;
        this.isAlive = true;
        this.simulationConfig = simulationConfig;
    }

    public Creature(Vector2d position, int initialEnergy, int genomeSize, SimulationConfig simulationConfig) {
        this(position, initialEnergy, new Genotype(genomeSize), simulationConfig);
    }

    public Creature(Creature other) {
        super(other.getPosition());
        this.energy = other.energy;
        this.genotype = new Genotype(other.genotype);
        this.direction = other.direction;
        this.isAlive = other.isAlive;
        this.simulationConfig = other.simulationConfig;
    }

    public SimulationConfig getSimulationConfig() {
        return simulationConfig;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    // IAlive
    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void addEnergy(int delta) {
        this.energy += delta;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void kill() {
        isAlive = false;
    }

    // IMovable
    public void move(LayerMap map){
        MoveDirection rotation = this.genotype.nextGene();

        this.setDirection(this.getDirection().rotate(rotation));

        Vector2d unitVector = this.getDirection().getUnitVector(); // wybieramy wektor, który pasuje dla tego kierunku

        Vector2d currentPos = this.getPosition();
        Vector2d newPos = currentPos.add(unitVector);

        Boundary bounds = map.getCurrentBoundary();

        // Sprawdzenie góry/dołu
        if (newPos.getY() < bounds.lowerLeft().getY() || newPos.getY() > bounds.upperRight().getY()) {
            this.setDirection(this.getDirection().rotate(MoveDirection.BACK)); // zawróć
            // Po obrocie recalculujemy pozycję (ruch w nową stronę)
            newPos = currentPos.add(this.getDirection().getUnitVector());
        }

        // Sprawdzenie lewej/prawej
        int newX = newPos.getX();
        if (newX < bounds.lowerLeft().getX()) {
            newX = bounds.upperRight().getX();
        } else if (newX > bounds.upperRight().getX()) {
            newX = bounds.lowerLeft().getX();
        }
        newPos = new Vector2d(newX, newPos.getY());

        if (map.inBounds(newPos)) {
            this.setPosition(newPos);
        }
    }

    @Override
    public Creature reproduce(Creature other) {
        if(!other.getClass().equals(this.getClass())){
            throw new ClassCastException("Nie można rozmnożyć różnych klas");
        }
        SimulationConfig config = getSimulationConfig();

        Genotype newGenotype = getGenotype().cross(other.getGenotype(), getEnergy(), other.getEnergy(), config);

        // Energia dziecka i strata rodziców
        int cost = config.reproductionCost();
        int newEnergy = 2 * cost;

        this.addEnergy(-cost);
        other.addEnergy(-cost);

        Vector2d position = this.getPosition();
        Vector2d newPosition = position;
        
        Creature child = createChild(newPosition, newGenotype, newEnergy);

        if (this instanceof Animal && child instanceof Animal) ((Animal) this).addChild((Animal)child);
        if (other instanceof Animal && child instanceof Animal) ((Animal) other).addChild((Animal)child);

        return child;
    }
    protected abstract Creature createChild(Vector2d position, Genotype genotype, int energy);
}
