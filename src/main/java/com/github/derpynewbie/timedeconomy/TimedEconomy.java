package com.github.derpynewbie.timedeconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TimedEconomy extends JavaPlugin {

    private static Economy econ = null;
    private static FileConfiguration config = null;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!setupEconomy()) {
            getLogger().severe("Vault not found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupConfig();

    }

    private void setupConfig() {
        saveDefaultConfig();
        config = getConfig();
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

    public static Economy getEconomy() {
        return econ;
    }

    public static FileConfiguration getPluginConfig() {
        return config;
    }
}
