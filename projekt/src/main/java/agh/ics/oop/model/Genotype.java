package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.HashSet;
import java.util.Set;

public class Genotype {
    private List<MoveDirection> genes;
    private int currentGeneIndex;
    private final int genotypeLenght;

    // FIXME: poprawić trzeba by było faktycznie losowe a nie takie xD
    public Genotype(int genotypeLenght) {
        this.genotypeLenght = genotypeLenght;
        this.currentGeneIndex = 0;
        this.genes = new ArrayList<>();
        for(int i = 0; i < this.genotypeLenght; i++){
            genes.add(MoveDirection.FRONT);
        }
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

    public static Genotype generateGenotype() {
        List<MoveDirection> randomGenes = new ArrayList<>();
        Random random = new Random();

        // geneAmount to Twoje pole w klasie (np. 8)
        for (int i = 0; i < this.genotypeLenght; i++) {
            int randomIndex;
            // Losujemy indeks od 0 do 7 (długość MoveDirection)
            if (i%2 == 0) randomIndex = random.nextInt(MoveDirection.values().length);

            else randomIndex = 0; // zwierzak nie kręci się w kółko

            randomGenes.add(MoveDirection.values()[randomIndex]);
        }

        return new Genotype(randomGenes);
    }

    public MoveDirection nextGene(){
        MoveDirection gene = genes.get(currentGeneIndex);
        currentGeneIndex = (currentGeneIndex + 1) % genes.size();
        return gene;
    }

    public Genotype cross(Genotype other, int energyFirst, int energySecond){ //dostajemy genotyp drugiego i ilość energi każdego stworzenia
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
        int amountRandomGenes = random.nextInt(genesCount); // ile genów
        Set<Integer> indexes = new HashSet<>();
        while(indexes.size() < amountRandomGenes){
            int idx = random.nextInt(genesCount);
            if(!indexes.contains(idx)){
                indexes.add(idx);
                childGenes.set(idx, MoveDirection.values()[random.nextInt(MoveDirection.values().length)]);
            }
        }

        return new Genotype(childGenes);
    }
}
