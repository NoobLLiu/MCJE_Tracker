package com.mcje.tracker.tracking;

/**
 * 一次扫描结果中的一个实体目标（掉落物 / 玩家）。
 * 客户端用 entityId 每帧重新解析实体，从而让描边跟随目标移动。
 */
public record TrackedEntity(int entityId, byte kind, double x, double y, double z, String label) {
}