package com.virusker.merchantGuild.Listener;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.guis.ShopGui;
import com.virusker.merchantGuild.guis.TradeMenu;
import com.virusker.merchantGuild.language.LangManager;
import com.virusker.merchantGuild.language.Message;
import com.virusker.merchantGuild.models.ItemDetail;
import com.virusker.merchantGuild.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradeEvent implements Listener {
    private ConfigManager config;
    private Economy economy;

    public  TradeEvent(ConfigManager config, Economy economy) {
        this.config = config;
        this.economy = economy;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof TradeMenu tradeMenu)) return;
        LangManager lang = config.getLangManager();
        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getRawSlot();
        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }
//        30 accept, 32 decline, 15-17 add(1,10,64) 11-9 minus(64,10,1)
        if (rawSlot == 30) {
            ItemDetail items = tradeMenu.acceptTrade();
            int playerSellAmount = tradeMenu.getPlayerSellAmount();
            if (items == null) {
                player.sendMessage(lang.get(Message.SHOP_CANT_BUY));
                event.setCancelled(true);
                return;
            }

            int playerInventoryAmountItems = Utils.getTotalAmount(player, items.getItem());

            if (playerInventoryAmountItems == 0) {
                player.sendMessage(lang.get(Message.NO_ITEM));
                event.setCancelled(true);
                return;
            }
            if (playerSellAmount > items.getAmount()) {
                player.sendMessage(lang.get(Message.SHOP_CANT_BUY));
                event.setCancelled(true);
                return;
            }

            // Check if player has enough items
            if (playerInventoryAmountItems < playerSellAmount) {
                playerSellAmount = playerInventoryAmountItems;
            }

            // First store the original amount from the shop inventory
            int originalShopAmount = items.getAmount();

            // Calculate new shop amount after player sells items
            int newShopAmount = originalShopAmount - playerSellAmount;

            // Check if the shop has sufficient capacity
            if (newShopAmount < 0) {
                player.sendMessage("§cThe shop can't buy that many items!");
                event.setCancelled(true);
                return;
            }

            ItemStack itemToRemove = items.getItem().clone();
            itemToRemove.setAmount(playerSellAmount);
            // Update shop inventory
            items.setAmount(newShopAmount);

            // Remove the exact amount from player's inventory

//            playerInventory.removeItem(itemToRemove);
            boolean r = Utils.removeItem(player, itemToRemove);

            player.sendMessage(lang.get(Message.SELL_SUCCESS, playerSellAmount, items.getPrice() * playerSellAmount));

            // Deposit money to player
            economy.depositPlayer(player, items.getPrice() * playerSellAmount);

            // Close inventory and confirm trade
            player.closeInventory();


        } else if (rawSlot == 32) {
            ShopGui shopGui = new ShopGui(config);
            player.openInventory(shopGui.getInventory());

        } else if (rawSlot >= 15 && rawSlot <= 17) {
            int amountToAdd = switch (rawSlot) {
                case 15 -> 1;
                case 16 -> 10;
                case 17 -> 64;
                default -> 0;
            };
            //
            tradeMenu.addItem(amountToAdd);
        } else if (rawSlot >= 9 && rawSlot <= 11) {
            int amountToSubtract = switch (rawSlot) {
                case 11 -> 1;
                case 10 -> 10;
                case 9 -> 64;
                default -> 0;
            };
            tradeMenu.subItem(amountToSubtract);
        }
        event.setCancelled(true);
    }
}
