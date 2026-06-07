package com.virusker.merchantGuild.language;

import com.virusker.merchantGuild.MerchantGuild;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class LangManager {
    private final MerchantGuild plugin;
    private Map<String, String> messages = new HashMap<>();

    private static final String DEFAULT_LANGUAGE = "en";
    private final Set<String> availableLanguages = new HashSet<>(java.util.Arrays.asList("en.yml", "vi.yml"));

    public LangManager(MerchantGuild plugin) {
        this.plugin = plugin;
        saveDefaultLanguageFiles();
        reload();
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

    public void reload() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String currentLang = config.getString("language", DEFAULT_LANGUAGE);

        if (!availableLanguages.contains(currentLang + ".yml")) {
            plugin.getLogger().warning("Language '" + currentLang + "' is not available. Falling back to '" + DEFAULT_LANGUAGE + "'.");
            currentLang = DEFAULT_LANGUAGE;
        }

        File langFile = new File(plugin.getDataFolder(), "languages/" + currentLang + ".yml");
        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file 'languages/" + currentLang + ".yml' is missing. Falling back to '" + DEFAULT_LANGUAGE + "'.");
            currentLang = DEFAULT_LANGUAGE;
            langFile = new File(plugin.getDataFolder(), "languages/" + currentLang + ".yml");
            if (!langFile.exists()) {
                plugin.saveResource("languages/en.yml", false);
            }
        }

        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        Map<String, String> fresh = new HashMap<>();
        collectKeys(langConfig, "", fresh);
        this.messages = fresh;
    }

    private void collectKeys(ConfigurationSection section, String prefix, Map<String, String> sink) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = section.get(key);
            if (value instanceof ConfigurationSection nested) {
                collectKeys(nested, fullKey, sink);
            } else {
                sink.put(fullKey, section.getString(key, fullKey));
            }
        }
    }

    public String get(Message message, Object... args) {
        String msg = messages.getOrDefault(message.getKey(), message.getKey());
        return String.format(msg, args);
    }
}
