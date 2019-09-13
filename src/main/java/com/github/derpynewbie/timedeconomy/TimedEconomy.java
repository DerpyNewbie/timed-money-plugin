package com.github.derpynewbie.timedeconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TimedEconomy extends JavaPlugin implements Listener {

    private static Economy econ = null;
    private static TimedEconomy instance = null;
    private static FileConfiguration config = null;

    @Override
    public void onDisable() {
        super.onDisable();
        for (Player p :
                Scheduler.getScheduledPlayers()) {
            Scheduler.deleteTask(p);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("Vault not found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Scheduler scheduler = new Scheduler(event.getPlayer(), config.getLong("tick"), config.getString("message")) {
            @Override
            public void run() {
                double bal = config.getDouble("balance");
                getEconomy().bankDeposit(PLAYER.getName(), bal);
                // (1st arg = display name[String], 2nd arg = balance[double], 3rd arg = time in seconds[double], 4th arg = time in minutes[double])
                PLAYER.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(MESSAGE, PLAYER.getDisplayName(), bal, (double) TICK / 20D, (double) TICK / 20D / 60D)));
            }
        };

        scheduler.schedule();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Scheduler.deleteTask(event.getPlayer());
    }

    private void setupConfig() {
        saveDefaultConfig();
        config = getConfig();
    }

    private boolean setupEconomy() {
//        if (getServer().getPluginManager().getPlugin("Vault") == null) {
//            return false;
//        }
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

    public static TimedEconomy getInstance() {
        return instance;
    }
}
