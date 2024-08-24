package dev.emortal.minestom.marathon.options

enum class Time(val time: Long, override val name: String) {
    DAY(1000, "Day"),
    NOON(6000, "Noon"),
    NIGHT(13000, "Night"),
    MIDNIGHT(18000, "Midnight");

    fun next(): Time {
        return if (this.ordinal == entries.size - 1
        ) entries[0]
        else entries[ordinal + 1]
    }
}
