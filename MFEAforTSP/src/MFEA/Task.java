package MFEA;

import TSP.City;
import TSP.EuclidDistance;
import UTILS.ConstantString;
import UTILS.Function;
import UTILS.TSPFile;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Task {
    private static final int DOMAIN = 250; //Coordinates domain.
    private static int taskCount = 0;
    private int taskId;
    private Function objectiveFunction;
    private Individual bestIndividual;
    private Double bestFitnessValue;
    private int dimension;
    private Double target;
    private ArrayList<City> map; //City map.
    private ArrayList<Double> aX;   //X-coordinate list.
    private ArrayList<Double> aY;   //Y-coordinate list.
    private String distanceType;

    public Task(String filename) {
        taskId = taskCount;
        bestIndividual  = new Individual();
        bestFitnessValue = Double.MAX_VALUE;
        map = new ArrayList<>();
        aX = new ArrayList<>();
        aY = new ArrayList<>();
        initializeMap(filename);
        taskCount++;
    }

    public static int getDOMAIN() {
        return DOMAIN;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Function getObjectiveFunction() {
        return objectiveFunction;
    }

    public void setObjectiveFunction(Function objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public Individual getBestIndividual() {
        return bestIndividual;
    }

    public void setBestIndividual(Individual bestIndividual) {
        this.bestIndividual = bestIndividual;
    }

    public Double getBestFitnessValue() {
        return bestFitnessValue;
    }

    public void setBestFitnessValue(Double bestFitnessValue) {
        this.bestFitnessValue = bestFitnessValue;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }



    public ArrayList<City> getMap() {
        return map;
    }

    public ArrayList<Double> getaX() {
        return aX;
    }

    public void setaX(ArrayList<Double> aX) {
        this.aX = aX;
    }

    public ArrayList<Double> getaY() {
        return aY;
    }

    public void setaY(ArrayList<Double> aY) {
        this.aY = aY;
    }

    public double getaX(int index) {
        return aX.get(index);
    }

    public void setaX(int index, double value) {
        if (index > aX.size() - 1) aX.add(value);
        else aX.set(index, value);
    }

    public double getaY(int index) {
        return aY.get(index);
    }

    public void setaY(int index, double value) {
        if (index > aY.size() - 1) aY.add(value);
        else aY.set(index, value);
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    //Initialize a map with cities.
    private void initializeMap(String filename) {
        if (filename != null) {
            TSPFile.readFile(filename, this);
        } else {
            System.out.print("Number of Cities: ");
            Scanner sc = new Scanner(System.in);
            dimension = sc.nextInt();
            int randomX, randomY;

            //Create city list without concurrent points.
            boolean duplicate;
            int count = 0;
            while (aX.size() < dimension) {
                do {
                    randomX = new Random().nextInt(DOMAIN);
                    if (aX.indexOf(randomX) == -1) {
                        duplicate = false;
                        setaX(count, randomX);
                        randomY = new Random().nextInt(DOMAIN);
                        setaX(count, randomY);
                        count++;
                    } else duplicate = true;
                } while (duplicate);
            }
        }

        //Add city into the map.
        for (int i = 0; i < dimension; i++) {
            City city = new City();
            city.setcX(aX.get(i));
            city.setcY(aY.get(i));
            map.add(city);
        }
    }

    //Show location of cities (their coordinates).
    public void printMap() {
        for (int i = 0; i < dimension; i++) {
            System.out.print("City " + i + ": [" + map.get(i).getcX()
                    + ", " + map.get(i).getcY() + "] \n");
        }
    }


    public double getDistance(int firstCity, int secondCity) {
        ArrayList<Double> variables = new ArrayList<>();
        City cityA = map.get(firstCity);
        City cityB = map.get(secondCity);
        double xd = cityA.getcX() - cityB.getcX();
        double yd = cityA.getcY() - cityB.getcY();
        variables.add(xd);
        variables.add(yd);
        if (distanceType.equals(ConstantString.EUC_2D)) {
            EuclidDistance f = (EuclidDistance) objectiveFunction;
            return f.euclidDistance(variables);
            //Euclid distance.
        } else return -1;
    }

    public double getFitnessValue(ArrayList<Double> distances) {
        if (distanceType.equals(ConstantString.EUC_2D)) {
            EuclidDistance f = (EuclidDistance) objectiveFunction;
            return f.fitnessFunction(distances);
        } else return -1;
    }
}
