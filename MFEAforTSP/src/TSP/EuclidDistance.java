package TSP;

import UTILS.Function;

import java.util.ArrayList;

public class EuclidDistance extends Function {

    public double euclidDistance(ArrayList<Double> x) {
        Double sum = 0.0;
        for (Double var : x) {
            sum += var*var;
        }
        return Math.sqrt(sum);
    }

    @Override
    public double fitnessFunction(ArrayList<Double> distances) {
        double fitnessValue = 0.0;
        for (Double distance : distances) {
            fitnessValue += distance;
        }
        return fitnessValue;
    }
}
