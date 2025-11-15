package com.example.recycler.reward;

import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnchantmentRollerTest {
    private RecyclerSettings settings;
    private ItemTierService tierService;
    private WeightedSelector selector;

    @BeforeEach
    void setUp() {
        settings = RecyclerSettings.builder()
                .fullStackMin(16)
                .enchantChance(1.0)
                .doubleRewardChance(0.0)
                .itemDropPenaltyMultiplier(1.0)
                .tierEnchantDivisor(ItemTier.NETHERITE, 2.0)
                .tierEnchantDivisor(ItemTier.STONE, 0.5)
                .tierEnchantDivisor(ItemTier.NEUTRAL, 1.0)
                .enchantCountWeight(1, 10.0)
                .enchantCountWeight(2, 20.0)
                .enchantCountWeight(3, 30.0)
                .build();
        tierService = new ItemTierService();
        selector = new MaxWeightSelector();
    }

    @Test
    void highTierItemsFavorLowerEnchantCounts() {
        EnchantmentRoller roller = new EnchantmentRoller(settings, tierService, selector, ThreadLocalRandom.current());
        int count = roller.rollEnchantmentCount(Material.NETHERITE_SWORD, 5);
        assertEquals(2, count, "High-tier items should have reduced weight for high enchant counts");
    }

    @Test
    void lowTierItemsAllowHigherEnchantCounts() {
        EnchantmentRoller roller = new EnchantmentRoller(settings, tierService, selector, ThreadLocalRandom.current());
        int count = roller.rollEnchantmentCount(Material.STONE_SWORD, 5);
        assertEquals(3, count, "Low-tier items should more readily reach higher enchant counts");
    }

    @Test
    void neverRequestsMoreThanAvailableEnchantments() {
        EnchantmentRoller roller = new EnchantmentRoller(settings, tierService, selector, ThreadLocalRandom.current());
        int count = roller.rollEnchantmentCount(Material.STONE_SWORD, 1);
        assertEquals(1, count, "Requested enchantments must not exceed available options");
    }

    private static final class MaxWeightSelector extends WeightedSelector {
        private MaxWeightSelector() {
            super(new Random(0));
        }

        @Override
        public <T> T pick(List<WeightedChoice<T>> choices) {
            return choices.stream()
                    .max(Comparator.comparingDouble(WeightedChoice::weight))
                    .map(WeightedChoice::value)
                    .orElse(null);
        }
    }
}
