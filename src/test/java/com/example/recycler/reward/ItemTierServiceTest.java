package com.example.recycler.reward;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTierServiceTest {
    private final ItemTierService service = new ItemTierService();

    @Test
    void detectsNetheriteItems() {
        assertEquals(ItemTier.NETHERITE, service.resolve(Material.NETHERITE_SWORD));
    }

    @Test
    void detectsChainmailPieces() {
        assertEquals(ItemTier.CHAIN, service.resolve(Material.CHAINMAIL_CHESTPLATE));
    }

    @Test
    void defaultsToNeutralForOtherItems() {
        assertEquals(ItemTier.NEUTRAL, service.resolve(Material.CARROT));
    }
}
