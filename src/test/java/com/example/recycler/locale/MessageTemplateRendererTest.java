package com.example.recycler.locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTemplateRendererTest {

    @Test
    void replacesStringAndComponentPlaceholders() {
        Component itemComponent = Component.text("Diamond Sword", NamedTextColor.GOLD);
        Component result = MessageTemplateRenderer.render(
                "Reward: {item} x{amount}",
                NamedTextColor.GREEN,
                Map.of("item", itemComponent),
                Map.of("amount", "2"));

        Component expected = Component.text()
                .color(NamedTextColor.GREEN)
                .append(Component.text("Reward: ", NamedTextColor.GREEN))
                .append(itemComponent)
                .append(Component.text(" x", NamedTextColor.GREEN))
                .append(Component.text("2", NamedTextColor.GREEN))
                .build();
        assertEquals(expected, result);
    }

    @Test
    void leavesUnknownPlaceholderLiteral() {
        Component result = MessageTemplateRenderer.render(
                "Hello {player} {unknown}",
                NamedTextColor.WHITE,
                Map.of(),
                Map.of("player", "Alex"));

        Component expected = Component.text()
                .color(NamedTextColor.WHITE)
                .append(Component.text("Hello ", NamedTextColor.WHITE))
                .append(Component.text("Alex", NamedTextColor.WHITE))
                .append(Component.text(" ", NamedTextColor.WHITE))
                .append(Component.text("{unknown}", NamedTextColor.WHITE))
                .build();
        assertEquals(expected, result);
    }
}
