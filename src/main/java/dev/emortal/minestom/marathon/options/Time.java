package dev.emortal.minestom.marathon.options;

import org.jetbrains.annotations.NotNull;

public enum Time {
    DAY(1000, "Day"),
    NOON(6000, "Noon"),
    NIGHT(13000, "Night"),
    MIDNIGHT(18000, "Midnight");

    private final long time;
    private final String friendlyName;

    Time(long time, @NotNull String friendlyName) {
        this.time = time;
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return this.friendlyName;
    }

    public Time next() {
        return this.ordinal() == values().length - 1
                ? values()[0]
                : values()[ordinal() + 1];
    }

    public long getTime() {
        return this.time;
    }
}
