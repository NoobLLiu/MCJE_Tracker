package com.mcje.tracker.tracking;

import java.util.Locale;

/**
 * 可独立开关的三类追踪目标。
 */
public enum TrackCategory {

    ITEM("item", (byte) 0x01),
    BLOCK("block", (byte) 0x02),
    PLAYER("player", (byte) 0x04);

    private final String key;
    private final byte mask;

    TrackCategory(String key, byte mask) {
        this.key = key;
        this.mask = mask;
    }

    public String key() {
        return key;
    }

    /** 用于下发给客户端的启用位掩码 */
    public byte mask() {
        return mask;
    }

    public static TrackCategory byKey(String raw) {
        if (raw == null) {
            return null;
        }
        String lower = raw.toLowerCase(Locale.ROOT);
        for (TrackCategory category : values()) {
            if (category.key.equals(lower)) {
                return category;
            }
        }
        return null;
    }
}