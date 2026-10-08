package com.mcje.tracker.tracking;

/**
 * 一次扫描结果中的一个方块目标。
 */
public record TrackedBlock(int x, int y, int z, String label) {
}