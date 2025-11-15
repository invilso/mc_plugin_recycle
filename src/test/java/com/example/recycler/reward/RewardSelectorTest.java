package com.example.recycler.reward;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardSelectorTest {
    private final RecyclerSettings settings = RecyclerSettings.defaults();
    private final ItemTierService tierService = new ItemTierService();

    private RewardSelector selector(Random random) {
        return new RewardSelector(settings, tierService, new WeightedSelector(random));
    }

    @Test
    void prefersNonAvoidMaterialWhenAlternativeExists() {
        RewardSelector selector = selector(new Random(0));
        Material result = selector.pickRewardMaterial(
                Material.DIRT,
                List.of(Material.DIRT, Material.STONE),
                true,
                material -> false);
        assertEquals(Material.STONE, result, "Should prioritize a different item than the avoided input");
    }

    @Test
    void omitsMaterialsThatAreOnCooldown() {
        RewardSelector selector = selector(new Random(1));
        Material result = selector.pickRewardMaterial(
                Material.DIAMOND_BLOCK,
                List.of(Material.DIAMOND_BLOCK, Material.GOLD_BLOCK),
                false,
                material -> material == Material.DIAMOND_BLOCK);
        assertEquals(Material.GOLD_BLOCK, result, "Cooldown predicate must filter blocked rewards");
    }

    @Test
    void fallsBackToAvoidWhenOnlyEligibleOption() {
        RewardSelector selector = selector(new Random(2));
        Material result = selector.pickRewardMaterial(
                Material.GOLDEN_SWORD,
                List.of(Material.GOLDEN_SWORD),
                true,
                material -> false);
        assertEquals(Material.GOLDEN_SWORD, result, "Should still return avoid material when it is the only choice");
    }
}
