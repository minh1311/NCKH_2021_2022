package MFEA;

import java.util.ArrayList;

public class Population {
    private ArrayList<Individual> individuals;
    static final int BEST_INDIVIDUAL_BOUND = 150;
    static final int INDIVIDUAL_COUNT = 200;
    static final double SELECTION_PROBABILITY = 0.75;
    static final double RMP = 0.05;
    static final double FACTOR_PROBABILITY = 0.5;
    static final long MAX_ITERATIONS = 100;
    static final long MAX_LAPSE = 100;

    public Population() {
        individuals = new ArrayList<>();
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(ArrayList<Individual> individuals) {
        this.individuals = individuals;
    }

    public Individual getIndividuals(int index) {
        return individuals.get(index);
    }

    public void setIndividuals(int index, Individual individual) {
        if(index > individuals.size() - 1) individuals.add(individual);
        else individuals.set(index, individual);
    }
}
