package dev.emortal.minestom.marathon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface EnumLore {
    static <T> @NotNull List<Component> createLore(@NotNull T current, @NotNull T[] values, @NotNull Function<T, String> nameFunction) {
        List<Component> lore = new ArrayList<>();

        for (T loopValue : values) {
            boolean selected = current.equals(loopValue);

            Component prefix = selected
                    ? Component.text("→", NamedTextColor.WHITE)
                    : Component.text("  ");

            Component name = Component.text(
                    nameFunction.apply(loopValue),
                    selected ? NamedTextColor.WHITE : NamedTextColor.GRAY);

            lore.add(Component.textOfChildren(prefix, Component.space(), name)
                    .decoration(TextDecoration.ITALIC, false));
        }

        return lore;
    }

    static <T> @NotNull List<Component> createLore(@NotNull T current, @NotNull T[] values) {
        return createLore(current, values, Object::toString);
    }
}
