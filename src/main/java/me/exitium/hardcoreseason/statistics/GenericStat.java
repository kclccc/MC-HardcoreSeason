package me.exitium.hardcoreseason.statistics;

import org.jetbrains.annotations.NotNull;

public record GenericStat(String name, int count) implements Comparable<GenericStat> {

    @Override
    public int compareTo(@NotNull GenericStat o) {
        return Integer.compare(this.count, o.count);
    }
}
