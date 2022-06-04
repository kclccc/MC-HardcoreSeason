package me.exitium.hardcoreseason.statistics;

public class DrinkPotion implements Comparable<DrinkPotion> {

    private final String potionName;
    private final int numDrank;

    public DrinkPotion(String potionName, int numDrank) {
        this.potionName = potionName;
        this.numDrank = numDrank;
    }

    public String getPotionName() {
        return potionName;
    }

    public int getNumDrank() {
        return numDrank;
    }

    @Override
    public int compareTo(DrinkPotion dp) {
        return Integer.compare(this.numDrank, dp.numDrank);
    }
}
