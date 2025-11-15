package com.example.recycler.reward;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class RewardSelector {
    private final RecyclerSettings settings;
    private final ItemTierService itemTierService;
    private final WeightedSelector weightedSelector;

    public RewardSelector(RecyclerSettings settings, ItemTierService itemTierService, WeightedSelector weightedSelector) {
        this.settings = settings;
        this.itemTierService = itemTierService;
        this.weightedSelector = weightedSelector;
    }

    public Material pickRewardMaterial(Material avoid,
                                       List<Material> pool,
                                       boolean useTierWeights,
                                       Predicate<Material> cooldownActive) {
        if (pool.isEmpty()) {
            return null;
        }

        List<WeightedSelector.WeightedChoice<Material>> primary = new ArrayList<>();
        List<WeightedSelector.WeightedChoice<Material>> secondary = new ArrayList<>();
        List<WeightedSelector.WeightedChoice<Material>> fallback = new ArrayList<>();

        for (Material material : pool) {
            if (cooldownActive.test(material)) {
                continue;
            }
            double weight = useTierWeights ? computeWeight(material) : 1.0;
            if (weight <= 0) {
                continue;
            }
            if (material == avoid) {
                secondary.add(new WeightedSelector.WeightedChoice<>(material, weight));
            } else {
                primary.add(new WeightedSelector.WeightedChoice<>(material, weight));
            }
        }

        if (!primary.isEmpty()) {
            return weightedSelector.pick(primary);
        }
        if (!secondary.isEmpty()) {
            return weightedSelector.pick(secondary);
        }

        for (Material material : pool) {
            if (material == avoid) {
                continue;
            }
            double weight = useTierWeights ? computeWeight(material) : 1.0;
            if (weight > 0) {
                fallback.add(new WeightedSelector.WeightedChoice<>(material, weight));
            }
        }
        if (fallback.isEmpty()) {
            for (Material material : pool) {
                double weight = useTierWeights ? computeWeight(material) : 1.0;
                if (weight > 0) {
                    fallback.add(new WeightedSelector.WeightedChoice<>(material, weight));
                }
            }
        }
        return fallback.isEmpty() ? null : weightedSelector.pick(fallback);
    }

    private double computeWeight(Material material) {
        ItemTier tier = itemTierService.resolve(material);
        double divisor = settings.getDivisorFor(tier);
        if (divisor <= 0) {
            return 0;
        }
        if (divisor == 1.0) {
            return 1.0;
        }
        return 1.0 / (divisor * settings.getItemDropPenaltyMultiplier());
    }
}
