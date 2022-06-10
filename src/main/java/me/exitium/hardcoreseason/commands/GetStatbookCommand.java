package me.exitium.hardcoreseason.commands;

import me.exitium.hardcoreseason.DefaultFontInfo;
import me.exitium.hardcoreseason.HardcoreSeason;
import me.exitium.hardcoreseason.Utils;
import me.exitium.hardcoreseason.player.HCPlayer;
import net.kyori.adventure.text.Component;
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

public class GetStatbookCommand implements CommandExecutor {
    private final HardcoreSeason plugin;

    public GetStatbookCommand(HardcoreSeason plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command cannot be run from console.");
            return false;
        } else {
            if (!plugin.getHcWorldManager().isHardcoreWorld(player.getWorld().getName())) return false;
            HCPlayer hcPlayer = plugin.getOnlinePlayer(player.getUniqueId());

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
            case VICTORY -> "Victory!";
        };

        String basicInfo = player.getName() + "\n\n" + pStatus + "\n\n" + Utils.convertTime(hcPlayer.getTime());

        bookMeta.addPages(Component.text(basicInfo),
                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getDamageDealtList()),
                        new StringBuilder("=== Weapons Used ===\n"))),
                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getMobKillList()),
                        new StringBuilder("=== Monster Kills ===\n"))),
                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getConsumeFoodList()),
                        new StringBuilder("=== Food Eaten ===\n"))),
                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getDrinkPotionList()),
                        new StringBuilder("=== Potions Used ===\n")))
//                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getDamageDealtList()),
//                        new StringBuilder("=== Trades Made ===\n"))),
//                Component.text(getPageString(sortByValue(hcPlayer.getStatistics().getDamageDealtList()),
//                        new StringBuilder("=== Eyes Used ===\n")))
        );
        return bookMeta;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private String getPageString(Map<String, Integer> inputMap, StringBuilder fmtString) {
        if (inputMap.isEmpty()) return fmtString.toString();
        inputMap.entrySet().forEach(entry -> fmtString.append(DefaultFontInfo.alignString(entry)));
        return fmtString.toString();
    }
}
