package com.github.derpynewbie.timedmoney;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public abstract class Scheduler implements Runnable {

    private static HashMap<Player, Integer> schedulers = new HashMap<>();

    protected final Player PLAYER;
    protected final String MESSAGE;
    protected final String AFK_MESSAGE;
    protected final Long TICK;
    protected Location lastLocation;
    protected double bal;
    protected Integer taskId;

    public Scheduler(Player p, double defBal, Long tick, String message, String afkMessage) {
        this.PLAYER = p;
        this.MESSAGE = message;
        this.AFK_MESSAGE = afkMessage;
        this.TICK = tick;
        this.lastLocation = p.getLocation();
        this.bal = defBal;
    }

    public static Integer getPlayerTimer(Player p) {
        return schedulers.get(p);
    }

    public static Set<Player> getScheduledPlayers() {
        return schedulers.keySet();
    }

    public Integer schedule() {
        if (taskId != null)
            deleteTask(this);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TimedMoney.getInstance(), this, TICK, TICK);
        schedulers.put(PLAYER, taskId);
        return taskId;
    }

    public static boolean deleteTask(Player _player) {
        Integer _taskId = schedulers.get(_player);
        Bukkit.getScheduler().cancelTask(_taskId);
        return schedulers.remove(_player, _taskId);
    }

    public static boolean deleteTask(Scheduler _scheduler) {
        return deleteTask(_scheduler.PLAYER);
    }

}
