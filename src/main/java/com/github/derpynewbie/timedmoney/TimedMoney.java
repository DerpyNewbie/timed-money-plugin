package com.github.derpynewbie.timedmoney;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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

public class TimedMoney extends JavaPlugin implements Listener {

    private static Economy econ = null;
    private static TimedMoney instance = null;
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

        for (Player p :
                Bukkit.getOnlinePlayers()) {
            setSchedulerOnPlayer(p);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        setSchedulerOnPlayer(event.getPlayer());
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
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void setSchedulerOnPlayer(Player p) {
        Scheduler scheduler = new Scheduler(p, config.getLong("tick"), config.getString("message")) {
            @Override
            public void run() {
                double bal = config.getDouble("balance");
                EconomyResponse r = getEconomy().depositPlayer(PLAYER, bal);
                // (1st arg = display name[String], 2nd arg = balance[double], 3rd arg = time in seconds[double], 4th arg = time in minutes[double])

                if (r.transactionSuccess() && !MESSAGE.isEmpty())
                    PLAYER.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(MESSAGE, PLAYER.getDisplayName(), bal, (double) TICK / 20D, (double) TICK / 20D / 60D)));
                else
                    getLogger().severe(r.errorMessage);
            }
        };

        scheduler.schedule();
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static FileConfiguration getPluginConfig() {
        return config;
    }

    public static TimedMoney getInstance() {
        return instance;
    }
}
