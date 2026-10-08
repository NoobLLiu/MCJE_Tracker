package com.mcje.tracker.client;

import com.mcje.tracker.client.net.TrackerNetworking;
import com.mcje.tracker.client.render.TrackerRenderer;
import net.fabricmc.api.ClientModInitializer;

/**
 * 客户端模组入口：注册网络接收与描边渲染。
 */
public final class TrackerClient implements ClientModInitializer {

    public static final String MOD_ID = "mcjetracker";

    @Override
    public void onInitializeClient() {
        TrackerNetworking.register();
        TrackerRenderer.register();
    }
}