package com.virusker.merchantGuild.config;

import com.virusker.merchantGuild.MerchantGuild;
import com.virusker.merchantGuild.language.LangManager;
import com.virusker.merchantGuild.models.ItemDetail;
import com.virusker.merchantGuild.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ConfigManager {
    private static MerchantGuild plugin;
    private LangManager langManager;
    private HashMap<Integer, ItemDetail> ShopItems = new HashMap<>();
    private List<ItemDetail> allShopItems = new ArrayList<>();
    private ConfigurationSection configItems;
    private static Random random = new Random();
    private int maxItems;
    private int numRowItem;
    private boolean duplicates;
    private long refreshTime;
    private List<String> allowWorlds;
    private final int startSlot = 10;
    private static final List<Integer> ignoreSlots = Arrays.asList(9, 17, 18, 26);

    public ConfigManager(MerchantGuild plugin) {
        ConfigManager.plugin = plugin;
        loadConfig();
    }
    public void loadConfig() {
        this.maxItems = Math.min(plugin.getConfig().getInt("shop.max_items"), 14);
        this.duplicates = plugin.getConfig().getBoolean("shop.duplicates");
        this.configItems = plugin.getConfig().getConfigurationSection("items");
        this.allowWorlds = plugin.getConfig().getStringList("shop.allow_worlds");
        this.numRowItem = (int) Math.ceil((double) maxItems / 7);
        this.refreshTime = plugin.getConfig().getLong("shop.refresh_time");
        this.allShopItems = loadItemInConfig();
        this.ShopItems = getRandItems();
        this.langManager = new LangManager(plugin);
    }
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
        plugin.startTask(this);
    }
    public LangManager getLangManager() {
        return langManager;
    }

    public void reloadItemShop() {
        this.ShopItems = getRandItems();
    }
    public MerchantGuild getPlugin() {
        return plugin;
    }
    public List<String> getAllowWorlds() {
        return allowWorlds;
    }

    public List<ItemDetail> getAllShopItems() { return allShopItems; }

    public HashMap<Integer, ItemDetail> getRandItems() {
        HashMap<Integer, ItemDetail> items = new HashMap<>();
        List<ItemDetail> itemsList = getAllShopItems();
        int start = startSlot;
        List<Integer> randed = new ArrayList<>();
        int maxItemsinShop = this.maxItems;
        int listSize = itemsList.size();
        if (listSize < maxItemsinShop && !duplicates) {
            maxItemsinShop = listSize;
        }
        for (int i = 0; i < maxItemsinShop; i++) {
            if (ignoreSlots.contains(start)) {
                start++;
                i--;
                continue;
            }
            int randIndex = random.nextInt(listSize);
            while (!duplicates && randed.contains(randIndex)) {
                randIndex = random.nextInt(listSize);
            }
            randed.add(randIndex);

            ItemDetail originalItem = itemsList.get(randIndex);
            int newAmount = parseAmount(originalItem.getAmountString());

            ItemDetail clonedItem = new ItemDetail(originalItem, newAmount);
            items.put(start++, clonedItem);
        }

        return items;
    }
    public List<ItemDetail> loadItemInConfig() {
        List<ItemDetail> items = new ArrayList<>();

        if (configItems != null) {

            for (String key : configItems.getKeys(false)) {
                var itemConfig = configItems.getConfigurationSection(key);
                if (itemConfig == null) continue;
                String amountStr = itemConfig.getString("amount", "1");
                int amount = parseAmount(amountStr);
                if (key.startsWith("ALL_")) {
                    // get prefix, for example: "concrete" from "all_concrete"
                    String prefix = key.substring(3).toUpperCase();

                    for (Material material : Material.values()) {
                        if (material.isLegacy() || !material.isItem()) continue;

                        if (material.name().endsWith(prefix)) { // filter by prefix
                            addItemToList(items, material, amount, amountStr, itemConfig);
                        }
                    }
                } else {
                    Material material = Material.matchMaterial(key);
                    if (material == null) {
                        plugin.getLogger().severe("Invalid material: " + key);
                        continue;
                    }
                    addItemToList(items, material, amount, amountStr, itemConfig);
                }
            }
        }
        // total items in shop
        plugin.getLogger().info( "Loaded " + items.size() + " items in shop");
        return items;
    }
    private void addItemToList(List<ItemDetail> items, Material material, int amount, String amountStr, ConfigurationSection itemConfig) {
        try {
            ItemStack itemStack = new ItemStack(material, amount);
            var meta = itemStack.getItemMeta();
            if (meta != null) {
                // Áp dụng enchantments nếu có
                ConfigurationSection enchants = itemConfig.getConfigurationSection("enchants");
                if (enchants != null) {
                    for (String enchantKey : enchants.getKeys(false)) {
                        Enchantment enchantment = Utils.getEnchantment(enchantKey);
                        if (enchantment != null) {
                            meta.addEnchant(enchantment, enchants.getInt(enchantKey), true);
                        }
                    }
                }
                itemStack.setItemMeta(meta);
            }
            items.add( new ItemDetail(itemStack, (float) itemConfig.getDouble("price"), amount, amountStr));
//            items.put(slot, new Itemdetail(itemStack, (float) itemConfig.getDouble("price"), amount, amountStr));
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to parse item: " + material.name());
            e.printStackTrace();
        }
    }
    public static int parseAmount(String amountStr) {
        try {
            if (!amountStr.contains("-")) {
                return Integer.parseInt(amountStr);
            }
            String[] parts = amountStr.split("-");
            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());

            if (min > max) {
                throw new IllegalArgumentException("Invalid amount range: " + amountStr);
            }
            // random [min, max]
            return min + random.nextInt(max - min + 1);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountStr);
        }
    }
    public HashMap<Integer, ItemDetail> getShopItems() {
        return ShopItems;
    }
    public ItemDetail getItem(int slot) {
        return ShopItems.get(slot);
    }
    public long getRefreshTime() {
        return refreshTime;
    }
}



