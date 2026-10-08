package com.mcje.tracker.registry;

/**
 * 服务端 <-> 客户端自定义通道与协议常量。
 * 客户端模组需与这里的通道名、协议版本、类型/种类常量完全一致。
 */
public final class Channels {

    /** 插件消息通道名（namespace:path） */
    public static final String MAIN = "mcjetracker:main";

    /** 协议版本，两端不一致时客户端会忽略数据 */
    public static final byte PROTOCOL_VERSION = 1;

    /** 数据包类型：整包追踪数据下发 */
    public static final byte TYPE_UPDATE = 0x01;

    /** 目标种类：掉落物 */
    public static final byte KIND_ITEM = 0x00;
    /** 目标种类：方块 */
    public static final byte KIND_BLOCK = 0x01;
    /** 目标种类：玩家 */
    public static final byte KIND_PLAYER = 0x02;

    private Channels() {
    }
}