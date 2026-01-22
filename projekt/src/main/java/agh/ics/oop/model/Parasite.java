package agh.ics.oop.model;

import agh.ics.oop.model.util.SimulationConfig;

public class Parasite extends Creature implements IMove,IReproduce {
    Animal host;
    private boolean panicking;
    //private final SimulationConfig simulationConfig;
    private int daysWithHost;

    public Parasite(Vector2d position, Genotype genotype, SimulationConfig simulationConfig) {
        super(position, simulationConfig.startingEnergy(), genotype, simulationConfig);
        panicking =  false;
        //this.simulationConfig = simulationConfig;
        daysWithHost = 0;
    }

    public Parasite(Vector2d position, Genotype genotype, int energy, SimulationConfig simulationConfig) {
        //this.simulationConfig = simulationConfig;
        super(position, energy, genotype, simulationConfig);
        panicking =  false;
        daysWithHost = 0;
    }

    public void setHost(Animal host) {
        if(this.host != null )
            return;

        this.panicking = host == null;
        this.host = host;
        this.daysWithHost = 0;
    }
    
    public Animal getHost() {
        return host;
    }

    @Override
    public void move(LayerMap map){
        if(!this.isAlive()){
            return;
        }

        // sprawdzamy, czy host wgl żyje
        if(this.host != null && !this.host.isAlive()){
            this.panicking = true;
            this.host = null;
        }

        // jeśli mamy hosta
        if(this.host != null){
            daysWithHost++;
            // sprawdzamy wszystkie pozycje wokół hosta (tylko z pasożytami)
            // i ustawiamy pozycję na pustą wokół; w przeciwnym wypadku na już zajętą z
            Vector2d hostPosition = this.host.getPosition();
            MapDirection hostBack = this.host.getDirection().rotate(MoveDirection.BACK);

            // przyklej mu się na tyłek
            Vector2d newPosition = hostPosition.add(hostBack.getUnitVector());
            // jeśli nie pasuje, to znajdź legalną pozycję
            if(!map.inBounds(newPosition) || map.isOccupied(newPosition)){
                for(MapDirection direction : MapDirection.values()){
                    newPosition = hostPosition.add(direction.getUnitVector());
                    // pierwsza wolna pozycja, to zajmij
                    if(map.inBounds(newPosition) && !map.isOccupied(newPosition)){
                        break;
                    }
                }
            }
            // jeśli nadal nielegalna pozycja (nie znaleźliśmy wolnej legalnej)
            if(!map.inBounds(newPosition)){
                for(MapDirection direction : MapDirection.values()){
                    newPosition = hostPosition.add(direction.getUnitVector());
                    // pierwsza wolna pozycja, to zajmij
                    if(map.inBounds(newPosition)){
                        break;
                    }
                }
            }

            // ustaw pozycję i zabierz hostowi energię
            this.setPosition(newPosition);
            int stolenEnergy = getSimulationConfig().energyLossDueParasite();
            this.host.addEnergy(-stolenEnergy);
            this.addEnergy(stolenEnergy);

            if(this.host.getEnergy() <= 0){
                this.host.kill();
            }
        }
        else {
            super.move(map);
            if(panicking) this.addEnergy(-getSimulationConfig().energyLossInPanic());
            else this.addEnergy(-getSimulationConfig().dailyEnergyLoss());
        }

    }

    @Override
    protected Creature createChild(Vector2d position, Genotype genotype, int energy) {
        // Zwierzę tworzy małe Zwierzę
        return new Parasite(position, genotype, energy, getSimulationConfig());
    }

    /*@Override
    public Creature reproduce(Creature other) {
        if(!other.getClass().equals(this.getClass())){
            throw new ClassCastException("Can't reproduce different class creatures");
        }

        Genotype newGenotype = getGenotype().cross(other.getGenotype(), getEnergy(), other.getEnergy());

        int newEnergy = getEnergy()/2 +  other.getEnergy()/2;
        this.setEnergy(getEnergy()/2);
        other.setEnergy(other.getEnergy()/2);

        Vector2d newPosition = this.getPosition();

        return new Parasite(newPosition, newGenotype, newEnergy, simulationConfig);
    }*/

}
