package agh.ics.oop.model;

import agh.ics.oop.model.util.SimulationConfig;
import agh.ics.oop.model.base.MoveDirection;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.HashSet;
import java.util.Set;

public class Genotype {
    private List<MoveDirection> genes;
    private int currentGeneIndex;
    private final int genotypeLenght;

    public Genotype(int genotypeLenght) {
        this.genotypeLenght = genotypeLenght;
        this.currentGeneIndex = 0;
        this.genes = new ArrayList<>();

        //generowanie losowego genotypu
        Random random = new Random();
        for(int i = 0; i < this.genotypeLenght; i++){
            if (i % 2 == 0) {
                // Losowy ruch
                this.genes.add(MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
            } else {
                // Ruch do przodu
                this.genes.add(MoveDirection.FRONT);
            }
        }
    }

    public Genotype(Genotype other) {
        this.genes = new ArrayList<>(other.genes);
        this.genotypeLenght = other.genotypeLenght;
        this.currentGeneIndex = other.currentGeneIndex;
    }

    public Genotype(List<MoveDirection> genes) {
        this.genes = new ArrayList<>(genes); // Kopia listy
        this.genotypeLenght = genes.size();
    }


    public List<MoveDirection> getGenes() {
        return genes;
    }

    public void setGenes(List<MoveDirection> genes) {
        this.genes = genes;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }

    public void setCurrentGeneIndex(int currentGeneIndex) {
        this.currentGeneIndex = currentGeneIndex;
    }

    public MoveDirection nextGene(){
        MoveDirection gene = genes.get(currentGeneIndex);
        currentGeneIndex = (currentGeneIndex + 1) % genes.size();
        return gene;
    }

    public Genotype cross(Genotype other, int energyFirst, int energySecond, SimulationConfig config){ //dostajemy genotyp drugiego i ilość energi każdego stworzenia
        Genotype stronger;
        Genotype weaker;
        int energyStronger;

        // ustaw genotypy
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
        float ratio = (float) energyStronger / combinedEnergy;
        int genesCount = genes.size();
        int amountStronger = (int) (genesCount * ratio);
        int amountWeaker = genesCount - amountStronger;

        // Losujemy stronę dla silniejszego (0 - lewa, 1 - prawa)
        boolean swapSides = random.nextBoolean();

        List<MoveDirection> childGenes = new ArrayList<>(genesCount);
        List<MoveDirection> strongerGenes = stronger.getGenes();
        List<MoveDirection> weakerGenes = weaker.getGenes();

        if(swapSides){
            // Silniejszy bierze lewą stronę, słabszy wypełnia resztę
            childGenes.addAll(strongerGenes.subList(0, amountStronger));
            childGenes.addAll(weakerGenes.subList(amountStronger, genesCount));
        }else{
            childGenes.addAll(weakerGenes.subList(0, amountWeaker));
            childGenes.addAll(strongerGenes.subList(amountWeaker, genesCount));
        }


        // losujemy ile genów i jakie będą poddane mutacji
        // losujemy ile genów i jakie będą poddane mutacji
        int mutationCount = random.nextInt(config.mutationMax() - config.mutationMin() + 1) + config.mutationMin();
        Set<Integer> indexes = new HashSet<>();
        while(indexes.size() < mutationCount){
            int idx = random.nextInt(genesCount);
            if(!indexes.contains(idx)){
                indexes.add(idx);
                childGenes.set(idx, MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
            }
        }

        return new Genotype(childGenes);
    }

    @Override
    public String toString() {
        return genes.stream()
                // zamiast samego obiektu, pobieramy jego wartość (int)
                .map(gene -> String.valueOf(gene.getValue()))
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
