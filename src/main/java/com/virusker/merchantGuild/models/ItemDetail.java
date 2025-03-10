package com.virusker.merchantGuild.models;

import org.bukkit.inventory.ItemStack;

public class ItemDetail {
    private ItemStack item;
    private double price;
    private int amount;
    private String amountString;
    public ItemDetail(ItemStack item, double price, int amount, String amountString) {
        this.item = item;
        this.price = price;
        this.amount = amount;
        this.amountString = amountString;
        this.item.setAmount(amount);
    }

    public ItemDetail(ItemDetail other, int newAmount) {
        this.item = other.item.clone();
        this.price = other.price;
        this.amount = newAmount;
        this.item.setAmount(newAmount);
        this.amountString = other.amountString;
    }
    public ItemStack getItem() {
        return item;
    }
    public String getAmountString() {
        return amountString;
    }
    public double getPrice() {
        return price;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
        this.item.setAmount(amount);
    }

}
