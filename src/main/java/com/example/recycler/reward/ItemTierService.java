package com.example.recycler.reward;

import org.bukkit.Material;

/**
 * Resolves material tiers for weighting logic.
 */
public final class ItemTierService {

    public ItemTier resolve(Material material) {
        if (material == null) {
            return ItemTier.NEUTRAL;
        }
        if (material.name().contains("NETHERITE")) {
            return ItemTier.NETHERITE;
        }
        if (material.name().contains("DIAMOND")) {
            return ItemTier.DIAMOND;
        }
        if (material.name().contains("GOLD")) {
            return ItemTier.GOLD;
        }
        if (material.name().contains("IRON")) {
            return ItemTier.IRON;
        }
        if (material.name().startsWith("CHAINMAIL_")) {
            return ItemTier.CHAIN;
        }
        if (material.name().startsWith("STONE_")) {
            return ItemTier.STONE;
        }
        return ItemTier.NEUTRAL;
    }
}
