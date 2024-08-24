package dev.emortal.minestom.marathon.options

import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material

enum class BlockPalette(
    val icon: Material,
    val starterBlock: Block,
    override val name: String,
    vararg blocks: Block
) {
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

    val blocks: List<Block> = java.util.List.of(*blocks)

    fun next(): BlockPalette {
        return if (this.ordinal == entries.size - 1
        ) entries[0]
        else entries[ordinal + 1]
    }

    val isSequential: Boolean
        get() = this == RAINBOW
}
