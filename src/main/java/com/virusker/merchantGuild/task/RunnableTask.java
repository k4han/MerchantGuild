package com.virusker.merchantGuild.task;

import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.language.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class RunnableTask implements Runnable {
    private ConfigManager configManager;
    public RunnableTask(ConfigManager configManager) {
        this.configManager = configManager;
    }
    @Override
    public void run() {
        configManager.reloadItemShop();
        // boardcast message reload
        Component text = Component.text(this.configManager.getLangManager().get(Message.MERCHANT_RELOAD));
        Bukkit.getServer().broadcast(text);

    }
}
