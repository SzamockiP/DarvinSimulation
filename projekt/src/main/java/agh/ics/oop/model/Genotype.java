package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.HashSet;
import java.util.Set;

public class Genotype {
    private List<MapDirection> genes = new ArrayList<MapDirection>();
    int currentGeneIndex;
    int geneAmount = 8;

    public Genotype(List<MapDirection> genes) {
        this.genes = new ArrayList<>(genes); // Kopia listy
    }
    public List<MapDirection> getGenes() {
        return genes;
    }
    public void setGenes(List<MapDirection> genes) {
        this.genes = genes;
    }
    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }
    public void setCurrentGeneIndex(int currentGeneIndex) {
        this.currentGeneIndex = currentGeneIndex;
    }

    public MapDirection nextGene(){
        MapDirection gene = genes.get(currentGeneIndex);
        currentGeneIndex = (currentGeneIndex + 1) % genes.size();
        return gene;
    }

    public Genotype cross(Genotype other, int energyFirst, int energySecond){ //dostajemy genotyp drugiego i ilość energi każdego stworzenia
        Genotype stronger;
        Genotype weaker;
        int energyStronger;

        if(energyFirst >= energySecond){
            stronger = this;
            weaker = other;
            energyStronger = energyFirst;
        }else{
            stronger = other;
            weaker = this;
            energyStronger = energySecond;
        }

        Random random = new Random();
        int combinedEnergy = energyFirst + energySecond;

        // Obliczamy ile genów bierze silniejszy
        double ratio = (double) energyStronger / combinedEnergy;
        int genesCount = genes.size();
        int amountStronger = (int) (genesCount * ratio);
        int amountWeaker = genesCount - amountStronger;

        // Losujemy stronę dla silniejszego (0 - lewa, 1 - prawa)
        boolean swapSides = random.nextBoolean();

        List<MapDirection> childGenes = new ArrayList<>();
        List<MapDirection> strongerGenes = stronger.getGenes();
        List<MapDirection> weakerGenes = weaker.getGenes();

        if(swapSides){
            // Silniejszy bierze lewą stronę, słabszy wypełnia resztę
            childGenes.addAll(strongerGenes.subList(0, amountStronger));
            childGenes.addAll(weakerGenes.subList(amountStronger, genesCount));
        }else{
            childGenes.addAll(weakerGenes.subList(0, amountWeaker));
            childGenes.addAll(strongerGenes.subList(amountWeaker, genesCount));
        }


        // losujemy ile genów i jakie będą poddane mutacji
        int amountRandomGenes = random.nextInt(genesCount); // ile genów
        Set<Integer> indexes = new HashSet<>();
        while(indexes.size() < amountRandomGenes){
            int idx = random.nextInt(genesCount);
            if(!indexes.contains(idx)){
                indexes.add(idx);
                childGenes.set(idx, MapDirection.values()[random.nextInt(MapDirection.values().length)]);
            }
        }

        return new Genotype(childGenes);
    }

}
