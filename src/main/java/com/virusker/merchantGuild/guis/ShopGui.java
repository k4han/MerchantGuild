package com.virusker.merchantGuild.guis;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.language.LangManager;
import com.virusker.merchantGuild.language.Message;
import com.virusker.merchantGuild.models.ItemDetail;
import com.virusker.merchantGuild.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopGui implements InventoryHolder  {
    private final Inventory inventory;
    private ConfigManager config;
    private LangManager langManager;
    public ShopGui(ConfigManager config) {
        this.config = config;
        int size = 5;
        Component title = Component.text("Merchant Guild");
        this.inventory = config.getPlugin().getServer().createInventory(this, size * 9, title);
        HashMap<Integer, ItemDetail> invItems = config.getShopItems();
        this.langManager = config.getLangManager();

        setupButton();
        setupItems();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    private void setupButton() {
        inventory.setItem(36, Utils.createBtn(Material.RED_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_EXIT)));
    }

    public void setupItems() {
        for (var entry : config.getShopItems().entrySet()) {
            ItemDetail itemDetail = entry.getValue();

            // set lore for item
            var meta = itemDetail.getItem().getItemMeta();
            if (meta != null) {
                Component space = Component.text(" ");
                Component price = Component.text(langManager.get(Message.SELL_PRICE, itemDetail.getPrice()));
                Component amount = Component.text(langManager.get(Message.SELL_AMOUNT, itemDetail.getAmount()));
                List<Component> lore = new ArrayList<>();
                lore.add(space);
                lore.add(price);
                lore.add(amount);

                meta.lore(lore);
                itemDetail.getItem().setItemMeta(meta);
            }
            inventory.setItem(entry.getKey(), itemDetail.getItem());
        }
    }

    public HashMap<Integer, ItemDetail> getInvItems() {
        return config.getShopItems();
    }
}
