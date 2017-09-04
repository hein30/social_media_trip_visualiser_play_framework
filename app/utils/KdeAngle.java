package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import weka.estimators.KernelEstimator;

public class KdeAngle {

  public static Optional<Double> getDominantDirection(Map<Double, Integer> map) {

    if (!map.isEmpty()) {
      KernelEstimator estimator = new KernelEstimator(0.1);
      map.entrySet().forEach(entry -> estimator.addValue(entry.getKey(), entry.getValue()));

      List<Double> densities = new ArrayList<>();
      double degree = 0;
      while (degree <= 180) {
        densities.add(estimator.getProbability(degree));
        degree = degree + 0.1;
      }

      Double max = densities.stream().max(Comparator.comparingDouble(d -> d)).get();
      int indexOfMax = densities.indexOf(max);
      double sum = densities
          .subList(Math.max(0, indexOfMax - 50), Math.min(densities.size(), indexOfMax + 50))
          .stream().mapToDouble(Double::doubleValue).sum();

      if (sum > 0.03) {
        return Optional.of((double) indexOfMax / 10);
      }
    }

    return Optional.empty();
  }
}
