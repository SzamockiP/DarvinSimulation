package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddPlantsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        LayerMap<Plant> plantMap = map.getPlants();

        for(int i=0; i < config.plantPerDay(); i++){
            Vector2d position = getRandomPlantPosition(map, config);

            if(position != null) plantMap.addEntity(new Plant(position));
            else break;
        }
    }

    private Vector2d getRandomPlantPosition(WorldMap map, SimulationConfig config) {
        LayerMap<Plant> plantMap = map.getPlants();

        int width = config.mapSize().upperRight().getX();
        int height = config.mapSize().upperRight().getY();

        // granice "Dżungli" (środkowego paska - 1/5 wysokości)
        int jungleStartY = config.jungleSize().lowerLeft().getY();
        int jungleEndY = config.jungleSize().upperRight().getY();

        // 3. Tworzymy dwie listy: wolne pola w dżungli i wolne pola na stepie
        List<Vector2d> freeJungleSpots = new ArrayList<>();
        List<Vector2d> freeSteppeSpots = new ArrayList<>();

        // Przechodzimy po całej mapie i sprawdzamy, gdzie nie ma trawy
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector2d pos = new Vector2d(x, y);

                if (!map.getPlants().isOccupied(pos)) {// Jeśli na danej pozycji NIE MA ROŚLINY
                    if (y >= jungleStartY && y < jungleEndY) {
                        freeJungleSpots.add(pos); // To jest środek (Dżungla)
                    } else {
                        freeSteppeSpots.add(pos); // To jest góra lub dół (Step)
                    }
                }
            }
        }

        // Losujemy, gdzie chcemy postawić trawę (80% szans na Dżunglę)
        boolean preferJungle = Math.random() < 0.8; // 80% szans na prawdę

        Random random = new Random();

        if (preferJungle) {
            // Chcemy postawić w Dżungli (80% przypadków)
            if (!freeJungleSpots.isEmpty()) {// Jest miejsce w dżungli -> losujemy stamtąd
                return freeJungleSpots.get(random.nextInt(freeJungleSpots.size()));
            } else if (!freeSteppeSpots.isEmpty()) {// Dżungla pełna -> bierzemy z reszty obszaru
                return freeSteppeSpots.get(random.nextInt(freeSteppeSpots.size()));
            }
        } else {
            // Chcemy postawić na Stepie (20% przypadków)
            if (!freeSteppeSpots.isEmpty()) {// Jest miejsce na reszcie mapie -> losujemy stamtąd
                return freeSteppeSpots.get(random.nextInt(freeSteppeSpots.size()));
            } else if (!freeJungleSpots.isEmpty()) {// Reszta mapy pełna -> bierzemy z Dżungli
                return freeJungleSpots.get(random.nextInt(freeJungleSpots.size()));
            }
        }

        return null;
    }
}
