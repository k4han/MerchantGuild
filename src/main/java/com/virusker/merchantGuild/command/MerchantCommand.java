package com.virusker.merchantGuild.command;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.guis.ShopGui;
import com.virusker.merchantGuild.language.LangManager;
import com.virusker.merchantGuild.language.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MerchantCommand  implements TabExecutor {

    private final ConfigManager config;
    private final LangManager lang;
    public MerchantCommand(ConfigManager config) {
        this.config = config;
        this.lang = config.getLangManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(lang.get(Message.CMD_CONSOLE_BLOCKED));
                return false;
            }

            if (!player.hasPermission("merchant.use")) {
                player.sendMessage(lang.get(Message.CMD_NO_PERMISSION));
                return true;
            }

            if (!config.getAllowWorlds().contains(player.getWorld().getName())) {
                player.sendMessage(lang.get(Message.CMD_WORLD_BLOCKED));
                return true;
            }

            ShopGui shopGui = new ShopGui(config);
            player.openInventory(shopGui.getInventory());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(lang.get(Message.CMD_HELP));
                return true;
            }

            if (args[0].equalsIgnoreCase("refresh") || args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("merchant.admin")) {
                    sender.sendMessage(lang.get(Message.CMD_NO_PERMISSION));
                    return true;
                }

                if (args[0].equalsIgnoreCase("refresh")) {
                    config.reloadItemShop();
                    sender.sendMessage(lang.get(Message.CMD_REFRESH_OK));
                } else {
                    config.reloadConfig();
                    sender.sendMessage(lang.get(Message.CMD_RELOAD_OK));
                }
                return true;
            }

            sender.sendMessage(lang.get(Message.CMD_UNKNOWN));
            return true;
        }

        sender.sendMessage(lang.get(Message.CMD_USAGE));
        return true;
    }
    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        if (args.length == 1) {
            return Arrays.asList("help", "refresh", "reload");
        }
        return null;
    }
}
