package UTILS;

import MFEA.Individual;
import MFEA.Task;

import java.util.ArrayList;

public class Sorting {

    public static void mergeSort(int p, int r, ArrayList<Individual> individuals, Task task) {
        if(p < r) {
            int  q = Math.floorDiv((p + r), 2);
            mergeSort(p, q, individuals, task);
            mergeSort(q + 1, r, individuals, task);
            merge(p, q, r, individuals, task);
        }
    }

    private static void merge(int p, int q, int r, ArrayList<Individual> individuals, Task task) {
        Individual maxIndividual = new Individual();
        int costSize = individuals.get(0).getFactorialCosts().size();
        int h = 0;
        while (h < costSize) {
            maxIndividual.setFactorialCosts(h, Double.POSITIVE_INFINITY);
            h++;
        }
        ArrayList<Individual> lIndividuals
                = new ArrayList<>(individuals.subList(p, q + 1));
        lIndividuals.add(maxIndividual);
        ArrayList<Individual> rIndividuals
                = new ArrayList<>(individuals.subList(q + 1, r + 1));
        rIndividuals.add(maxIndividual);
        int i = 0;
        int j = 0;
        for (int k = p; k < r + 1; k++) {
            if (lIndividuals.get(i).compareTo(rIndividuals.get(j), task.getTaskId()) != 1) {
                individuals.set(k, lIndividuals.get(i));
                i++;
            }
            else {
                individuals.set(k, rIndividuals.get(j));
                j++;
            }
        }
    }

    public static void mergeSort(int p, int r, ArrayList<Individual> individuals) {
        if(p < r) {
            int  q = Math.floorDiv((p + r), 2);
            mergeSort(p, q, individuals);
            mergeSort(q + 1, r, individuals);
            merge(p, q, r, individuals);
        }
    }

    private static void merge(int p, int q, int r, ArrayList<Individual> individuals) {

        Individual maxIndividual = new Individual();
        int h = 0;
        maxIndividual.setScalarFitness(Double.MAX_VALUE);
        ArrayList<Individual> lIndividuals
                = new ArrayList<>(individuals.subList(p, q + 1));
        lIndividuals.add(maxIndividual);
        ArrayList<Individual> rIndividuals
                = new ArrayList<>(individuals.subList(q + 1, r + 1));
        rIndividuals.add(maxIndividual);
        int i = 0;
        int j = 0;
        for (int k = p; k < r + 1; k++) {
            if (lIndividuals.get(i).compareTo(rIndividuals.get(j)) - 1 != 0) {
                individuals.set(k, lIndividuals.get(i));
                i++;
            }
            else {
                individuals.set(k, rIndividuals.get(j));
                j++;
            }
        }
    }
}
