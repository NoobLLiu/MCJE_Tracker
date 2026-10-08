package com.mcje.tracker.util;

import org.bukkit.Material;

import java.util.Locale;

/**
 * 材质键归一化工具。
 * 统一使用形如 "minecraft:diamond_ore" 的完整键做匹配，
 * 用户输入允许省略命名空间（如 "diamond_ore"）。
 */
public final class MaterialKeys {

    private MaterialKeys() {
    }

    /** 把用户输入归一化为完整键 */
    public static String normalize(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return value;
        }
        return value.indexOf(':') < 0 ? "minecraft:" + value : value;
    }

    /** 材质的完整键，例如 minecraft:diamond_ore */
    public static String keyOf(Material material) {
        return material.getKey().toString();
    }

    /** 去掉命名空间后的短名，用于展示与补全 */
    public static String shortKey(String key) {
        int index = key.indexOf(':');
        return index < 0 ? key : key.substring(index + 1);
    }
}