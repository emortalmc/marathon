package dev.emortal.minestom.marathon.options;

import dev.emortal.minestom.marathon.animator.BlockAnimator;
import dev.emortal.minestom.marathon.animator.NoAnimator;
import dev.emortal.minestom.marathon.animator.PathAnimator;
import dev.emortal.minestom.marathon.animator.RiseAnimator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum BlockAnimation {
    POPOUT(PathAnimator::new, "Popout"),
    RISE(RiseAnimator::new, "Rise"),
    NONE(NoAnimator::new, "None");

    private final @NotNull Supplier<@NotNull BlockAnimator> animatorSupplier;
    private final @NotNull String friendlyName;

    BlockAnimation(@NotNull Supplier<@NotNull BlockAnimator> animatorSupplier, @NotNull String friendlyName) {
        this.animatorSupplier = animatorSupplier;
        this.friendlyName = friendlyName;
    }

    public @NotNull BlockAnimator createAnimator() {
        return this.animatorSupplier.get();
    }

    public @NotNull String getFriendlyName() {
        return this.friendlyName;
    }

    public BlockAnimation next() {
        return this.ordinal() == values().length - 1
                ? values()[0]
                : values()[ordinal() + 1];
    }
}
