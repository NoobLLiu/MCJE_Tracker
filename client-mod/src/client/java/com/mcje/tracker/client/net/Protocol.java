package com.mcje.tracker.client.net;

/**
 * 与插件端 {@code com.mcje.tracker.registry.Channels} 对应的协议常量。
 * 两端必须保持一致。
 */
public final class Protocol {

    public static final byte VERSION = 1;

    public static final byte TYPE_UPDATE = 0x01;

    public static final byte KIND_ITEM = 0x00;
    public static final byte KIND_BLOCK = 0x01;
    public static final byte KIND_PLAYER = 0x02;

    private Protocol() {
    }
}