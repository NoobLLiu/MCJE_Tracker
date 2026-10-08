package com.mcje.tracker.listener;

import com.mcje.tracker.tracking.TrackerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 玩家离线时清理其追踪设置，避免内存堆积。
 */
public final class PlayerListener implements Listener {

    private final TrackerManager manager;

    public PlayerListener(TrackerManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.remove(event.getPlayer().getUniqueId());
    }
}