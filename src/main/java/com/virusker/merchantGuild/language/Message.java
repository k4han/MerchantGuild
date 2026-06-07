package com.virusker.merchantGuild.language;

public enum Message {

    SELL_SUCCESS("sell_success"),
    NO_ITEM("no_item"),
    SHOP_CANT_BUY("shop_cant_buy"),
    MERCHANT_RELOAD("merchant_reload"),
    SELL_ERROR("sell_error"),

    GUI_TITLE("gui_title"),

    BUTTON_EXIT("button_exit"),
    BUTTON_SELL("button_sell"),
    BUTTON_ADD("button_add"),
    BUTTON_MINUS("button_minus"),
    BUTTON_CONFIRM("button_confirm"),
    BUTTON_CANCEL("button_cancel"),
    SELL_PRICE("sell_price"),
    SELL_AMOUNT("sell_amount"),

    CMD_CONSOLE_BLOCKED("cmd_console_blocked"),
    CMD_NO_PERMISSION("cmd_no_permission"),
    CMD_WORLD_BLOCKED("cmd_world_blocked"),
    CMD_HELP("cmd_help"),
    CMD_REFRESH_OK("cmd_refresh_ok"),
    CMD_RELOAD_OK("cmd_reload_ok"),
    CMD_UNKNOWN("cmd_unknown"),
    CMD_USAGE("cmd_usage");

    private final String key;

    Message(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
