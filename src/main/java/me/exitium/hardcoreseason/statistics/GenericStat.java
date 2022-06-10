package me.exitium.hardcoreseason.statistics;

import org.jetbrains.annotations.NotNull;

public class GenericStat implements Comparable<GenericStat> {

    String name;
    int numVar;

    public GenericStat(String name) {
        this.name = name;
    }

    public GenericStat(String name, int numVar) {
        this.name = name;
        this.numVar = numVar;
    }

    public String getName() {
        return name;
    }

    public int getNumVar() {
        return numVar;
    }

    @Override
    public int compareTo(@NotNull GenericStat o) {
        return Integer.compare(this.numVar, o.numVar);
    }
}
