package dev.emortal.minestom.marathon.options;

import org.jetbrains.annotations.NotNull;

public enum Time {
    DAY(1000, "Day"),
    NOON(6000, "Noon"),
    NIGHT(13000, "Night"),
    MIDNIGHT(18000, "Midnight");

    private final long time;
    private final String name;

    Time(long time, @NotNull String name) {
        this.time = time;
        this.name = name;
    }

    public Time next() {
        return this.ordinal() == values().length - 1
                ? values()[0]
                : values()[ordinal() + 1];
    }

    public long getTime() {
        return this.time;
    }

    public @NotNull String getName() {
        return this.name;
    }
}
