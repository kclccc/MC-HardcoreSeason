package me.exitium.hardcoreseason.statistics;

public class ConsumeFood implements Comparable<ConsumeFood> {

    private final String foodName;
    private final int numEaten;

    public ConsumeFood(String foodName, int numEaten) {
        this.foodName = foodName;
        this.numEaten = numEaten;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getNumEaten() {
        return numEaten;
    }

    @Override
    public int compareTo(ConsumeFood cf) {
        return Integer.compare(this.numEaten, cf.numEaten);
    }
}
