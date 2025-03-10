package com.virusker.merchantGuild.language;

public enum Message {

    SELL_SUCCESS("sell_success"),
    NO_ITEM("no_item"),
    // §cThe shop can't buy that many items!
    SHOP_CANT_BUY("shop_cant_buy"),
    // §aMerchant wants to buy some items at §e/merchant
    MERCHANT_RELOAD("merchant_reload"),
    SELL_ERROR("sell_error"),

    BUTTON_EXIT("button_exit"),
    BUTTON_SELL("button_sell"),
    BUTTON_ADD("button_add"),
    BUTTON_MINUS("button_minus"),
    BUTTON_CONFIRM("button_confirm"),
    BUTTON_CANCEL("button_cancel"),
    SELL_PRICE("sell_price"),
    SELL_AMOUNT("sell_amount");

    private final String key;

    Message(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
