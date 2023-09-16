package dev.emortal.minestom.marathon;

import dev.emortal.minestom.gamesdk.config.GameCreationInfo;
import dev.emortal.minestom.gamesdk.game.Game;
import dev.emortal.minestom.marathon.animator.BlockAnimator;
import dev.emortal.minestom.marathon.animator.SuvatAnimator;
import dev.emortal.minestom.marathon.generator.DefaultGenerator;
import dev.emortal.minestom.marathon.generator.Generator;
import dev.emortal.minestom.marathon.listener.MovementListener;
import dev.emortal.minestom.marathon.palette.BlockPalette;
import dev.emortal.minestom.marathon.util.BlockPacketUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public final class MarathonGame extends Game {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarathonGame.class);

    static final @NotNull DimensionType FULLBRIGHT_DIMENSION = DimensionType.builder(NamespaceID.from("fullbright")).ambientLight(1F).build();

    private static final int NEXT_BLOCKS_COUNT = 7;
    private static final Pos RESET_POINT = new Pos(0.5, 150, 0.5);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("mm:ss");
    public static final @NotNull Tag<Boolean> MARATHON_ENTITY_TAG = Tag.Boolean("marathonEntity");

    private final @NotNull Instance instance;
    private final @NotNull Generator generator;
    private final @NotNull BlockAnimator animator;
    private final @NotNull BlockPalette palette;

    private final ArrayDeque<Point> blocks = new ArrayDeque<>(NEXT_BLOCKS_COUNT + 1);

    private int score;
    private int combo;
//    private int targetX;
    private int targetY;
    private boolean runInvalidated;
    private long lastBlockTimestamp = 0;
    private long startTimestamp = -1;

    private @Nullable Task breakingTask;

    public MarathonGame(@NotNull GameCreationInfo creationInfo) {
        super(creationInfo);

        this.generator = DefaultGenerator.INSTANCE;
        this.animator = new SuvatAnimator();
        this.palette = BlockPalette.OVERWORLD;

        this.instance = MinecraftServer.getInstanceManager().createInstanceContainer(FULLBRIGHT_DIMENSION);
        this.instance.setTimeRate(0);
        this.instance.setTimeUpdate(null);

        MovementListener movementListener = new MovementListener(this);
        this.getEventNode().addListener(PlayerMoveEvent.class, movementListener::onMove);
    }

    @Override
    public void start() {
        if (this.getCreationInfo().playerIds().size() > 1) {
            LOGGER.error("There should be no more than one player joining Marathon");
            return;
        }

        this.reset();
    }

    @Override
    public void onJoin(@NotNull Player player) {
    }

    @Override
    public void onLeave(@NotNull Player player) {
    }

    @Override
    public void cleanUp() {
        this.instance.scheduleNextTick(MinecraftServer.getInstanceManager()::unregisterInstance);
    }

    public void reset() {
        if (this.breakingTask != null) {
            this.breakingTask.cancel();
            this.breakingTask = null;
        }

        this.score = 0;
        this.combo = 0;
        this.runInvalidated = false;
        this.lastBlockTimestamp = 0;
        this.startTimestamp = System.currentTimeMillis();

        for (Point block : this.blocks) {
            this.animator.destroyBlockAnimated(this.instance, block, Block.AIR);
        }
        this.animator.reset();

        for (Entity entity : this.instance.getEntities()) {
            if (entity.hasTag(MARATHON_ENTITY_TAG)) entity.remove();
        }

        this.blocks.clear();
        this.blocks.addLast(RESET_POINT);

        for (Player player : this.getPlayers()) {
            player.teleport(RESET_POINT.add(0, 1, 0));
        }
        this.instance.setBlock(RESET_POINT, Block.DIAMOND_BLOCK);

        this.onBlockTouch(NEXT_BLOCKS_COUNT, false);
    }

    private void refreshDisplays() {
        long millisTaken = this.startTimestamp == -1 ? 0 : System.currentTimeMillis() - this.startTimestamp;
        String formattedTime = DATE_FORMAT.format(new Date(millisTaken));
        double secondsTaken = millisTaken / 1000.0;
        String scorePerSecond = this.score < 2 ? "-.-" : String.valueOf(MathUtils.clamp(Math.floor(this.score / secondsTaken * 10.0) / 10.0, 0.0, 9.9));

        Component message = Component.text()
                .append(Component.text(this.score, TextColor.fromHexString("#ff00a6"), TextDecoration.BOLD))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(formattedTime, NamedTextColor.GRAY))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(scorePerSecond + "bps", NamedTextColor.GRAY))
                .build();

        this.sendActionBar(message);

//        this.player.showTitle(Title.title(
//                Component.empty(),
//                Component.text(score, MARATHON_COLOR),
//                Title.Times.times(Duration.ZERO, Duration.ofMillis(600), Duration.ofMillis(200))
//        ));
    }

    private void generateNextBlock(boolean inGame) {
        if (this.blocks.size() > NEXT_BLOCKS_COUNT) {
            Point firstBlockPos = this.blocks.getFirst();
            this.animator.destroyBlockAnimated(this.instance, firstBlockPos, Block.AIR);
            this.animator.destroyBlockAnimated(this.instance, firstBlockPos.add(0, 1, 0), Block.AIR);

            this.blocks.removeFirst();
        }

        Point lastBlockPos = this.blocks.getLast();
        Point nextBlockPos = this.generator.getNextPoint(lastBlockPos, this.targetY, this.score);

        this.blocks.addLast(nextBlockPos);

        if (lastBlockPos.blockY() == RESET_POINT.blockY()) {
            this.targetY = 0;
        } else if (lastBlockPos.blockY() < RESET_POINT.blockY() - 30 || lastBlockPos.blockY() < RESET_POINT.blockY() + 30) {
            this.targetY = RESET_POINT.blockY();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Block> blocks = this.palette.getBlocks();
        Block randomBlock = blocks.get(random.nextInt(blocks.size()));

        if (inGame) {
            this.animator.setBlockAnimated(this.instance, nextBlockPos, randomBlock, lastBlockPos);
        } else {
            this.instance.setBlock(nextBlockPos, randomBlock);
        }
    }

    public void onBlockTouch(int blockIndex, boolean inGame) {
        for (int i = 0; i < blockIndex; i++) {
            this.generateNextBlock(inGame);
        }

        if (inGame) {
            this.score += blockIndex;

            double powerResult = Math.pow(2, this.combo / 45.0);
            double maxTimeTaken = 1000L * blockIndex / powerResult;

            if (System.currentTimeMillis() - this.lastBlockTimestamp < maxTimeTaken) {
                this.combo += blockIndex;
            } else {
                this.combo = 0;
            }

            this.playSound(this.combo);
            this.createBreakingTask();

            // TODO: Reached highscore notification

            this.refreshDisplays();
        }

        this.lastBlockTimestamp = System.currentTimeMillis();
    }

    private void playSound(int combo) {
        float pitch = 0.9f + (combo - 1) * 0.05f;
        this.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_BASS, Sound.Source.MASTER, 1f, pitch), Sound.Emitter.self());
    }

    private void createBreakingTask() {
        if (this.breakingTask != null) this.breakingTask.cancel();

        this.breakingTask = this.instance.scheduler().submitTask(new Supplier<>() {
            byte destroyStage = 0;
            boolean firstIter = true;

            @Override
            public TaskSchedule get() {
                if (this.firstIter) {
                    this.firstIter = false;
                    return TaskSchedule.tick(40);
                }

                Point block = MarathonGame.this.blocks.getFirst();
                this.destroyStage += 2;

                if (this.destroyStage > 8) {
                    MarathonGame.this.generateNextBlock(false);
                    MarathonGame.this.createBreakingTask();
                }

                MarathonGame.this.playSound(Sound.sound(SoundEvent.BLOCK_WOOD_HIT, Sound.Source.MASTER, 0.5f, 1f), Sound.Emitter.self());
                BlockPacketUtils.sendBlockDamage(MarathonGame.this.instance, block, this.destroyStage);

                return TaskSchedule.tick(10);
            }
        });
    }

    public @NotNull Collection<Point> getBlocks() {
        return this.blocks;
    }

    public @NotNull Instance getSpawningInstance() {
        return this.instance;
    }

    public boolean isRunInvalidated() {
        return this.runInvalidated;
    }

    public void setRunInvalidated(boolean runInvalidated) {
        this.runInvalidated = runInvalidated;
    }
}
