package dev.emortal.minestom.marathon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EnumLore {

    public static <T> @NotNull List<Component> createLore(@NotNull T current, @NotNull T[] values, @Nullable Function<T, String> nameFunction) {
        List<Component> lore = new ArrayList<>();
        for (T loopValue : values) {
            String name = "> " + (nameFunction != null ? nameFunction.apply(loopValue) : loopValue.toString());
            NamedTextColor colour = loopValue == current ? NamedTextColor.GREEN : NamedTextColor.RED;
            lore.add(Component.text(name, colour).decoration(TextDecoration.ITALIC, false));
        }
        return lore;
    }

    public static <T> @NotNull List<Component> createLore(@NotNull T current, @NotNull T[] values) {
        return createLore(current, values, null);
    }
}
