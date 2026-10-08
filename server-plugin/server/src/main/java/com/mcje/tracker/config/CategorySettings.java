package com.mcje.tracker.config;

import com.mcje.tracker.tracking.TrackCategory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 单个玩家在某一类别（item/block/player）下的追踪设置：
 * 开关、追踪半径、以及被追踪的目标集合（已归一化的键）。
 */
public final class CategorySettings {

    private final TrackCategory category;
    private final Set<String> targets = new LinkedHashSet<>();
    private boolean enabled;
    private int range;

    public CategorySettings(TrackCategory category, int defaultRange) {
        this.category = category;
        this.range = defaultRange;
    }

    public TrackCategory category() {
        return category;
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int range() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Set<String> targets() {
        return Collections.unmodifiableSet(targets);
    }

    public boolean addTarget(String key) {
        return targets.add(key);
    }

    public boolean removeTarget(String key) {
        return targets.remove(key);
    }
}