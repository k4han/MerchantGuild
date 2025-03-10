package com.virusker.merchantGuild.language;

import com.virusker.merchantGuild.MerchantGuild;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private final MerchantGuild plugin;
    private Map<String, String> messages = new HashMap<>();

    private final String[] availableLanguages = {"en.yml", "vi.yml"};

    public LangManager(MerchantGuild plugin) {
        this.plugin = plugin;
        saveDefaultLanguageFiles();
        this.messages = loadLanguage();
    }
    private void saveDefaultLanguageFiles() {
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        for (String langFileName : availableLanguages) {
            File langFile = new File(langFolder, langFileName);
            if (!langFile.exists()) {
                plugin.saveResource("languages/" + langFileName, false);
            }
        }
    }
    public Map<String, String> loadLanguage() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String currentLang = config.getString("language", "en");

        File langFile = new File(plugin.getDataFolder(), "languages/" + currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("languages/en.yml", false); // Copy mặc định nếu chưa có
        }
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

        for (String key : langConfig.getKeys(false)) { // Lấy key từ root
            messages.put(key, langConfig.getString(key, key)); // Lưu vào Map
        }
        return this.messages;
    }

    public String get(Message message, Object... args) {
        String msg = messages.getOrDefault(message.getKey(), message.getKey());
        return String.format(msg, args);
    }
}
