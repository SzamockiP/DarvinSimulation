package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.interfaces.*;
import agh.ics.oop.model.util.SimulationConfig;
import java.util.List;

public class ReproduceParasitesManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        LayerMap<Parasite> parasiteMap = map.getParasites();

        for(int y = 0; y < parasiteMap.getCurrentBoundary().upperRight().getY(); y++){
            for(int x = 0; x < parasiteMap.getCurrentBoundary().upperRight().getX(); x++){
                Vector2d pos = new Vector2d(x,y);
                List<Parasite> parasites = parasiteMap.getEntitiesAt(pos);
                if(parasites.isEmpty()) continue;

                // pasożyt z największą energią
                Parasite bestParasite = parasites.getFirst();

                for(Parasite parasite : parasites){
                    if(parasite.getEnergy() > bestParasite.getEnergy()){
                        bestParasite = parasite;
                    }
                }

                Parasite secondBestParasite = null;
                for(Parasite parasite : parasites){
                    if(parasite.getEnergy() <= bestParasite.getEnergy() && parasite != bestParasite){
                        secondBestParasite = parasite;
                    }
                }
                if(secondBestParasite == null) continue;

                if(bestParasite.getEnergy() >= config.reproductionMinEnergy() &&
                        config.reproductionMinEnergy() <= secondBestParasite.getEnergy()){
                    Parasite newParasite = (Parasite) bestParasite.reproduce(secondBestParasite);
                    parasiteMap.addEntity(newParasite);
                }
            }
        }
    }
}
