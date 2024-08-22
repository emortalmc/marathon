package dev.emortal.minestom.marathon.options;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum BlockPalette {
    OVERWORLD(
            Material.GRASS_BLOCK,
            Block.DIAMOND_BLOCK,
            "Overworld",
            Block.GRASS_BLOCK,
            Block.OAK_LOG,
            Block.BIRCH_LOG,
            Block.OAK_LEAVES,
            Block.BIRCH_LEAVES,
            Block.DIRT,
            Block.MOSS_BLOCK
    ),
    NETHER(
            Material.NETHERRACK,
            Block.RESPAWN_ANCHOR,
            "The Nether",
            Block.NETHERRACK,
            Block.NETHER_BRICKS,
            Block.BLACKSTONE,
            Block.BASALT,
            Block.OBSIDIAN,
            Block.MAGMA_BLOCK,
            Block.SHROOMLIGHT,
            Block.SOUL_SOIL
    ),
    END(
            Material.DRAGON_EGG,
            Block.BEDROCK,
            "The End",
            Block.END_STONE,
            Block.END_STONE_BRICKS,
            Block.SEA_LANTERN,
            Block.PURPUR_BLOCK,
            Block.PURPUR_PILLAR,
            Block.OBSIDIAN
    ),
    RAINBOW(
            Material.CAKE,
            Block.DIAMOND_BLOCK,
            "Rainbow",
            Block.RED_GLAZED_TERRACOTTA,
            Block.ORANGE_GLAZED_TERRACOTTA,
            Block.YELLOW_GLAZED_TERRACOTTA,
            Block.LIME_GLAZED_TERRACOTTA,
            Block.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Block.BLUE_GLAZED_TERRACOTTA,
            Block.PINK_GLAZED_TERRACOTTA,
            Block.PURPLE_GLAZED_TERRACOTTA
    );

    private final Material icon;
    private final Block starterBlock;
    private final String name;
    private final @NotNull List<Block> blocks;

    BlockPalette(
            @NotNull Material icon,
            @NotNull Block starterBlock,
            @NotNull String name,
            @NotNull Block... blocks) {
        this.icon = icon;
        this.starterBlock = starterBlock;
        this.name = name;
        this.blocks = List.of(blocks);
    }

    public BlockPalette next() {
        return this.ordinal() == values().length - 1
                ? values()[0]
                : values()[ordinal() + 1];
    }

    public @NotNull Material getIcon() {
        return this.icon;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull Block getStarterBlock() {
        return this.starterBlock;
    }

    public boolean isSequential() {
        return this == RAINBOW;
    }

    public @NotNull List<Block> getBlocks() {
        return this.blocks;
    }
}
