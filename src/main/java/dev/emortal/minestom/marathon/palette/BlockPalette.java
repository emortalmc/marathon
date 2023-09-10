package dev.emortal.minestom.marathon.palette;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

public enum BlockPalette {

    OVERWORLD(
            Material.GRASS_BLOCK,
            Component.text("Grass", NamedTextColor.GREEN),
            SoundEvent.BLOCK_GRASS_BREAK,
            Block.GRASS_BLOCK,
            Block.OAK_LOG,
            Block.BIRCH_LOG,
            Block.OAK_LEAVES,
            Block.BIRCH_LEAVES,
            Block.DIRT,
            Block.MOSS_BLOCK
    );

    private final @NotNull Block[] blocks;

    BlockPalette(@NotNull Material displayItem, @NotNull Component display, @NotNull SoundEvent soundEffect, @NotNull Block... blocks) {
        this.blocks = blocks;
    }

    public @NotNull Block[] getBlocks() {
        return this.blocks;
    }
}
