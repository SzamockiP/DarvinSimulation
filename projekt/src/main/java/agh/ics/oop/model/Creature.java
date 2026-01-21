package agh.ics.oop.model;
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

        if (rotation.equals(MoveDirection.FRONT)) {
            Vector2d unitVector = this.getDirection().getUnitVector(); // wybieramy wektor, który pasuje dla tego kierunku

            Vector2d currentPos = this.getPosition();
            Vector2d newPos = currentPos.add(unitVector);

            if (map.inBounds(newPos)) {
                this.setPosition(newPos);
            } else { // odbijamy od ściany

                this.setDirection(this.getDirection().rotate(MoveDirection.BACK)); // zawróć

                Vector2d bounceVector = this.getDirection().getUnitVector();

                Vector2d bouncePos = currentPos.add(bounceVector);

                // FIXME: Biedak może być w rogu i znowu się teoretycznie odbić, ale to już jego problem
                if (map.inBounds(bouncePos)) {
                    this.setPosition(bouncePos);
                }
            }
        }
    }

    @Override
    public Creature reproduce(Creature other) {
        if(!other.getClass().equals(this.getClass())){
            throw new ClassCastException("Can't reproduce different class creatures");
        }
        SimulationConfig config = getSimulationConfig();

        Genotype newGenotype = getGenotype().cross(other.getGenotype(), getEnergy(), other.getEnergy(), config);

        // Energia dziecka i strata rodziców
        int cost = config.reproductionCost();
        int newEnergy = 2 * cost;

        this.addEnergy(-cost);
        other.addEnergy(-cost);

        if (this instanceof Animal) ((Animal) this).addChild();
        if (other instanceof Animal) ((Animal) other).addChild();

        Vector2d newPosition = this.getPosition();

        return createChild(newPosition, newGenotype, newEnergy);
    }
    protected abstract Creature createChild(Vector2d position, Genotype genotype, int energy);
}
