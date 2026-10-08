package com.mcje.tracker.tracking;

import java.util.List;

/**
 * 单次扫描下发给客户端的完整快照。
 *
 * @param mask     已启用类别位掩码，0 表示当前无任何追踪
 * @param entities 掉落物 / 玩家目标
 * @param blocks   方块目标
 */
public record ScanResult(byte mask, List<TrackedEntity> entities, List<TrackedBlock> blocks) {
}