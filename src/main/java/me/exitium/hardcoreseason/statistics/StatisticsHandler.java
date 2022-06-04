package me.exitium.hardcoreseason.statistics;

import me.exitium.hardcoreseason.HardcoreSeason;

import java.util.Comparator;
import java.util.TreeSet;

public class StatisticsHandler {

    private final HardcoreSeason plugin;

    public StatisticsHandler(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    private TreeSet<MobKill> mobKillList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<DrinkPotion> drinkPotionList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<ConsumeFood> consumeFoodList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<DamageTaken> damageTakenList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<DamageDealt> damageDealtList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<ItemCrafted> itemCraftedList = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<EyeUsed> eyesUsedList = new TreeSet<>(Comparator.reverseOrder());

    private void addMobKill(MobKill mobKill) {
        mobKillList.add(mobKill);
    }

    private void addPotionDrank(DrinkPotion drinkPotion) {
        drinkPotionList.add(drinkPotion);
    }

    private void addFoodEaten(ConsumeFood consumeFood) {
        consumeFoodList.add(consumeFood);
    }

    private void addDamageTaken(DamageTaken damageTaken) {
        damageTakenList.add(damageTaken);
    }

    private void addDamageDealt(DamageDealt damageDealt) {
        damageDealtList.add(damageDealt);
    }

    private void addItemCrafted(ItemCrafted itemCrafted) {
        itemCraftedList.add(itemCrafted);
    }

    private void addEyeUsed(EyeUsed eyeUsed) {
        eyesUsedList.add(eyeUsed);
    }
}
