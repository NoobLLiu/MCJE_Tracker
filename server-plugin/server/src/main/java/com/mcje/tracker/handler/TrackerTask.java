package com.mcje.tracker.handler;

import com.mcje.tracker.config.PlayerTrackSettings;
import com.mcje.tracker.config.TrackerConfig;
import com.mcje.tracker.registry.Channels;
import com.mcje.tracker.tracking.ScanResult;
import com.mcje.tracker.tracking.TrackedBlock;
import com.mcje.tracker.tracking.TrackedEntity;
import com.mcje.tracker.tracking.TrackerManager;
import com.mcje.tracker.util.DirectionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;

/**
 * 周期任务：扫描每位玩家 -> 下发位置包 -> 发送 actionbar 方向指引。
 */
public final class TrackerTask extends BukkitRunnable {

    private final TrackerManager manager;
    private final TrackerScanner scanner;
    private final PacketSender sender;

    public TrackerTask(TrackerManager manager) {
        this.manager = manager;
        this.scanner = new TrackerScanner(manager);
        this.sender = new PacketSender(manager.plugin());
    }

    @Override
    public void run() {
        TrackerConfig config = manager.config();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerTrackSettings settings = manager.settings(player);
            ScanResult result = settings.anyEnabled()
                    ? scanner.scan(player)
                    : new ScanResult((byte) 0, List.of(), List.of());

            sender.sendUpdate(player, result);

            if (config.actionBarEnabled() && result.mask() != 0) {
                sendActionBar(player, result);
            }
        }
    }

    private void sendActionBar(Player player, ScanResult result) {
        double best = Double.MAX_VALUE;
        String label = null;
        String category = null;
        double targetX = 0;
        double targetY = 0;
        double targetZ = 0;

        for (TrackedEntity entity : result.entities()) {
            double distance = DirectionUtil.distance(player, entity.x(), entity.y(), entity.z());
            if (distance < best) {
                best = distance;
                targetX = entity.x();
                targetY = entity.y();
                targetZ = entity.z();
                label = entity.label();
                category = entity.kind() == Channels.KIND_ITEM ? "物品" : "玩家";
            }
        }

        for (TrackedBlock block : result.blocks()) {
            double x = block.x() + 0.5;
            double y = block.y() + 0.5;
            double z = block.z() + 0.5;
            double distance = DirectionUtil.distance(player, x, y, z);
            if (distance < best) {
                best = distance;
                targetX = x;
                targetY = y;
                targetZ = z;
                label = block.label();
                category = "方块";
            }
        }

        if (label == null) {
            return;
        }

        String direction = DirectionUtil.direction(player, targetX, targetY, targetZ);
        String coords = String.format(Locale.ROOT, "(%.1f, %.1f, %.1f)", targetX, targetY, targetZ);
        String distanceText = String.format(Locale.ROOT, "%.1f", best);

        Component message = Component.text()
                .append(Component.text("追踪[" + category + "] ", NamedTextColor.GRAY))
                .append(Component.text(label, NamedTextColor.YELLOW))
                .append(Component.text(" 位于 ", NamedTextColor.GRAY))
                .append(Component.text(coords, NamedTextColor.AQUA))
                .append(Component.text(" 方向 ", NamedTextColor.GRAY))
                .append(Component.text(direction, NamedTextColor.GREEN))
                .append(Component.text(" 距离 ", NamedTextColor.GRAY))
                .append(Component.text(distanceText + "格", NamedTextColor.GOLD))
                .build();

        player.sendActionBar(message);
    }
}