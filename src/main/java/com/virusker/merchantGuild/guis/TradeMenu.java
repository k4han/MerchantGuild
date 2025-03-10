package com.virusker.merchantGuild.guis;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.language.LangManager;
import com.virusker.merchantGuild.language.Message;
import com.virusker.merchantGuild.models.ItemDetail;
import com.virusker.merchantGuild.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TradeMenu implements InventoryHolder {
    private final Inventory inventory;
    private ItemDetail itemdetail;
    private int playerSellAmount = 1;
    private LangManager langManager;

    public TradeMenu(ConfigManager config, int ClickedSlot) {
        this.langManager = config.getLangManager();
        Component title = Component.text("Merchant Guild");
        this.inventory = Bukkit.createInventory(this, 45, title);

        this.itemdetail = config.getItem(ClickedSlot);

        inventory.setItem(4, itemdetail.getItem());
        inventory.setItem(
                13,
                Utils.createBtn(
                        Material.GRAY_STAINED_GLASS_PANE,
                        langManager.get( Message.SELL_AMOUNT, this.playerSellAmount),
                        this.playerSellAmount));
        setupButton();
    }
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
    public void addItem(int amount) {
        if(amount < 1){
            return;
        }
        // max amount <= max shop amount
        if (this.playerSellAmount + amount > itemdetail.getAmount())
            amount = itemdetail.getAmount() - this.playerSellAmount;
        this.playerSellAmount += amount;
        inventory.setItem(13, Utils.createBtn(Material.GRAY_STAINED_GLASS_PANE, langManager.get(Message.SELL_AMOUNT,this.playerSellAmount), this.playerSellAmount));
    }
    public void subItem(int amount){
        if(this.playerSellAmount == 1){
            return;
        }
        if (this.playerSellAmount - amount < 1)
            amount = this.playerSellAmount - 1;
        this.playerSellAmount -= amount;
        inventory.setItem(13, Utils.createBtn(Material.GRAY_STAINED_GLASS_PANE, langManager.get(Message.SELL_AMOUNT,this.playerSellAmount), this.playerSellAmount));
    }
    public ItemDetail acceptTrade(){
        if(itemdetail.getAmount() < playerSellAmount){

            return null;
        }
        return itemdetail;
    }
    public int getPlayerSellAmount(){
        return playerSellAmount;
    }

    public void setupButton() {
        // Accept button
        inventory.setItem(30, Utils.createBtn(Material.GREEN_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_CONFIRM)));
        // Decline button
        inventory.setItem(32, Utils.createBtn(Material.RED_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_CANCEL)));
        // Add buttons
        inventory.setItem(15, Utils.createBtn(Material.GREEN_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_ADD), 1));
        inventory.setItem(16, Utils.createBtn(Material.GREEN_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_ADD), 10));
        inventory.setItem(17, Utils.createBtn(Material.GREEN_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_ADD), 64));
        // Minus buttons
        inventory.setItem(11, Utils.createBtn(Material.RED_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_MINUS), 1));
        inventory.setItem(10, Utils.createBtn(Material.RED_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_MINUS), 10));
        inventory.setItem(9, Utils.createBtn(Material.RED_STAINED_GLASS_PANE, langManager.get(Message.BUTTON_MINUS), 64));
    }
}
