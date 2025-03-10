package com.virusker.merchantGuild.command;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.guis.ShopGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MerchantCommand  implements TabExecutor {

    private ConfigManager config;
    public MerchantCommand(ConfigManager config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console cannot use this command.");
                return false;
            }
//            Player player = (Player) sender;

            if (!player.hasPermission("merchant.use")) {
                player.sendMessage("You don't have permission to use this command!");
                return true;
            }

            // Kiểm tra nếu thế giới hiện tại không nằm trong danh sách
            if (!config.getAllowWorlds().contains(player.getWorld().getName())) {
                player.sendMessage("You cannot use this command in this world!");
                return true;
            }

            ShopGui shopGui = new ShopGui(config);
            player.openInventory(shopGui.getInventory());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("Not help!");
                return true;
            }

            if (args[0].equalsIgnoreCase("refresh") || args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("merchant.admin")) {
                    sender.sendMessage("You don't have permission to use this command!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("refresh")) {
                    config.reloadItemShop();
                    sender.sendMessage("Merchant Shop has been refreshed!");
                } else {
                    config.reloadConfig();
                    sender.sendMessage("Config has been reloaded!");
                }
                return true;
            }

            sender.sendMessage("Unknown command. Use /merchant help for commands.");
            return true;
        }

        sender.sendMessage("Invalid command usage. Use /merchant help for commands.");
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
