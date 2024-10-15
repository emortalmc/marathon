package dev.emortal.minestom.marathon.options;

import dev.emortal.minestom.marathon.animator.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum BlockAnimation {
    POPOUT(PathAnimator::new, "Pop Out"),
    RISE(RiseAnimator::new, "Rise"),
    SUVAT(SuvatAnimator::new, "Jump"),
    SCALE(ScaleAnimator::new, "Grow"),
    NONE(NoAnimator::new, "None");

    private final @NotNull Supplier<@NotNull BlockAnimator> animatorSupplier;
    private final @NotNull String friendlyName;

    BlockAnimation(@NotNull Supplier<@NotNull BlockAnimator> animatorSupplier, @NotNull String friendlyName) {
        this.animatorSupplier = animatorSupplier;
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }

    public @NotNull BlockAnimator createAnimator() {
        return this.animatorSupplier.get();
    }

    public BlockAnimation next() {
        return this.ordinal() == values().length - 1
                ? values()[0]
                : values()[ordinal() + 1];
    }
}
