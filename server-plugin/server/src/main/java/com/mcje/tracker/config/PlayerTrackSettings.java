package com.mcje.tracker.config;

import com.mcje.tracker.tracking.TrackCategory;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * 某个玩家的全部追踪设置，按类别分组。
 */
public final class PlayerTrackSettings {

    private final UUID uuid;
    private final Map<TrackCategory, CategorySettings> categories = new EnumMap<>(TrackCategory.class);

    public PlayerTrackSettings(UUID uuid, int defaultRange) {
        this.uuid = uuid;
        for (TrackCategory category : TrackCategory.values()) {
            categories.put(category, new CategorySettings(category, defaultRange));
        }
    }

    public UUID uuid() {
        return uuid;
    }

    public CategorySettings get(TrackCategory category) {
        return categories.get(category);
    }

    /** 是否有任意一类处于开启状态 */
    public boolean anyEnabled() {
        for (CategorySettings settings : categories.values()) {
            if (settings.enabled()) {
                return true;
            }
        }
        return false;
    }
}