package com.example.recycler.reward;

import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

/**
 * Central place for tweakable recycler parameters.
 */
public final class RecyclerSettings {
    private final int fullStackMin;
    private final double enchantChance;
    private final double doubleRewardChance;
    private final double itemDropPenaltyMultiplier;
    private final Map<ItemTier, Double> tierEnchantDivisors;
    private final Map<Integer, Double> enchantCountWeights;
    private final Set<Material> excludedMaterials;

    private RecyclerSettings(Builder builder) {
        this.fullStackMin = builder.fullStackMin;
        this.enchantChance = builder.enchantChance;
        this.doubleRewardChance = builder.doubleRewardChance;
        this.itemDropPenaltyMultiplier = builder.itemDropPenaltyMultiplier;
        this.tierEnchantDivisors = Map.copyOf(builder.tierEnchantDivisors);
        this.enchantCountWeights = Map.copyOf(builder.enchantCountWeights);
        this.excludedMaterials = Set.copyOf(builder.excludedMaterials);
    }

    public static RecyclerSettings defaults() {
        return new Builder()
                .fullStackMin(16)
                .enchantChance(0.7)
                .doubleRewardChance(0.05)
                .itemDropPenaltyMultiplier(5.0)
                .tierEnchantDivisor(ItemTier.NETHERITE, 2.0)
                .tierEnchantDivisor(ItemTier.DIAMOND, 1.7)
                .tierEnchantDivisor(ItemTier.GOLD, 1.3)
                .tierEnchantDivisor(ItemTier.IRON, 1.5)
                .tierEnchantDivisor(ItemTier.STONE, 0.8)
                .tierEnchantDivisor(ItemTier.CHAIN, 0.9)
                .tierEnchantDivisor(ItemTier.NEUTRAL, 1.0)
                .enchantCountWeight(5, 5.0)
                .enchantCountWeight(4, 10.0)
                .enchantCountWeight(3, 25.0)
                .enchantCountWeight(2, 35.0)
                .enchantCountWeight(1, 40.0)
                .exclude(Material.EGG)
                .excludePrefix("MUSIC_DISC")
                .excludeSuffix("_SPAWN_EGG")
                .excludeSuffix("_SMITHING_TEMPLATE")
                .build();
    }

    public int getFullStackMin() {
        return fullStackMin;
    }

    public double getEnchantChance() {
        return enchantChance;
    }

    public double getDoubleRewardChance() {
        return doubleRewardChance;
    }

    public double getItemDropPenaltyMultiplier() {
        return itemDropPenaltyMultiplier;
    }

    public Map<ItemTier, Double> getTierEnchantDivisors() {
        return tierEnchantDivisors;
    }

    public Map<Integer, Double> getEnchantCountWeights() {
        return enchantCountWeights;
    }

    public Set<Material> getExcludedMaterials() {
        return excludedMaterials;
    }

    public double getDivisorFor(ItemTier tier) {
        return tierEnchantDivisors.getOrDefault(tier, 1.0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int fullStackMin = 16;
        private double enchantChance = 0.7;
        private double doubleRewardChance = 0.05;
        private double itemDropPenaltyMultiplier = 5.0;
        private final Map<ItemTier, Double> tierEnchantDivisors = new java.util.EnumMap<>(ItemTier.class);
        private final Map<Integer, Double> enchantCountWeights = new java.util.HashMap<>();
        private final Set<Material> excludedMaterials = new java.util.HashSet<>();
        private final Set<String> excludedPrefixes = new java.util.HashSet<>();
        private final Set<String> excludedSuffixes = new java.util.HashSet<>();

        public Builder fullStackMin(int value) {
            this.fullStackMin = value;
            return this;
        }

        public Builder enchantChance(double chance) {
            this.enchantChance = chance;
            return this;
        }

        public Builder doubleRewardChance(double chance) {
            this.doubleRewardChance = chance;
            return this;
        }

        public Builder itemDropPenaltyMultiplier(double multiplier) {
            this.itemDropPenaltyMultiplier = multiplier;
            return this;
        }

        public Builder tierEnchantDivisor(ItemTier tier, double divisor) {
            this.tierEnchantDivisors.put(tier, divisor);
            return this;
        }

        public Builder enchantCountWeight(int enchantCount, double weight) {
            this.enchantCountWeights.put(enchantCount, weight);
            return this;
        }

        public Builder exclude(Material material) {
            this.excludedMaterials.add(material);
            return this;
        }

        public Builder excludePrefix(String prefix) {
            this.excludedPrefixes.add(prefix);
            return this;
        }

        public Builder excludeSuffix(String suffix) {
            this.excludedSuffixes.add(suffix);
            return this;
        }

        public RecyclerSettings build() {
            // Expand prefix/suffix exclusions into actual materials at build time.
            if (!excludedPrefixes.isEmpty() || !excludedSuffixes.isEmpty()) {
                for (Material material : Material.values()) {
                    if (material == Material.AIR) {
                        continue;
                    }
                    String name = material.name();
                    boolean matchesPrefix = excludedPrefixes.stream().anyMatch(name::startsWith);
                    boolean matchesSuffix = excludedSuffixes.stream().anyMatch(name::endsWith);
                    if (matchesPrefix || matchesSuffix) {
                        excludedMaterials.add(material);
                    }
                }
            }
            return new RecyclerSettings(this);
        }
    }
}
