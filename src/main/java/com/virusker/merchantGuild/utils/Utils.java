package com.virusker.merchantGuild.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Utils {
    public static Enchantment getEnchantment(String enchantKey) {
        if (enchantKey == null) return null;

        NamespacedKey keyname = NamespacedKey.minecraft(enchantKey.toLowerCase());
        Enchantment enchantment = Registry.ENCHANTMENT.get(keyname);

        return enchantment;
    }

    public static int getTotalAmount(Player player, ItemStack targetItem) {
        int totalAmount = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && equalsEnchantment(item, targetItem)) {
                totalAmount += item.getAmount();
            }
        }
        return totalAmount;
    }

    public static boolean equalsEnchantment(ItemStack item1, ItemStack item2) {
        if (item1.getType() != item2.getType()) return false;

        Map<Enchantment, Integer> enchantments1 = item1.getEnchantments();
        Map<Enchantment, Integer> enchantments2 = item2.getEnchantments();

        return enchantments1.equals(enchantments2);
    }

    public static boolean removeItem(Player player, ItemStack itemToRemove) {
        int amount = itemToRemove.getAmount();
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && equalsEnchantment(item, itemToRemove)) {
                int removeAmount = Math.min(amount, item.getAmount());
                amount -= removeAmount;

                if (removeAmount >= item.getAmount()) {
                    inventory.setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - removeAmount);
                }

                if (amount <= 0) return true;
            }
        }
        return false;
    }

    public static ItemStack createBtn(Material material, String name) {
        return createBtn(material, name, 1);
    }

    public static ItemStack createBtn(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    // Credits: https://github.com/SpigotMC/BungeeCord/pull/2883#issuecomment-770429978
    public static String colorize(final String textToTranslate) {
        final char altColorChar = '&'; // & -> §, &chello -> RED, &#cc44ffHello -> colorize Hello
        final StringBuilder b = new StringBuilder();
        final char[] mess = textToTranslate.toCharArray();
        boolean color = false, hashtag = false, doubleTag = false;
        char tmp; // Used in loops

        for (int i = 0; i < mess.length; ) { // i increment is handled case by case for speed

            final char c = mess[i];

            if (doubleTag) { // DoubleTag module
                doubleTag = false;

                final int max = i + 3;

                if (max <= mess.length) {
                    // There might be a hex color here
                    boolean match = true;

                    for (int n = i; n < max; n++) {
                        tmp = mess[n];
                        // The order of the checks below is meant to improve performances (i.e. capital letters check is at the end)
                        if (!((tmp >= '0' && tmp <= '9') || (tmp >= 'a' && tmp <= 'f') || (tmp >= 'A' && tmp <= 'F'))) {
                            // It wasn't a hex color, appending found chars to the StringBuilder and continue the for loop
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        b.append(ChatColor.COLOR_CHAR);
                        b.append('x');

                        // Copy colors with a color code in between
                        for (; i < max; i++) {
                            tmp = mess[i];
                            b.append(ChatColor.COLOR_CHAR);
                            b.append(tmp);
                            // Double the color code
                            b.append(ChatColor.COLOR_CHAR);
                            b.append(tmp);
                        }

                        // i increment has been already done
                        continue;
                    }
                }

                b.append(altColorChar);
                b.append("##");
                // Malformed hex, let's carry on checking mess[i]
            }

            if (hashtag) { // Hashtagmodule
                hashtag = false;

                // Check for double hashtag (&##123 => &#112233)
                if (c == '#') {
                    doubleTag = true;
                    i++;
                    continue;
                }

                final int max = i + 6;

                if (max <= mess.length) {
                    // There might be a hex color here
                    boolean match = true;

                    for (int n = i; n < max; n++) {
                        tmp = mess[n];
                        // The order of the checks below is meant to improve performances (i.e. capital letters check is at the end)
                        if (!((tmp >= '0' && tmp <= '9') || (tmp >= 'a' && tmp <= 'f') || (tmp >= 'A' && tmp <= 'F'))) {
                            // It wasn't a hex color, appending found chars to the StringBuilder and continue the for loop
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        b.append(ChatColor.COLOR_CHAR);
                        b.append('x');

                        // Copy colors with a color code in between
                        for (; i < max; i++) {
                            b.append(ChatColor.COLOR_CHAR);
                            b.append(mess[i]);
                        }
                        // i increment has been already done
                        continue;
                    }
                }

                b.append(altColorChar);
                b.append('#');
                // Malformed hex, let's carry on checking mess[i]
            }

            if (color) { // Color module
                color = false;

                if (c == '#') {
                    hashtag = true;
                    i++;
                    continue;
                }

                // The order of the checks below is meant to improve performances (i.e. capital letters check is at the end)
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r' || (c >= 'k' && c <= 'o') || (c >= 'A' && c <= 'F') || c == 'R' || (c >= 'K' && c <= 'O')) {
                    b.append(ChatColor.COLOR_CHAR);
                    b.append(c);
                    i++;
                    continue;
                }

                b.append(altColorChar);
                // Not a valid color, let's carry on checking mess[i]
            }

            // Base case
            if (c == altColorChar) { // c == '&'
                color = true;
                i++;
                continue;
            }

            // None matched, append current character
            b.append(c);
            i++;

        }

        // Append '&' if '&' was the last character of the string
        if (color)
            b.append(altColorChar);
        else // color and hashtag cannot be true at the same time
            // Append "&#" if "&#" were the last characters of the string
            if (hashtag) {
                b.append(altColorChar);
                b.append('#');
            } else // color, hashtag, and doubleTag cannot be true at the same time
                // Append "&##" if "&##" were the last characters of the string
                if (doubleTag) {
                    b.append(altColorChar);
                    b.append("##");
                }

        return b.toString();
    }
}
