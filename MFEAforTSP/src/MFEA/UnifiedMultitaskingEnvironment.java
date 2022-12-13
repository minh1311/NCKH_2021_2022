package MFEA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static MFEA.Population.*;
import static UTILS.Sorting.mergeSort;

public class UnifiedMultitaskingEnvironment {
    private static int unifiedTaskDimension = 0;
    private ArrayList<Task> taskList;
    private Population currentPopulation,
            offspringPopulation,
            intermediatePopulation;
    private Individual optimumIndividual;
    private ArrayList<Double> optimumFitnessValues;
    private Double optimumScalarFitness;

    public UnifiedMultitaskingEnvironment(ArrayList<Task> taskList) {
        this.taskList = taskList;
        currentPopulation = new Population();
        offspringPopulation = new Population();
        intermediatePopulation = new Population();
        optimumIndividual = new Individual();
        optimumFitnessValues = new ArrayList<>();
        optimumScalarFitness = 0.0;
        updateUnifiedTaskDimension();
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public Task getTaskList(int index) {
        return taskList.get(index);
    }

    public void setTaskList(int index, Task task) {
        if(index > taskList.size() - 1) taskList.add(task);
        else taskList.set(index, task);
    }

    public Population getCurrentPopulation() {
        return currentPopulation;
    }

    public void setCurrentPopulation(Population currentPopulation) {
        this.currentPopulation = currentPopulation;
    }

    public Population getOffspringPopulation() {
        return offspringPopulation;
    }

    public void setOffspringPopulation(Population offspringPopulation) {
        this.offspringPopulation = offspringPopulation;
    }

    public Population getIntermediatePopulation() {
        return intermediatePopulation;
    }

    public void setIntermediatePopulation(Population intermediatePopulation) {
        this.intermediatePopulation = intermediatePopulation;
    }

    public Individual getOptimumIndividual() {
        return optimumIndividual;
    }

    public void setOptimumIndividual(Individual optimumIndividual) {
        this.optimumIndividual = optimumIndividual;
    }

    public ArrayList<Double> getOptimumFitnessValues() {
        return optimumFitnessValues;
    }

    public void setOptimumFitnessValues(ArrayList<Double> optimumFitnessValues) {
        this.optimumFitnessValues = optimumFitnessValues;
    }

    public Double getOptimumFitnessValues(int index) {
        return optimumFitnessValues.get(index);
    }

    public void setOptimumFitnessValues(int index, Double value) {
        if(index > optimumFitnessValues.size() - 1) optimumFitnessValues.add(value);
        else optimumFitnessValues.set(index, value);
    }

    public Double getOptimumScalarFitness() {
        return optimumScalarFitness;
    }

    public void setOptimumScalarFitness(Double optimumScalarFitness) {
        this.optimumScalarFitness = optimumScalarFitness;
    }

    public static int getUnifiedTaskDimension() {
        return unifiedTaskDimension;
    }

    public void updateUnifiedTaskDimension() {
        for (Task task : taskList) {
            if(task.getDimension() > unifiedTaskDimension)
                unifiedTaskDimension = task.getDimension();
        }
    }

    public void MFEAlgorithm() {
        Individual aIndividual;
        int iteration = 0;
        int counter = 0;
        boolean done = false;

        //Khởi tạo quần thể
        initializePopulation();

        while (!done) {
            /*
            Điều kiện kết thúc lặp:
                Quá số lượng lặp quy định, hoặc,
                Nếu fitness không thay đổi quá lâu (VD: 5 lần).
            */

            if(iteration < MAX_ITERATIONS) {
                GAlgorithm(); //Algorithm 2
                culturalTransmission(); //Algorithm 3
                ArrayList<Individual> concatPopulation = new ArrayList<>();
                concatPopulation.addAll(currentPopulation.getIndividuals());
                concatPopulation.addAll(offspringPopulation.getIndividuals());
                intermediatePopulation.setIndividuals(new ArrayList<>(concatPopulation));
                for (Task task : taskList) {
                    sortByFitness(offspringPopulation, task);
                }
                for (Individual individual : intermediatePopulation.getIndividuals()) {
                    individual.setScalarFitness(1.0 / Collections.min(individual.getFactorialRanks()));
                }
                sortByScalarFitness(intermediatePopulation);
                for (int i = 0; i < BEST_INDIVIDUAL_BOUND; i++) {
                    currentPopulation.setIndividuals(i, intermediatePopulation.getIndividuals(i));
                }
                for (int i = BEST_INDIVIDUAL_BOUND; i < INDIVIDUAL_COUNT; i++) {
                    int rand = new Random().nextInt(INDIVIDUAL_COUNT - BEST_INDIVIDUAL_BOUND) + BEST_INDIVIDUAL_BOUND;
                    currentPopulation.setIndividuals(i, intermediatePopulation.getIndividuals(rand));
                }
                counter++;
                if(currentPopulation.getIndividuals(0).getScalarFitness() < optimumScalarFitness) {
                    optimumIndividual = currentPopulation.getIndividuals(0);
                    optimumFitnessValues = new ArrayList<>(optimumIndividual.getFactorialCosts());
                    optimumScalarFitness = optimumIndividual.getScalarFitness();
                    counter = 0;
                }
                //Nếu fitness không thay đổi quá lâu
                if(counter == MAX_LAPSE) {
                    done = true;
                }

                iteration++;
            }
            else {
                done = true;
            }
        }
    }

    private void sortByScalarFitness(Population population) {
        mergeSort(0,
                population.getIndividuals().size() - 1,
                population.getIndividuals());
        Collections.reverse(population.getIndividuals());
    }

    private void GAlgorithm() {
        //Chọn bố mẹ để ghép cặp
        ArrayList<Individual> parents = new ArrayList<>(selectParent());
        ArrayList<Individual> offspring = new ArrayList<>();
        while (parents.size() != 0) {
            int[] soulMates = mating(parents);
            Individual[] crossChildren = new Individual[2];
            //Nếu lai ghép
            double rand = new Random().nextDouble();
            if ((parents.get(soulMates[0]).getSkillFactor()
                    == parents.get(soulMates[1]).getSkillFactor())
                    || (rand < RMP)) {
                crossChildren = crossOver(soulMates, parents);
            } else {
            	//Nếu đột biến
                crossChildren[0] = mutation(parents.get(soulMates[0]));
                crossChildren[1] = mutation(parents.get(soulMates[1]));
            }
            //Xóa các cá thể bố mẹ đã lai ghép hoặc đột biến ra khỏi list parents
            if (soulMates[0] > soulMates[1]) {
                parents.remove(soulMates[0]);
                parents.remove(soulMates[1]);
            } else {
                parents.remove(soulMates[1]);
                parents.remove(soulMates[0]);
            }
            offspring.addAll(Arrays.asList(crossChildren));
        }
        offspringPopulation.setIndividuals(new ArrayList<>(offspring));
    }

    private Individual mutation(Individual parent) {
        Individual mutantChild = new Individual();
        mutantChild.setChromosome(new ArrayList<>(parent.getChromosome()));
        int firstCity = new Random().nextInt(unifiedTaskDimension);
        int secondCity = 0;
        boolean done = false;
        while (!done) {
            secondCity = (new Random().nextInt(unifiedTaskDimension));
            if (secondCity != firstCity) {
                done = true;
            }
        }
        int temp = mutantChild.getChromosome(firstCity);
        mutantChild.setChromosome(firstCity,
                mutantChild.getChromosome(secondCity));
        mutantChild.setChromosome(secondCity, temp);
        mutantChild.setParentNumber(1);
        mutantChild.setParentASkillFactor(parent.getSkillFactor());
        mutantChild.setParentBSkillFactor(parent.getSkillFactor());
        mutantChild.setSkillFactor(parent.getSkillFactor());
        return mutantChild;
    }

    private Individual[] crossOver(int[] soulMates, ArrayList<Individual> parents) {
        int firstCutIndex = new Random().nextInt(unifiedTaskDimension - 2);
        int secondCutIndex = 0;
        boolean done = false;
        //Chọn hai cá thể khác nhau
        while (!done) {
            secondCutIndex = new Random().nextInt(unifiedTaskDimension - 1);
            if (secondCutIndex >= firstCutIndex) {
                done = true;
            }
        }
        //Lấy thông tin từ hai cá thể bố mẹ.
        Individual parentA = parents.get(soulMates[0]);
        Individual parentB = parents.get(soulMates[1]);
        //Lấy đoạn gen từ cá thể bố mẹ
        int[] segmentA = createSegment(firstCutIndex, secondCutIndex, parentA);
        int[] segmentB = createSegment(firstCutIndex, secondCutIndex, parentB);
        Individual[] returnCrossChildren = new Individual[2];
        //Sao chép đoạn gen lấy từ cá thể bố mẹ sang cá thể con
        Integer[] childAChromosome = insertSegment(firstCutIndex, secondCutIndex, segmentA);
        Integer[] childBChromosome = insertSegment(firstCutIndex, secondCutIndex, segmentB);
        //Điền đầy đủ tạo thành cá thể con mới
        cross(firstCutIndex, secondCutIndex, childAChromosome, parentB, parentA);
        cross(firstCutIndex, secondCutIndex, childBChromosome, parentA, parentB);
        returnCrossChildren[0] = new Individual();
        returnCrossChildren[0].setChromosome(new ArrayList<>(Arrays.asList(childAChromosome)));
        returnCrossChildren[0].setParentNumber(2);
        returnCrossChildren[0].setParentASkillFactor(parentA.getSkillFactor());
        returnCrossChildren[0].setParentBSkillFactor(parentB.getSkillFactor());
        returnCrossChildren[1] = new Individual();
        returnCrossChildren[1].setChromosome(new ArrayList<>(Arrays.asList(childBChromosome)));
        returnCrossChildren[1].setParentNumber(2);
        returnCrossChildren[1].setParentASkillFactor(parentB.getSkillFactor());
        returnCrossChildren[1].setParentBSkillFactor(parentA.getSkillFactor());
        return returnCrossChildren;
    }

    private void cross(int firstCutIndex, int secondCutIndex, Integer[] childChromosome,
                       Individual notAlikeParent, Individual alikeParent) {
        for (int i = firstCutIndex; i <= secondCutIndex; i++) {
            boolean alreadyThere = false;
            for (int j = firstCutIndex; j <= secondCutIndex; j++) {
                if (childChromosome[j] == notAlikeParent.getChromosome(i)) {
                    alreadyThere = true;
                    break;
                }
            }
            if (!alreadyThere) {
                int index = i;
                int notAlreadyThereValue = notAlikeParent.getChromosome(index);
                while (true) {
                    int sameIndexValue = alikeParent.getChromosome(index);
                    int sameValueIndex = notAlikeParent.getChromosome().indexOf(sameIndexValue);
                    if ((sameValueIndex < firstCutIndex) || (sameValueIndex > secondCutIndex)) {
                        childChromosome[sameValueIndex] = notAlreadyThereValue;
                        break;
                    } else {
                        index = sameValueIndex;
                    }
                }
            }
        }
        for (int i = 0; i < unifiedTaskDimension; i++) {
            if ((childChromosome[i] == -1)) {
                childChromosome[i] = notAlikeParent.getChromosome(i);
            }
        }
    }

    private Integer[] insertSegment(int firstCutIndex, int secondCutIndex, int[] segment) {
        Integer[] returnChildChromosome = new Integer[unifiedTaskDimension];
        int segmentIndex = 0;
        for (int i = 0; i < unifiedTaskDimension; i++) {
            if ((i >= firstCutIndex) && (i <= secondCutIndex)) {
                returnChildChromosome[i] = segment[segmentIndex];
                segmentIndex++;
            } else {
                returnChildChromosome[i] = -1;
            }
        }
        return returnChildChromosome;
    }

    private int[] createSegment(int firstCutIndex, int secondCutIndex, Individual parent) {
        int capacityOfSegment = (secondCutIndex - firstCutIndex) + 1;
        int[] returnSegment = new int[capacityOfSegment];
        ArrayList<Integer> parentChromosome = new ArrayList<>(parent.getChromosome());
        int segmentIndex = 0;
        for (int i = 0; i < unifiedTaskDimension; i++) {
            if ((i >= firstCutIndex) && (i <= secondCutIndex)) {
                returnSegment[segmentIndex] = parentChromosome.get(i);
                segmentIndex++;
            }
        }
        return returnSegment;
    }

    private int[] mating(ArrayList<Individual> parents) {
        int parentsSize = parents.size();
        int randomA = new Random().nextInt(parentsSize);
        int randomB;
        randomB = 0;
        boolean done = false;
        //Chọn 2 cá thể khác nhau
        while (!done) {
            randomB = new Random().nextInt(parentsSize);
            if (randomB != randomA) {
                done = true;
            }
        }
        return new int[]{randomA, randomB};
    }

    private ArrayList<Individual> selectParent() {
        ArrayList<Individual> parents = new ArrayList<>();
        double fitnessSum = 0.0;
        double individualFitness = 0.0;
        //Tính tổng fitness
        for (int i = 0; i < INDIVIDUAL_COUNT; i++) {
            for (Task task : taskList) {
                fitnessSum += currentPopulation.
                        getIndividuals(i).
                        getFactorialCosts(task.getTaskId());
            }
        }
        for (int i = 0; i < INDIVIDUAL_COUNT; i++) {
            for (Task task : taskList) {
                individualFitness += currentPopulation.
                        getIndividuals(i).
                        getFactorialCosts(task.getTaskId());
            }
            if (individualFitness / fitnessSum
                    <= SELECTION_PROBABILITY) {
                parents.add(currentPopulation.getIndividuals(i));
            }
            individualFitness = 0;
        }
        if (parents.size() % 2 != 0) {
            parents.remove(parents.size()-1);
        }
        return parents;
    }

    private void culturalTransmission() {
        ArrayList<Individual> offspring = new ArrayList<>(offspringPopulation.getIndividuals());

        for (Individual child : offspring) {
            for (Task task : taskList) {
                child.setFactorialCosts(task.getTaskId(), Double.MAX_VALUE);
            }
            int parentNumber = child.getParentNumber();
            if(parentNumber == 2) {
                double rand = new Random().nextDouble();
                if(rand < FACTOR_PROBABILITY) {
                    child.setSkillFactor(child.getParentASkillFactor());
                }
                else {
                    child.setSkillFactor(child.getParentBSkillFactor());
                }
            }
            Task evaluatedTask = taskList.get(child.getSkillFactor());
            calculateFitnessValue(child, evaluatedTask);
        }
    }

    private void initializePopulation() {
        for (int i = 0; i < INDIVIDUAL_COUNT; i++) {
            Individual newIndividual = new Individual();
            for (int j = 0; j < unifiedTaskDimension; j++) {
                newIndividual.setChromosome(j, j);
            }
            currentPopulation.setIndividuals(i, newIndividual);
            //Tạo các cá thể
            for (int j = 0; j < INDIVIDUAL_COUNT; j++) {
                randomlyArrange(currentPopulation.getIndividuals().indexOf(newIndividual));
            }
            //Đánh giá từng cá thể với các nhiệm vụ tối ưu hóa
            for (Task task : taskList) {
                calculateFitnessValue(currentPopulation.getIndividuals(i), task);
            }
        }
        //Sắp xếp cá thể theo Fitness từng nhiệm vụ tối ưu.
        for (Task task : taskList) {
            sortByFitness(currentPopulation, task);
        }
        //Tính skill factor.
        for (int i = 0; i < INDIVIDUAL_COUNT; i++) {
            Individual currentIndividual = currentPopulation.getIndividuals(i);
            ArrayList<Integer> ranks = new ArrayList<>(currentIndividual.getFactorialRanks());
            currentIndividual.setSkillFactor(ranks.indexOf(Collections.min(ranks)));
        }
    }

    private void sortByFitness(Population population, Task task) {
        mergeSort(0,
                population.getIndividuals().size() - 1,
                population.getIndividuals(),
                task);
        for (int i = 0; i < population.getIndividuals().size(); i++) {
            population.getIndividuals(i).setFactorialRanks(task.getTaskId(), i + 1);
        }
        if(population.getIndividuals(0).getFactorialCosts(task.getTaskId()) < task.getBestFitnessValue()) {
            task.setBestIndividual(population.getIndividuals(0));
            task.setBestFitnessValue(task.getBestIndividual().getFactorialCosts(task.getTaskId()));
        }
    }

    private void calculateFitnessValue(Individual individual, Task task) {
        ArrayList<Integer> copyChromosome = new ArrayList<>(
                individual.decoding(task));
        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < task.getDimension(); i++) {
            distances.add(task.getDistance(copyChromosome.get(i),
                    copyChromosome.get((i == task.getDimension() - 1) ? 0 : (i + 1))));
        }
        individual.setFactorialCosts(task.getTaskId(), task.getFitnessValue(distances));
    }

    private void randomlyArrange(int index) {
        int firstCity = new Random().nextInt(unifiedTaskDimension);
        int secondCity = 0;
        boolean done = false;
        //Chọn 2 thành phố bất kì
        while (!done) {
            secondCity = new Random().nextInt(unifiedTaskDimension);
            if (secondCity != firstCity) {
                done = true;
            }
        }
        //Hoán đổi.
        switchValues(index, secondCity, firstCity);
    }

    private void switchValues(int index, int secondCity, int firstCity) {
        int temp = currentPopulation.getIndividuals().get(index).getChromosome(firstCity);
        currentPopulation.getIndividuals().get(index).setChromosome(firstCity,
                currentPopulation.getIndividuals().get(index).getChromosome(secondCity));
        currentPopulation.getIndividuals().get(index).setChromosome(secondCity, temp);
    }

    public double printBestSolution(Task task)
    {
        Individual bestIndividual = task.getBestIndividual();
        Double bestFitness = task.getBestFitnessValue();
        System.out.print("Shortest Route: ");
        ArrayList<Integer> taskChromosome = bestIndividual.decoding(task);
        for(int j = 0; j < task.getDimension(); j++)
        {
            System.out.print(taskChromosome.get(j) + ", ");
            if(j == task.getDimension() - 1)
                System.out.print(taskChromosome.get(0) + ", ");
        }
        System.out.print("Distance: " + bestFitness + "\n");

        return bestFitness;
    }
    
    public double printDistance(Task task)
    {
        Individual bestIndividual = task.getBestIndividual();
        Double bestFitness = task.getBestFitnessValue();

        return bestFitness;
    }
}
