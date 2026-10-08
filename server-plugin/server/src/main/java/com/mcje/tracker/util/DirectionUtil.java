package com.mcje.tracker.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 方向与距离计算，用于 actionbar 的"方向指引"。
 */
public final class DirectionUtil {

    /** 以玩家朝向为基准的 8 个水平方位，索引 0 为正前方，顺时针递增 */
    private static final String[] SECTORS = {"前", "右前", "右", "右后", "后", "左后", "左", "左前"};

    /** 目标与玩家处于同一竖直列（x、z 相同）时使用的提示 */
    private static final String SAME_COLUMN = "就在这里";

    private DirectionUtil() {
    }

    /**
     * 计算目标相对玩家的完整方位：水平方位（8 向，或同一竖直列的"就在这里"）+ 垂直方位（上/下）。
     *
     * <p>当目标与玩家的方块坐标 x、z 完全相同时，水平方位显示"就在这里"；
     * 垂直方位按方块 y 判断，同一高度时不附加任何内容。
     */
    public static String direction(Player player, double targetX, double targetY, double targetZ) {
        Location location = player.getLocation();
        String horizontal = horizontalDirection(location, targetX, targetZ);
        String vertical = verticalDirection(location.getBlockY(), targetY);
        return vertical.isEmpty() ? horizontal : horizontal + " " + vertical;
    }

    /**
     * 计算目标相对玩家视线的水平方位。
     * Minecraft 中 yaw=0 指向 +Z（南），90 指向 -X（西）。
     */
    private static String horizontalDirection(Location location, double targetX, double targetZ) {
        int targetBlockX = (int) Math.floor(targetX);
        int targetBlockZ = (int) Math.floor(targetZ);
        if (location.getBlockX() == targetBlockX && location.getBlockZ() == targetBlockZ) {
            return SAME_COLUMN;
        }

        double dx = targetX - location.getX();
        double dz = targetZ - location.getZ();
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double relative = ((targetYaw - location.getYaw()) % 360.0 + 360.0) % 360.0;
        int sector = (int) Math.round(relative / 45.0) % 8;
        return SECTORS[sector];
    }

    /** 目标方块 y 高于玩家返回"上"，低于玩家返回"下"，同层返回空串 */
    private static String verticalDirection(int playerBlockY, double targetY) {
        int targetBlockY = (int) Math.floor(targetY);
        if (targetBlockY > playerBlockY) {
            return "上";
        }
        if (targetBlockY < playerBlockY) {
            return "下";
        }
        return "";
    }

    public static double distance(Player player, double x, double y, double z) {
        Location location = player.getLocation();
        double dx = x - location.getX();
        double dy = y - location.getY();
        double dz = z - location.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}