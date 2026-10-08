package com.mcje.tracker.tracking;

import com.mcje.tracker.TrackerPlugin;
import com.mcje.tracker.config.PlayerTrackSettings;
import com.mcje.tracker.config.TrackerConfig;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持有所有在线玩家的追踪设置，并提供按需创建 / 清理。
 */
public final class TrackerManager {

    private final TrackerPlugin plugin;
    private final Map<UUID, PlayerTrackSettings> settings = new ConcurrentHashMap<>();
    private volatile TrackerConfig config;

    public TrackerManager(TrackerPlugin plugin, TrackerConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public TrackerPlugin plugin() {
        return plugin;
    }

    public TrackerConfig config() {
        return config;
    }

    public void setConfig(TrackerConfig config) {
        this.config = config;
    }

    public PlayerTrackSettings settings(Player player) {
        return settings.computeIfAbsent(player.getUniqueId(),
                id -> new PlayerTrackSettings(id, config.defaultRange()));
    }

    public void remove(UUID uuid) {
        settings.remove(uuid);
    }

    public void clear() {
        settings.clear();
    }
}