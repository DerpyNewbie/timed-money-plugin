package com.github.derpynewbie.timedeconomy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public abstract class Scheduler implements Runnable {

    private static HashMap<Player, Integer> schedulers = new HashMap<>();

    protected final Player PLAYER;
    protected final String MESSAGE;
    protected final Long TICK;
    protected Integer taskId;

    public Scheduler(Player p, Long tick, String message) {
        this.PLAYER = p;
        this.TICK = tick;
        this.MESSAGE = message;
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
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TimedEconomy.getInstance(), this, TICK, TICK);
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
