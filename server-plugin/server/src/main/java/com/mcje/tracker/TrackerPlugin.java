package com.mcje.tracker;

import com.mcje.tracker.command.TrackerCommand;
import com.mcje.tracker.config.TrackerConfig;
import com.mcje.tracker.handler.TrackerTask;
import com.mcje.tracker.listener.PlayerListener;
import com.mcje.tracker.registry.Channels;
import com.mcje.tracker.tracking.TrackerManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * MCJE_Tracker 服务端插件入口。
 */
public final class TrackerPlugin extends JavaPlugin {

    private TrackerConfig trackerConfig;
    private TrackerManager manager;
    private TrackerTask task;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        trackerConfig = TrackerConfig.load(this);
        manager = new TrackerManager(this, trackerConfig);

        getServer().getMessenger().registerOutgoingPluginChannel(this, Channels.MAIN);
        getServer().getPluginManager().registerEvents(new PlayerListener(manager), this);

        PluginCommand command = getCommand("tracker");
        if (command != null) {
            TrackerCommand executor = new TrackerCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        startTask();
        getLogger().info("MCJE_Tracker 已启用，下发通道: " + Channels.MAIN);
    }

    @Override
    public void onDisable() {
        stopTask();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        if (manager != null) {
            manager.clear();
        }
    }

    /** 热重载 config.yml 并重建扫描任务 */
    public void reloadAll() {
        stopTask();
        reloadConfig();
        trackerConfig = TrackerConfig.load(this);
        manager.setConfig(trackerConfig);
        startTask();
    }

    private void startTask() {
        int interval = Math.max(1, trackerConfig.scanIntervalTicks());
        task = new TrackerTask(manager);
        task.runTaskTimer(this, interval, interval);
    }

    private void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public TrackerManager manager() {
        return manager;
    }

    public TrackerConfig trackerConfig() {
        return trackerConfig;
    }
}