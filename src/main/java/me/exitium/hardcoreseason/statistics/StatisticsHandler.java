package me.exitium.hardcoreseason.statistics;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class StatisticsHandler {

    private Map<String, Integer> mobKillList;
    private Map<String, Integer> drinkPotionList;
    private Map<String, Integer> consumeFoodList;
    private Map<String, Integer> damageTakenList;
    private Map<String, Integer> damageDealtList;
    private Map<String, Integer> itemCraftedList;
    private Map<String, Integer> eyesUsedList;
    private Map<String, Integer> tradesList;

    public StatisticsHandler(
            Map<String, Integer> mobKillList,
            Map<String, Integer> drinkPotionList,
            Map<String, Integer> consumeFoodList,
            Map<String, Integer> damageTakenList,
            Map<String, Integer> damageDealtList,
            Map<String, Integer> itemCraftedList,
            Map<String, Integer> eyesUsedList,
            Map<String, Integer> tradesList) {
        this.mobKillList = mobKillList;
        this.drinkPotionList = drinkPotionList;
        this.consumeFoodList = consumeFoodList;
        this.damageTakenList = damageTakenList;
        this.damageDealtList = damageDealtList;
        this.itemCraftedList = itemCraftedList;
        this.eyesUsedList = eyesUsedList;
        this.tradesList = tradesList;
    }

    public StatisticsHandler() {
        this.mobKillList = new HashMap<>();
        this.drinkPotionList = new HashMap<>();
        this.consumeFoodList = new HashMap<>();
        this.damageTakenList = new HashMap<>();
        this.damageDealtList = new HashMap<>();
        this.itemCraftedList = new HashMap<>();
        this.eyesUsedList = new HashMap<>();
        this.tradesList = new HashMap<>();
    }

    public void addStat(GenericStat stat, STATTYPE type) {
        switch (type) {
            case MOB_KILL -> mobKillList.merge(stat.getName(), 1, Integer::sum);
            case DRINK_POTION -> drinkPotionList.merge(stat.getName(), 1, Integer::sum);
            case CONSUME_FOOD -> consumeFoodList.merge(stat.getName(), 1, Integer::sum);
            case DAMAGE_TAKEN -> damageTakenList.merge(stat.getName(), stat.getNumVar(), Integer::sum);
            case DAMAGE_DEALT -> damageDealtList.merge(stat.getName(), stat.getNumVar(), Integer::sum);
            case ITEM_CRAFTED -> itemCraftedList.merge(stat.getName(), stat.getNumVar(), Integer::sum);
            case EYE_USED -> eyesUsedList.merge(stat.getName(), 1, Integer::sum);
            case TRADES_MADE -> tradesList.merge(stat.getName(), stat.getNumVar(), Integer::sum);
        }
    }

    public int isFirstDragonKill() {
        return mobKillList.get("Ender Dragon");
    }

    public String toJson(Map<String, Integer> map) {
        return new GsonBuilder().create().toJson(map);
    }

    public Map<String, Integer> getMobKillList() {
        if (mobKillList == null) return new HashMap<>();
        return mobKillList;
    }

    public Map<String, Integer> getDrinkPotionList() {
        if (drinkPotionList == null) return new HashMap<>();
        return drinkPotionList;
    }

    public Map<String, Integer> getConsumeFoodList() {
        if (consumeFoodList == null) return new HashMap<>();
        return consumeFoodList;
    }

    public Map<String, Integer> getDamageTakenList() {
        if (damageTakenList == null) return new HashMap<>();
        return damageTakenList;
    }

    public Map<String, Integer> getDamageDealtList() {
        if (damageDealtList == null) return new HashMap<>();
        return damageDealtList;
    }

    public Map<String, Integer> getItemCraftedList() {
        if (itemCraftedList == null) return new HashMap<>();
        return itemCraftedList;
    }

    public Map<String, Integer> getEyesUsedList() {
        if (eyesUsedList == null) return new HashMap<>();
        return eyesUsedList;
    }

    public Map<String, Integer> getTradesList() {
        if (tradesList == null) return new HashMap<>();
        return tradesList;
    }

    public enum STATTYPE {
        MOB_KILL,
        DRINK_POTION,
        CONSUME_FOOD,
        ITEM_CRAFTED,
        DAMAGE_TAKEN,
        DAMAGE_DEALT,
        EYE_USED,
        TRADES_MADE
    }
}
