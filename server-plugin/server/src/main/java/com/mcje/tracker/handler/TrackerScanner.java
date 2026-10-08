package com.mcje.tracker.handler;

import com.mcje.tracker.config.CategorySettings;
import com.mcje.tracker.config.PlayerTrackSettings;
import com.mcje.tracker.config.TrackerConfig;
import com.mcje.tracker.registry.Channels;
import com.mcje.tracker.tracking.ScanResult;
import com.mcje.tracker.tracking.TrackCategory;
import com.mcje.tracker.tracking.TrackedBlock;
import com.mcje.tracker.tracking.TrackedEntity;
import com.mcje.tracker.tracking.TrackerManager;
import com.mcje.tracker.util.MaterialKeys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 在一个玩家周围按已启用类别做一次扫描，产出待下发的快照。
 */
public final class TrackerScanner {

    private final TrackerManager manager;

    public TrackerScanner(TrackerManager manager) {
        this.manager = manager;
    }

    public ScanResult scan(Player player) {
        PlayerTrackSettings settings = manager.settings(player);
        TrackerConfig config = manager.config();

        byte mask = 0;
        List<TrackedEntity> entities = new ArrayList<>();
        List<TrackedBlock> blocks = new ArrayList<>();

        CategorySettings itemSettings = settings.get(TrackCategory.ITEM);
        if (itemSettings.enabled() && !itemSettings.targets().isEmpty()) {
            mask |= TrackCategory.ITEM.mask();
            collectItems(player, itemSettings, entities, config);
        }

        CategorySettings playerSettings = settings.get(TrackCategory.PLAYER);
        if (playerSettings.enabled() && !playerSettings.targets().isEmpty()) {
            mask |= TrackCategory.PLAYER.mask();
            collectPlayers(player, playerSettings, entities, config);
        }

        CategorySettings blockSettings = settings.get(TrackCategory.BLOCK);
        if (blockSettings.enabled() && !blockSettings.targets().isEmpty()) {
            mask |= TrackCategory.BLOCK.mask();
            collectBlocks(player, blockSettings, blocks, config);
        }

        return new ScanResult(mask, entities, blocks);
    }

    private void collectItems(Player player, CategorySettings settings, List<TrackedEntity> out, TrackerConfig config) {
        int limit = config.maxResultsPerCategory();
        Location origin = player.getLocation();
        for (Entity entity : player.getWorld().getNearbyEntities(origin, settings.range(), settings.range(), settings.range())) {
            if (!(entity instanceof Item itemEntity)) {
                continue;
            }
            Material type = itemEntity.getItemStack().getType();
            String key = MaterialKeys.keyOf(type);
            if (!settings.targets().contains(key)) {
                continue;
            }
            Location location = entity.getLocation();
            out.add(new TrackedEntity(entity.getEntityId(), Channels.KIND_ITEM,
                    location.getX(), location.getY(), location.getZ(), MaterialKeys.shortKey(key)));
            if (out.size() >= limit) {
                return;
            }
        }
    }

    private void collectPlayers(Player player, CategorySettings settings, List<TrackedEntity> out, TrackerConfig config) {
        int limit = config.maxResultsPerCategory();
        Location origin = player.getLocation();
        for (Entity entity : player.getWorld().getNearbyEntities(origin, settings.range(), settings.range(), settings.range())) {
            if (!(entity instanceof Player other) || other.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            String key = other.getName().toLowerCase(Locale.ROOT);
            if (!settings.targets().contains(key)) {
                continue;
            }
            Location location = entity.getLocation();
            out.add(new TrackedEntity(entity.getEntityId(), Channels.KIND_PLAYER,
                    location.getX(), location.getY(), location.getZ(), other.getName()));
            if (out.size() >= limit) {
                return;
            }
        }
    }

    private void collectBlocks(Player player, CategorySettings settings, List<TrackedBlock> out, TrackerConfig config) {
        World world = player.getWorld();
        Location center = player.getLocation();
        int radius = settings.range();
        int limit = config.maxResultsPerCategory();
        int budget = config.maxBlocksScanned();
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        int radiusSquared = radius * radius;
        int scanned = 0;

        scan:
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }
                    if (++scanned > budget) {
                        break scan;
                    }
                    int x = cx + dx;
                    int y = cy + dy;
                    int z = cz + dz;
                    // 超出世界高度时 getBlockAt 会返回空气，无需额外边界判断
                    Material type = world.getBlockAt(x, y, z).getType();
                    if (type.isAir()) {
                        continue;
                    }
                    String key = MaterialKeys.keyOf(type);
                    if (!settings.targets().contains(key)) {
                        continue;
                    }
                    out.add(new TrackedBlock(x, y, z, MaterialKeys.shortKey(key)));
                    if (out.size() >= limit) {
                        break scan;
                    }
                }
            }
        }
    }
}