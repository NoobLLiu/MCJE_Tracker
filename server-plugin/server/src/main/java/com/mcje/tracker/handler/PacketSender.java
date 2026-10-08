package com.mcje.tracker.handler;

import com.mcje.tracker.registry.Channels;
import com.mcje.tracker.tracking.ScanResult;
import com.mcje.tracker.tracking.TrackedBlock;
import com.mcje.tracker.tracking.TrackedEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将扫描结果按协议编码为插件消息并下发给客户端。
 *
 * <p>字段布局需与客户端 {@code TrackerUpdatePayload} 完全一致；
 * 字符串采用 Minecraft {@code PacketByteBuf} 的格式（VarInt 长度前缀 + UTF-8 字节），
 * 以便客户端用 {@code readByteArray()} 读取。
 *
 * <pre>
 * byte    protocolVersion
 * byte    type (TYPE_UPDATE)
 * byte    enabledMask
 * int     entityCount
 *   循环： int entityId | byte kind | double x | double y | double z | [varint len + utf8 label]
 * int     blockCount
 *   循环： int x | int y | int z | [varint len + utf8 label]
 * </pre>
 */
public final class PacketSender {

    private final Plugin plugin;

    public PacketSender(Plugin plugin) {
        this.plugin = plugin;
    }

    public void sendUpdate(Player player, ScanResult result) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(64);
            DataOutputStream out = new DataOutputStream(buffer);

            out.writeByte(Channels.PROTOCOL_VERSION);
            out.writeByte(Channels.TYPE_UPDATE);
            out.writeByte(result.mask());

            out.writeInt(result.entities().size());
            for (TrackedEntity entity : result.entities()) {
                out.writeInt(entity.entityId());
                out.writeByte(entity.kind());
                out.writeDouble(entity.x());
                out.writeDouble(entity.y());
                out.writeDouble(entity.z());
                writeString(out, entity.label());
            }

            out.writeInt(result.blocks().size());
            for (TrackedBlock block : result.blocks()) {
                out.writeInt(block.x());
                out.writeInt(block.y());
                out.writeInt(block.z());
                writeString(out, block.label());
            }

            out.flush();
            player.sendPluginMessage(plugin, Channels.MAIN, buffer.toByteArray());
        } catch (IOException exception) {
            // 写入内存流不会失败，保留兜底避免异常中断扫描任务
            plugin.getLogger().warning("编码追踪数据包失败: " + exception.getMessage());
        }
    }

    /** 按 Minecraft 的格式写入字符串：VarInt 长度前缀 + UTF-8 字节 */
    private static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & ~0x7F) != 0) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value);
    }
}