package me.exitium.hardcoreseason.statistics;

public class ItemCrafted implements Comparable<ItemCrafted> {

    private final String itemType;
    private final int numCrafted;

    public ItemCrafted(String itemType, int numCrafted) {
        this.itemType = itemType;
        this.numCrafted = numCrafted;
    }

    public String getItemType() {
        return itemType;
    }

    public int getNumCrafted() {
        return numCrafted;
    }

    @Override
    public int compareTo(ItemCrafted ic) {
        return Integer.compare(this.numCrafted, ic.numCrafted);
    }
}
