package me.exitium.hardcoreseason.statistics;

import com.google.gson.GsonBuilder;

import java.util.Map;

public class StatisticsHandler {

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

    private Map<String, Integer> mobKillList;
    private Map<String, Integer> drinkPotionList;
    private Map<String, Integer> consumeFoodList;
    private Map<String, Integer> damageTakenList;
    private Map<String, Integer> damageDealtList;
    private Map<String, Integer> itemCraftedList;
    private Map<String, Integer> eyesUsedList;
    private Map<String, Integer> tradesList;

    public void addStat(GenericStat stat, STATTYPE type) {
        switch (type) {
            case MOB_KILL -> mobKillList.merge(stat.getName(), 1, Integer::sum);
            case DRINK_POTION -> drinkPotionList.merge(stat.getName(), 1, Integer::sum);
            case CONSUME_FOOD -> consumeFoodList.merge(stat.getName(), 1, Integer::sum);
            case DAMAGE_TAKEN -> damageTakenList.merge(stat.getName(), 1, Integer::sum);
            case DAMAGE_DEALT -> damageDealtList.merge(stat.getName(), 1, Integer::sum);
            case ITEM_CRAFTED -> itemCraftedList.merge(stat.getName(), 1, Integer::sum);
            case EYE_USED -> eyesUsedList.merge(stat.getName(), 1, Integer::sum);
            case TRADES_MADE -> tradesList.merge(stat.getName(), 1, Integer::sum);
        }
    }

    public int isFirstDragonKill() {
        return mobKillList.get("Ender Dragon");
    }

    public String jsonMobKillList() {
        return new GsonBuilder().create().toJson(mobKillList);
    }

    public String jsonPotionList() {
        return new GsonBuilder().create().toJson(drinkPotionList);
    }

    public String jsonFoodList() {
        return new GsonBuilder().create().toJson(consumeFoodList);
    }

    public String jsonDamageTakenList() {
        return new GsonBuilder().create().toJson(damageTakenList);
    }

    public String jsonDamageDealtList() {
        return new GsonBuilder().create().toJson(damageDealtList);
    }

    public String jsonItemCraftedList() {
        return new GsonBuilder().create().toJson(itemCraftedList);
    }

    public String jsonEyesUsedList() {
        return new GsonBuilder().create().toJson(eyesUsedList);
    }

    public String jsonTradesList() {
        return new GsonBuilder().create().toJson(tradesList);
    }
}
