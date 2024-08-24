package dev.emortal.minestom.marathon

import dev.emortal.minestom.marathon.animator.BlockAnimator
import dev.emortal.minestom.marathon.animator.PathAnimator
import dev.emortal.minestom.marathon.generator.DefaultGenerator
import dev.emortal.minestom.marathon.generator.Generator
import dev.emortal.minestom.marathon.options.BlockPalette
import dev.emortal.minestom.marathon.options.Time
import dev.emortal.minestom.marathon.util.BlockPacketUtils
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.player.PlayerChunkUnloadEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.network.ConnectionState
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.sound.SoundEvent
import net.minestom.server.tag.Tag
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.MathUtils
import net.minestom.server.world.DimensionType
import java.text.SimpleDateFormat
import java.util.*
import java.util.List
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier
import kotlin.collections.Collection
import kotlin.math.floor

class MarathonGame internal constructor(private val player: Player, dimension: DynamicRegistry.Key<DimensionType?>) {
    val instance: Instance
    private val generator: Generator = DefaultGenerator.Companion.INSTANCE
    private val animator: BlockAnimator = PathAnimator()
    private var palette: BlockPalette
    private var time: Time

    private val blocks = ArrayDeque<Point>(NEXT_BLOCKS_COUNT + 1)

    private var score = 0
    private var combo = 0

    //    private int targetX;
    private var targetY = 0
    var isRunInvalidated: Boolean = false
    private var lastBlockTimestamp: Long = 0
    private var startTimestamp: Long = -1
    private var index = 0

    private var breakingTask: Task? = null

    init {
        this.palette = BlockPalette.OVERWORLD
        this.time = Time.MIDNIGHT

        this.instance = MinecraftServer.getInstanceManager().createInstanceContainer(dimension)
        instance.setTimeRate(0)
        instance.setTime(time.time)
        instance.setTimeSynchronizationTicks(0)
        instance.eventNode().addListener(PlayerChunkUnloadEvent::class.java) { event: PlayerChunkUnloadEvent ->
            val chunk = instance.getChunk(event.chunkX, event.chunkZ) ?: return@addListener
            if (chunk.viewers.isEmpty()) instance.unloadChunk(chunk)
        }

        this.startRefreshDisplaysTask()
        this.reset()
    }

    fun onJoin(player: Player) {
        player.setGameMode(GameMode.ADVENTURE)
        player.respawnPoint = RESET_POINT.add(0.0, 1.0, 0.0)
        player.eventNode()
            .addListener(PlayerSpawnEvent::class.java) { event: PlayerSpawnEvent? -> this.refreshInventory() }

        player.eventNode().addListener(InventoryPreClickEvent::class.java) { event: InventoryPreClickEvent ->
            event.isCancelled = true
            if (event.clickType != ClickType.LEFT_CLICK) {
                return@addListener
            }
            if (event.slot == TIME_SLOT) {
                this.time = time.next()
                event.instance.time = time.time
                this.playClickSound()
                this.refreshInventory()
            } else if (event.slot == PALETTE_SLOT) {
                this.palette = palette.next()
                this.playClickSound()
                this.refreshInventory()
            }
        }
    }

    fun cleanUp() {
        instance.scheduleNextTick { instance: Instance? ->
            MinecraftServer.getInstanceManager().unregisterInstance(
                instance!!
            )
        }
    }

    fun refreshInventory() {
        player.inventory.setItemStack(
            TIME_SLOT, ItemStack.of(Material.CLOCK)
                .with(ItemComponent.ITEM_NAME, Component.text("Time of Day", NamedTextColor.GREEN))
                .with(
                    ItemComponent.LORE, List.of<Component>(
                        Component.text(time.getName(), NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
                    )
                )
        )

        player.inventory.setItemStack(
            PALETTE_SLOT, ItemStack.of(
                palette.icon
            )
                .with(ItemComponent.ITEM_NAME, Component.text("Block Palette", NamedTextColor.GREEN))
                .with(
                    ItemComponent.LORE, List.of<Component>(
                        Component.text(palette.getName(), NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
                    )
                )
        )
    }

    fun reset() {
        if (this.breakingTask != null) {
            breakingTask!!.cancel()
            this.breakingTask = null
        }

        this.score = 0
        this.combo = 0
        this.index = 0
        this.isRunInvalidated = false
        this.lastBlockTimestamp = 0
        this.startTimestamp = -1

        for (block in this.blocks) {
            animator.destroyBlockAnimated(this.instance, block!!, Block.AIR)
        }
        animator.reset()

        for (entity in instance.entities) {
            if (entity.hasTag(MARATHON_ENTITY_TAG)) entity.remove()
        }

        blocks.clear()
        blocks.addLast(RESET_POINT)

        instance.setBlock(RESET_POINT, palette.starterBlock)

        this.generateNextBlocks(NEXT_BLOCKS_COUNT, false)

        this.refreshDisplays()
    }

    fun teleportPlayerToStart() {
        player.teleport(player.respawnPoint)
    }

    private fun startRefreshDisplaysTask() {
        instance.scheduler().buildTask { this.refreshDisplays() }.repeat(TaskSchedule.tick(20)).schedule()
    }

    private fun refreshDisplays() {
        // the player is added to the game during the configuration state,
        // this stops action bars from being sent to the player during that time
        if (player.playerConnection.connectionState != ConnectionState.PLAY) return

        val millisTaken = if (this.startTimestamp == -1L) 0 else System.currentTimeMillis() - this.startTimestamp
        val formattedTime = DATE_FORMAT.format(Date(millisTaken))
        val secondsTaken = millisTaken / 1000.0
        val scorePerSecond = if (this.score < 5) "-.-" else MathUtils.clamp(
            floor(
                this.score / secondsTaken * 10.0
            ) / 10.0, 0.0, 9.9
        ).toString()

        val message: Component = Component.text()
            .append(Component.text(this.score, TextColor.fromHexString("#ff00a6"), TextDecoration.BOLD))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text(formattedTime, NamedTextColor.GRAY))
            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
            .append(Component.text(scorePerSecond + "bps", NamedTextColor.GRAY))
            .build()

        player.sendActionBar(message)

        //        this.player.showTitle(Title.title(
//                Component.empty(),
//                Component.text(score, MARATHON_COLOR),
//                Title.Times.times(Duration.ZERO, Duration.ofMillis(600), Duration.ofMillis(200))
//        ));
    }

    private fun generateNextBlock(shouldAnimate: Boolean) {
        if (blocks.size > NEXT_BLOCKS_COUNT) {
            val firstBlockPos = blocks.first
            animator.destroyBlockAnimated(this.instance, firstBlockPos, Block.AIR)
            animator.destroyBlockAnimated(this.instance, firstBlockPos.add(0.0, 1.0, 0.0), Block.AIR)

            blocks.removeFirst()
        }

        val lastBlockPos = blocks.last
        val nextBlockPos =
            generator.getNextPoint(lastBlockPos, this.targetY, this.score)

        blocks.addLast(nextBlockPos)

        if (lastBlockPos.blockY() == RESET_POINT.blockY()) {
            this.targetY = 0
        } else if (lastBlockPos.blockY() < RESET_POINT.blockY() - 30 || lastBlockPos.blockY() < RESET_POINT.blockY() + 30) {
            this.targetY = RESET_POINT.blockY()
        }

        val random = ThreadLocalRandom.current()
        val blocks = palette.blocks

        val randomBlock = if (palette.isSequential) {
            blocks[index % blocks.size]
        } else {
            blocks[random.nextInt(blocks.size)]
        }

        if (shouldAnimate) {
            animator.setBlockAnimated(this.instance, nextBlockPos, randomBlock!!, lastBlockPos)
        } else {
            instance.setBlock(nextBlockPos, randomBlock!!)
        }

        index++
    }

    fun generateNextBlocks(blockCount: Int, shouldAnimate: Boolean) {
        if (this.startTimestamp == -1L && shouldAnimate) {
            this.beginTimer()
        }

        for (i in 0 until blockCount) {
            this.generateNextBlock(shouldAnimate)
        }

        if (shouldAnimate) {
            this.score += blockCount

            val powerResult: Double = 2.pow(this.combo / 45.0)
            val maxTimeTaken = 1000L * blockCount / powerResult

            if (System.currentTimeMillis() - this.lastBlockTimestamp < maxTimeTaken) {
                this.combo += blockCount
            } else {
                this.combo = 0
            }

            this.playSound(this.combo)
            this.createBreakingTask()

            // TODO: Reached highscore notification
            this.refreshDisplays()
        }

        this.lastBlockTimestamp = System.currentTimeMillis()
    }

    private fun playSound(combo: Int) {
        val pitch = 0.9f + (combo - 1) * 0.05f
        val comboSound = Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_BASS, Sound.Source.MASTER, 1f, pitch)
        player.playSound(comboSound, Sound.Emitter.self())
    }

    private fun createBreakingTask() {
        if (this.breakingTask != null) breakingTask!!.cancel()

        this.breakingTask = instance.scheduler().submitTask(object : Supplier<TaskSchedule> {
            var destroyStage: Byte = 0
            var firstIter: Boolean = true

            override fun get(): TaskSchedule {
                if (this.firstIter) {
                    this.firstIter = false
                    return TaskSchedule.tick(40)
                }

                val block = blocks.first
                this.destroyStage = (this.destroyStage + 2).toByte()

                if (this.destroyStage > 8) {
                    this@MarathonGame.generateNextBlock(false)
                    this@MarathonGame.createBreakingTask()
                }

                val breakingSound = Sound.sound(SoundEvent.BLOCK_WOOD_HIT, Sound.Source.MASTER, 0.5f, 1f)
                player.playSound(breakingSound, Sound.Emitter.self())
                BlockPacketUtils.Companion.sendBlockDamage(this@MarathonGame.instance, block, this.destroyStage)

                return TaskSchedule.tick(10)
            }
        })
    }

    fun beginTimer() {
        this.startTimestamp = System.currentTimeMillis()
    }

    fun getBlocks(): Collection<Point> {
        return this.blocks
    }

    private fun playClickSound() {
        player.playSound(
            Sound.sound(
                SoundEvent.UI_BUTTON_CLICK,
                Sound.Source.MASTER,
                1.0f,
                1.0f
            )
        )
    }

    companion object {
        const val TIME_SLOT: Int = 20
        const val PALETTE_SLOT: Int = 24

        private const val NEXT_BLOCKS_COUNT = 7
        private val RESET_POINT = Pos(0.5, 150.0, 0.5)
        private val DATE_FORMAT = SimpleDateFormat("mm:ss")
        val MARATHON_ENTITY_TAG: Tag<Boolean> = Tag.Boolean("marathonEntity")
    }
}
