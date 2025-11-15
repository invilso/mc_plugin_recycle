package com.example.recycler.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Simple placeholder renderer that merges template strings with Adventure components.
 */
final class MessageTemplateRenderer {

    private MessageTemplateRenderer() {
    }

    static Component render(String template,
                            NamedTextColor color,
                            Map<String, Component> componentPlaceholders,
                            Map<String, String> stringPlaceholders) {
        Objects.requireNonNull(color, "color");
        String safeTemplate = template == null ? "" : template;
        Map<String, Component> componentMap = componentPlaceholders == null ? Collections.emptyMap() : componentPlaceholders;
        Map<String, String> stringMap = stringPlaceholders == null ? Collections.emptyMap() : stringPlaceholders;

        TextComponent.Builder builder = Component.text();
        int cursor = 0;
        while (cursor < safeTemplate.length()) {
            int open = safeTemplate.indexOf('{', cursor);
            if (open == -1) {
                builder.append(Component.text(safeTemplate.substring(cursor), color));
                break;
            }
            int close = safeTemplate.indexOf('}', open + 1);
            if (close == -1) {
                builder.append(Component.text(safeTemplate.substring(cursor), color));
                break;
            }

            if (open > cursor) {
                builder.append(Component.text(safeTemplate.substring(cursor, open), color));
            }

            String placeholder = safeTemplate.substring(open + 1, close);
            Component componentReplacement = componentMap.get(placeholder);
            if (componentReplacement != null) {
                builder.append(componentReplacement);
            } else {
                String stringReplacement = stringMap.get(placeholder);
                if (stringReplacement != null) {
                    builder.append(Component.text(stringReplacement, color));
                } else {
                    builder.append(Component.text("{" + placeholder + "}", color));
                }
            }
            cursor = close + 1;
        }

        if (cursor == 0 && safeTemplate.length() == 0) {
            return Component.text("", color);
        }

        return builder.colorIfAbsent(color).build();
    }
}
