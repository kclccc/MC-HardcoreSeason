package me.exitium.hardcoreseason.statistics;

public class DamageTaken implements Comparable<DamageTaken> {
    private final String damageType;
    private final int damageValue;

    public DamageTaken(String damageType, int damageValue) {
        this.damageType = damageType;
        this.damageValue = damageValue;
    }

    public String getDamageType() {
        return damageType;
    }

    public int getDamageValue() {
        return damageValue;
    }

    @Override
    public int compareTo(DamageTaken dt) {
        return Integer.compare(this.damageValue, dt.damageValue);
    }
}
