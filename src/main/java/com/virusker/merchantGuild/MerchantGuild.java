package com.virusker.merchantGuild;

import com.virusker.merchantGuild.Listener.ShopEvent;
import com.virusker.merchantGuild.command.MerchantCommand;
import com.virusker.merchantGuild.config.ConfigManager;
import com.virusker.merchantGuild.metrics.Metrics;
import com.virusker.merchantGuild.task.RunnableTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class MerchantGuild extends JavaPlugin {
    private static Economy econ = null;
    private BukkitTask bukkittask;

    @Override
    public void onEnable() {
        int pluginId = 25056;

        saveDefaultConfig();
        ConfigManager configManager = new ConfigManager(this);

        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("merchant").setExecutor(new MerchantCommand(configManager));

        getServer().getPluginManager().registerEvents(new ShopEvent(configManager), this);
        getServer().getPluginManager().registerEvents(new com.virusker.merchantGuild.Listener.TradeEvent(configManager, econ), this);

        startTask(configManager);
        getLogger().info("MerchantGuild is enabled!");

        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        stopTask();
        getLogger().info("MerchantGuild is disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public void stopTask() {
        if (bukkittask != null && !bukkittask.isCancelled()) {
            bukkittask.cancel();
        }
    }
    public void startTask(ConfigManager configManager) {
        stopTask();
        long nextRefreshTime = configManager.getRefreshTime() * 20 * 60;
        bukkittask = getServer().getScheduler().runTaskTimerAsynchronously(this,
                new RunnableTask(configManager), nextRefreshTime, nextRefreshTime);
    }
}