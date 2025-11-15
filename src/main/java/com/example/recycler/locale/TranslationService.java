package com.example.recycler.locale;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.Locale;

public class TranslationService {

    public Component material(Material material) {
        if (material == null) {
            return Component.text("unknown material");
        }
        try {
            String translationKey = material.translationKey();
            if (translationKey != null && !translationKey.isBlank()) {
                return Component.translatable(translationKey);
            }
        } catch (IllegalStateException | ExceptionInInitializerError ex) {
            // Server registries are not available in unit tests, fall back to plain text instead.
        }
        String fallback = material.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return Component.text(fallback);
    }
}
