package me.exitium.hardcoreseason.statistics;

import java.util.Map;

public class StatisticsHandler {

    public StatisticsHandler(
            Map<String, Integer> mobKillList,
            Map<String, Integer> drinkPotionList,
            Map<String, Integer> consumeFoodList,
            Map<String, Integer> damageTakenList,
            Map<String, Integer> damageDealtList,
            Map<String, Integer> itemCraftedList,
            Map<String, Integer> eyesUsedList) {
        this.mobKillList = mobKillList;
        this.drinkPotionList = drinkPotionList;
        this.consumeFoodList = consumeFoodList;
        this.damageTakenList = damageTakenList;
        this.damageDealtList = damageDealtList;
        this.itemCraftedList = itemCraftedList;
        this.eyesUsedList = eyesUsedList;
    }

    enum STATTYPE {
        MOB_KILL,
        DRINK_POTION,
        CONSUME_FOOD,
        ITEM_CRAFTED,
        DAMAGE_TAKEN,
        DAMAGE_DEALT,
        EYE_USED
    }

    private Map<String, Integer> mobKillList;
    private Map<String, Integer> drinkPotionList;
    private Map<String, Integer> consumeFoodList;
    private Map<String, Integer> damageTakenList;
    private Map<String, Integer> damageDealtList;
    private Map<String, Integer> itemCraftedList;
    private Map<String, Integer> eyesUsedList;

    private void addStat(GenericStat stat, STATTYPE type) {
        switch (type) {
            case MOB_KILL -> mobKillList.merge(stat.name(), 1, Integer::sum);
            case DRINK_POTION -> drinkPotionList.merge(stat.name(), 1, Integer::sum);
            case CONSUME_FOOD -> consumeFoodList.merge(stat.name(), 1, Integer::sum);
            case DAMAGE_TAKEN -> damageTakenList.merge(stat.name(), 1, Integer::sum);
            case DAMAGE_DEALT -> damageDealtList.merge(stat.name(), 1, Integer::sum);
            case ITEM_CRAFTED -> itemCraftedList.merge(stat.name(), 1, Integer::sum);
            case EYE_USED -> eyesUsedList.merge(stat.name(), 1, Integer::sum);
        }
    }
}
