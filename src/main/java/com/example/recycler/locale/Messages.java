package com.example.recycler.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public final class Messages {
    private final JavaPlugin plugin;
    private final String defaultLocale;
    private final Map<String, Map<String, String>> bundles = new HashMap<>();

    public Messages(JavaPlugin plugin, String defaultLocale, List<String> supportedLocales) {
        this.plugin = plugin;
        this.defaultLocale = normalizeCode(defaultLocale);
        for (String localeCode : supportedLocales) {
            bundles.put(normalizeCode(localeCode), loadBundle(localeCode));
        }
        bundles.computeIfAbsent(this.defaultLocale, code -> loadBundle(this.defaultLocale));
    }

    public Component guiTitle(Locale locale) {
        return component(locale, "gui-title", NamedTextColor.WHITE);
    }

    public Component component(Locale locale, String key, NamedTextColor color) {
        return component(locale, key, color, Collections.emptyMap(), Collections.emptyMap());
    }

    public Component component(Locale locale, String key, NamedTextColor color, Map<String, String> stringPlaceholders) {
        return component(locale, key, color, Collections.emptyMap(), stringPlaceholders);
    }

    public Component component(Locale locale,
                               String key,
                               NamedTextColor color,
                               Map<String, Component> componentPlaceholders,
                               Map<String, String> stringPlaceholders) {
        String template = get(locale, key);
        return MessageTemplateRenderer.render(template, color, componentPlaceholders, stringPlaceholders);
    }

    public String get(Locale locale, String key) {
        String normalized = normalize(locale);
        String resolved = lookup(normalized, key);
        if (resolved == null) {
            plugin.getLogger().warning("Missing message key '" + key + "' for locale " + normalized);
            resolved = key;
        }
        return resolved;
    }

    private String lookup(String locale, String key) {
        Map<String, String> localeBundle = bundles.get(locale);
        if (localeBundle != null && localeBundle.containsKey(key)) {
            return localeBundle.get(key);
        }
        Map<String, String> defaultBundle = bundles.get(defaultLocale);
        if (defaultBundle != null && defaultBundle.containsKey(key)) {
            return defaultBundle.get(key);
        }
        return null;
    }

    private Map<String, String> loadBundle(String localeCode) {
        String normalized = normalizeCode(localeCode);
        String path = "lang/" + normalized + ".yml";
        try (InputStream stream = plugin.getResource(path)) {
            if (stream == null) {
                plugin.getLogger().warning("Missing localization file: " + path);
                return Collections.emptyMap();
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
                ConfigurationSection section = config.getConfigurationSection("messages");
                if (section == null) {
                    return Collections.emptyMap();
                }
                Map<String, String> flattened = new HashMap<>();
                collect(section, "", flattened);
                return flattened;
            }
        } catch (IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to load localization for " + normalized, exception);
            return Collections.emptyMap();
        }
    }

    private void collect(ConfigurationSection section, String prefix, Map<String, String> target) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                ConfigurationSection child = section.getConfigurationSection(key);
                if (child != null) {
                    collect(child, fullKey, target);
                }
            } else {
                String value = section.getString(key, "");
                target.put(fullKey, value);
            }
        }
    }

    private String normalize(Locale locale) {
        if (locale == null) {
            return defaultLocale;
        }
        return normalizeCode(locale.toLanguageTag());
    }

    private String normalizeCode(String localeCode) {
        return localeCode.toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
