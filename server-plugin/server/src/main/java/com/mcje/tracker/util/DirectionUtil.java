package com.mcje.tracker.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 方向与距离计算，用于 actionbar 的"方向指引"。
 */
public final class DirectionUtil {

    /** 以玩家朝向为基准的 8 个方位，索引 0 为正前方，顺时针递增 */
    private static final String[] SECTORS = {"前", "右前", "右", "右后", "后", "左后", "左", "左前"};

    private DirectionUtil() {
    }

    /**
     * 计算目标相对玩家视线的水平方位。
     * Minecraft 中 yaw=0 指向 +Z（南），90 指向 -X（西）。
     */
    public static String direction(Player player, double targetX, double targetZ) {
        Location location = player.getLocation();
        double dx = targetX - location.getX();
        double dz = targetZ - location.getZ();
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double relative = ((targetYaw - location.getYaw()) % 360.0 + 360.0) % 360.0;
        int sector = (int) Math.round(relative / 45.0) % 8;
        return SECTORS[sector];
    }

    public static double distance(Player player, double x, double y, double z) {
        Location location = player.getLocation();
        double dx = x - location.getX();
        double dy = y - location.getY();
        double dz = z - location.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}