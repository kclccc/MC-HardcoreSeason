package me.exitium.hardcoreseason.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import me.exitium.hardcoreseason.HardcoreSeason;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

public class StatisticsHandler {

    private final HardcoreSeason plugin;

    StatisticsHandler(
            HardcoreSeason plugin, TreeSet<MobKill> mobKillList,
            TreeSet<DrinkPotion> drinkPotionList,
            TreeSet<ConsumeFood> consumeFoodList,
            TreeSet<DamageTaken> damageTakenList,
            TreeSet<DamageDealt> damageDealtList,
            TreeSet<ItemCrafted> itemCraftedList,
            TreeSet<EyeUsed> eyesUsedList){
        this.plugin = plugin;
        this.mobKillList = mobKillList;
        this.drinkPotionList = drinkPotionList;
        this.consumeFoodList = consumeFoodList;
        this.damageTakenList = damageTakenList;
        this.damageDealtList = damageDealtList;
        this.itemCraftedList = itemCraftedList;
        this.eyesUsedList = eyesUsedList;
    }

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

    public TreeSet<?> jsonToTree(String json, Class<?> statClass) {
        TreeSet<?> tree = new TreeSet<>();
        GsonBuilder builder = new GsonBuilder();
        LinkedTreeMap<?,?> map = (LinkedTreeMap<?, ?>) builder.create().fromJson(json, Object.class);

        for(Map.Entry<?, ?> entry : map.entrySet()){
            String key = (String) entry.getKey();
            int value = (int) entry.getValue();

            Object objClass = statClass;
            tree.add();
        }
    }
}
