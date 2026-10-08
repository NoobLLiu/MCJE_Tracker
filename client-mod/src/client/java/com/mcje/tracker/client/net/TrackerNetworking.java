package com.mcje.tracker.client.net;

import com.mcje.tracker.client.state.TrackerState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * 注册 S2C 载荷类型与接收器。
 */
public final class TrackerNetworking {

    private TrackerNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(TrackerUpdatePayload.ID, TrackerUpdatePayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(TrackerUpdatePayload.ID,
                (payload, context) -> TrackerState.update(payload));
    }
}