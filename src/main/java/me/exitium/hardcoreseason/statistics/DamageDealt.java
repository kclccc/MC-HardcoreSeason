package me.exitium.hardcoreseason.statistics;

public class DamageDealt implements Comparable<DamageDealt> {
    private final String weaponType;
    private final int damageValue;

    public DamageDealt(String weaponType, int damageValue) {
        this.weaponType = weaponType;
        this.damageValue = damageValue;
    }

    public String getDamageType() {
        return weaponType;
    }

    public int getDamageValue() {
        return damageValue;
    }

    @Override
    public int compareTo(DamageDealt dd) {
        return Integer.compare(this.damageValue, dd.damageValue);
    }
}
