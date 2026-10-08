package com.mcje.tracker.config;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 全局配置（config.yml），支持热重载。
 */
public final class TrackerConfig {

    private final int defaultRange;
    private final int scanIntervalTicks;
    private final int maxResultsPerCategory;
    private final int maxBlocksScanned;
    private final boolean actionBarEnabled;

    private TrackerConfig(int defaultRange, int scanIntervalTicks, int maxResultsPerCategory,
                          int maxBlocksScanned, boolean actionBarEnabled) {
        this.defaultRange = defaultRange;
        this.scanIntervalTicks = scanIntervalTicks;
        this.maxResultsPerCategory = maxResultsPerCategory;
        this.maxBlocksScanned = maxBlocksScanned;
        this.actionBarEnabled = actionBarEnabled;
    }

    public static TrackerConfig load(JavaPlugin plugin) {
        var config = plugin.getConfig();
        return new TrackerConfig(
                config.getInt("default-range", 15),
                config.getInt("scan-interval-ticks", 10),
                config.getInt("max-results-per-category", 64),
                config.getInt("max-blocks-scanned", 40000),
                config.getBoolean("actionbar-enabled", true));
    }

    public int defaultRange() {
        return defaultRange;
    }

    public int scanIntervalTicks() {
        return scanIntervalTicks;
    }

    public int maxResultsPerCategory() {
        return maxResultsPerCategory;
    }

    public int maxBlocksScanned() {
        return maxBlocksScanned;
    }

    public boolean actionBarEnabled() {
        return actionBarEnabled;
    }
}