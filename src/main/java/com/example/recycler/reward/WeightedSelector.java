package com.example.recycler.reward;

import java.util.List;
import java.util.Random;

/**
 * Utility to pick an element according to weights.
 */
public class WeightedSelector {
    private final Random random;

    public WeightedSelector(Random random) {
        this.random = random;
    }

    public <T> T pick(List<WeightedChoice<T>> choices) {
        if (choices.isEmpty()) {
            return null;
        }
        double totalWeight = choices.stream().mapToDouble(WeightedChoice::weight).sum();
        if (totalWeight <= 0) {
            return choices.get(random.nextInt(choices.size())).value();
        }
        double roll = random.nextDouble(totalWeight);
        for (WeightedChoice<T> choice : choices) {
            roll -= choice.weight();
            if (roll <= 0) {
                return choice.value();
            }
        }
        return choices.get(choices.size() - 1).value();
    }

    public record WeightedChoice<T>(T value, double weight) {
    }
}
