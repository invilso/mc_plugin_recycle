package com.example.recycler.reward;

import org.bukkit.Material;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Requires Paper registry access which isn't available in headless unit tests")
class RewardPoolsTest {

    @Test
    void blockRewardsOnlyContainItemBackedBlocks() {
        RewardPools pools = new RewardPools(RecyclerSettings.defaults());
        assertFalse(pools.getBlockRewards().isEmpty(), "Block reward pool should not be empty");
        for (Material material : pools.getBlockRewards()) {
            assertTrue(material.isBlock(), material + " must be a block");
            assertTrue(material.isItem(), material + " must have an item form");
        }
    }

    @Test
    void itemRewardsExcludeBlocks() {
        RewardPools pools = new RewardPools(RecyclerSettings.defaults());
        assertFalse(pools.getItemRewards().isEmpty(), "Item reward pool should not be empty");
        for (Material material : pools.getItemRewards()) {
            assertTrue(material.isItem(), material + " must be an item");
            assertFalse(material.isBlock(), material + " must not be a block");
        }
    }
}
