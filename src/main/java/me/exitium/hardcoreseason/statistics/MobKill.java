package me.exitium.hardcoreseason.statistics;

public class MobKill implements Comparable<MobKill> {

    private final String mobName;
    private final int numKills;

    public MobKill(String mobName, int numKills) {
        this.mobName = mobName;
        this.numKills = numKills;
    }

    public String getMobName() {
        return mobName;
    }

    public int getNumKills() {
        return numKills;
    }

    @Override
    public int compareTo(MobKill mk) {
        return Integer.compare(this.numKills, mk.numKills);
    }
}
