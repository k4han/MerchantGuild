package com.virusker.merchantGuild.Listener;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.guis.ShopGui;
import com.virusker.merchantGuild.guis.TradeMenu;
import com.virusker.merchantGuild.models.ItemDetail;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import java.util.HashMap;

public class ShopEvent implements Listener {
    private ConfigManager config;
    private HashMap<Integer, ItemDetail> invItems = new HashMap<>();

    public ShopEvent(ConfigManager config) {
        this.config = config;

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        // Check if the holder is our MyInventory,
        // if yes, use instanceof pattern matching to store it in a variable immediately.
        if (!(inventory.getHolder(false) instanceof ShopGui shopInv)) {
            return;
        }
        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        int clickedSlot = event.getRawSlot();

        if (clickedSlot == 36) {
            player.closeInventory();
            return;
        }

        invItems = shopInv.getInvItems();
        if (invItems.containsKey(clickedSlot)) {
            ItemDetail itemdetail = invItems.get(clickedSlot);
            if (itemdetail.getAmount() == 0) {
//                player.sendMessage("§cThis item is out of stock!");
                return;
            }
            TradeMenu tradeMenu = new TradeMenu(config, clickedSlot);
            player.openInventory(tradeMenu.getInventory());
        }
        event.setCancelled(true); // Ensure the inventory click action is cancelled as intended.
    }

}
