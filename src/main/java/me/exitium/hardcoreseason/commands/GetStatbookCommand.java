package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.DefaultFontInfo;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import me.exitium.hardcoreseason.statistics.StatisticsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record GetStatbookCommand(HardcoreSeason plugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console.");
            return false;
        } else {
            if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return false;
            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());

            if (!hcPlayer.getStatus().equals(HCPlayer.STATUS.DEAD))
                hcPlayer.updateTime();

            ItemStack bookItem = CheckForStatBook(player.getInventory());
            ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = GenerateBookMeta(player, writtenBook, hcPlayer);
            if (bookItem != null) {
                bookItem.setItemMeta(bookMeta);
                player.updateInventory();
                player.sendMessage("You already have a stat book, Updating.");
            } else {
                writtenBook.setItemMeta(bookMeta);
                player.sendMessage("A stat book has been added to your inventory.");
                player.getInventory().addItem(writtenBook);
            }
        }
        return true;
    }

    private ItemStack CheckForStatBook(Inventory inventory) {
        ItemStack item = null;
        for (ItemStack i : inventory) {
            if (i == null) break;
            if (i.getType() == Material.WRITTEN_BOOK) {
                BookMeta bookMeta = (BookMeta) i.getItemMeta();
                if (bookMeta == null) break;
                if (Objects.equals(bookMeta.getTitle(), "HC Stats")) {
                    item = i;
                }
            }
        }
        return item;
    }

    private BookMeta GenerateBookMeta(Player player, ItemStack writtenBook, HCPlayer hcPlayer) {
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("HC Stats");
        bookMeta.setAuthor("Unknown");

        String pStatus = switch (hcPlayer.getStatus()) {
            case ALIVE -> "Alive";
            case DEAD -> "Dead";
            case NETHER -> "Nether";
            case END -> "End";
            case VICTORY -> "Victory!";
        };

        TextComponent timeString = Utils.convertTime(hcPlayer.getTime());
        String basicInfo = "Player: " + player.getName() +
                "\n\nProgression: " + pStatus +
                "\n\nTime Played: " + timeString.content();

        Map<StatisticsHandler.STATTYPE, Map<Integer, String>> pageMap = new HashMap<>();
        List<String> pageList = new ArrayList<>();

        populatePageList(pageList, hcPlayer);
        bookMeta.addPages(Component.text(basicInfo));

        for (String entry : pageList) {
            bookMeta.addPages(Component.text(entry));
        }
        return bookMeta;
    }

    private void populatePageList(List<String> pageList, HCPlayer hcPlayer) {
        Map<String, Integer> weaponList = sortByValue(hcPlayer.getStatistics().getDamageDealtList());
        Map<String, Integer> damageList = sortByValue(hcPlayer.getStatistics().getDamageTakenList());
        Map<String, Integer> monsterList = sortByValue(hcPlayer.getStatistics().getMobKillList());
        Map<String, Integer> foodList = sortByValue(hcPlayer.getStatistics().getConsumeFoodList());
        Map<String, Integer> potionList = sortByValue(hcPlayer.getStatistics().getDrinkPotionList());
        Map<String, Integer> craftedList = sortByValue(hcPlayer.getStatistics().getItemCraftedList());
        Map<String, Integer> eyeList = sortByValue(hcPlayer.getStatistics().getEyesUsedList());
        Map<String, Integer> tradeList = sortByValue(hcPlayer.getStatistics().getTradesList());

        processPages(pageList, weaponList, StatisticsHandler.STATTYPE.DAMAGE_DEALT);
        processPages(pageList, damageList, StatisticsHandler.STATTYPE.DAMAGE_TAKEN);
        processPages(pageList, monsterList, StatisticsHandler.STATTYPE.MOB_KILL);
        processPages(pageList, foodList, StatisticsHandler.STATTYPE.CONSUME_FOOD);
        processPages(pageList, potionList, StatisticsHandler.STATTYPE.DRINK_POTION);
        processPages(pageList, craftedList, StatisticsHandler.STATTYPE.ITEM_CRAFTED);
        processPages(pageList, eyeList, StatisticsHandler.STATTYPE.EYE_USED);
        processPages(pageList, tradeList, StatisticsHandler.STATTYPE.TRADES_MADE);
    }

    private void processPages(List<String> pageList, Map<String, Integer> statList, StatisticsHandler.STATTYPE stat) {
        String title = "";
        switch (stat) {
            case DAMAGE_DEALT -> title = "== Weapons Used ==";
            case DAMAGE_TAKEN -> title = "== Damage Taken ==";
            case MOB_KILL -> title = "== Mob Kills ==";
            case CONSUME_FOOD -> title = "== Food Eaten ==";
            case DRINK_POTION -> title = "== Potions Used ==";
            case ITEM_CRAFTED -> title = "== Items Crafted ==";
            case EYE_USED -> title = "== Eyes Used ==";
            case TRADES_MADE -> title = "== Trades Made ==";
        }

        StringBuilder sb = new StringBuilder();
        Map<String, Integer> tempMap = new HashMap<>();
        int pageIndex = 0;
        int entryIndex = 1;

        for (Map.Entry<String, Integer> entry : statList.entrySet()) {
            tempMap.put(entry.getKey(), entry.getValue());
            if (entryIndex == (statList.entrySet().size()) || (entryIndex % 13) == 0) {
                String pageString = getPageString(sortByValue(tempMap),
                        sb.insert(0, (pageIndex + 1))
                                .append(title)
                                .append(pageIndex + 1)
                                .append("\n"));
                pageList.add(pageString);
                pageIndex++;
                tempMap.clear();
                sb = new StringBuilder();
            }
            entryIndex++;
        }
    }


    public static Map<String, Integer> sortByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

//    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
//        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//        list.sort(Map.Entry.comparingByValue());
//        Map<K, V> result = new LinkedHashMap<>();
//        for (Map.Entry<K, V> entry : list) {
//            result.put(entry.getKey(), entry.getValue());
//        }
//
//        return result;
//    }

    private String getPageString(Map<String, Integer> inputMap, StringBuilder fmtString) {
        if (inputMap.isEmpty()) return fmtString.toString();
        inputMap.entrySet().forEach(entry -> fmtString.append(DefaultFontInfo.alignString(entry)));
        return fmtString.toString();
    }
}
