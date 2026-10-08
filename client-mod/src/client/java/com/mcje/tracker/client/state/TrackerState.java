package com.mcje.tracker.client.state;

import com.mcje.tracker.client.net.TrackerUpdatePayload;

/**
 * 保存最近一次从服务端收到的追踪快照。
 *
 * <p>网络线程写入、渲染线程读取，因此用 volatile 整体替换保证可见性与一致性。
 */
public final class TrackerState {

    private static volatile TrackerUpdatePayload snapshot = TrackerUpdatePayload.empty();

    private TrackerState() {
    }

    public static void update(TrackerUpdatePayload payload) {
        snapshot = payload;
    }

    public static TrackerUpdatePayload snapshot() {
        return snapshot;
    }
}