package com.example.recycler.locale;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TranslationServiceTest {

    private final TranslationService translationService = new TranslationService();

    @Test
    void fallsBackToPlainTextForNullMaterial() {
        Component component = translationService.material(null);
        assertEquals(Component.text("unknown material"), component);
    }

    @Test
    void gracefullyFallsBackWhenRegistryUnavailable() {
        Component component = translationService.material(Material.DIAMOND_SWORD);
        assertEquals(Component.text("diamond sword"), component);
    }
}
