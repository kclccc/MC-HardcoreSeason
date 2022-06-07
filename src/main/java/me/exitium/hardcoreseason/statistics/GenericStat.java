package me.exitium.hardcoreseason.statistics;

import org.jetbrains.annotations.NotNull;

public class GenericStat implements Comparable<GenericStat> {

    String name;
    int count;

    public GenericStat(String name) {
        this.name = name;
    }

    GenericStat(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull GenericStat o) {
        return Integer.compare(this.count, o.count);
    }
}
