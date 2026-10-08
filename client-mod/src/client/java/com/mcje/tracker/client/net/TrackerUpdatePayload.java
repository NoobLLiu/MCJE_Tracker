package com.mcje.tracker.client.net;

import com.mcje.tracker.client.TrackerClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端下发的整包追踪数据快照。
 *
 * <p>其 {@link #ID} 的命名空间/路径与插件端使用的插件消息通道名（{@code mcjetracker:main}）
 * 完全一致，因此插件通过 {@code sendPluginMessage} 发出的原始字节会被 Minecraft 按此
 * 编解码器解析，并交由 Fabric 的接收器处理。
 */
public record TrackerUpdatePayload(byte mask, List<TrackedEntity> entities, List<TrackedBlock> blocks)
        implements CustomPayload {

    public static final CustomPayload.Id<TrackerUpdatePayload> ID =
            new CustomPayload.Id<>(Identifier.of(TrackerClient.MOD_ID, "main"));

    public static final PacketCodec<RegistryByteBuf, TrackerUpdatePayload> CODEC =
            PacketCodec.of(TrackerUpdatePayload::encode, TrackerUpdatePayload::decode);

    /** 单次快照的条目上限，防御异常大包 */
    private static final int MAX_ENTRIES = 4096;

    public static TrackerUpdatePayload empty() {
        return new TrackerUpdatePayload((byte) 0, List.of(), List.of());
    }

    public static TrackerUpdatePayload decode(RegistryByteBuf buf) {
        byte version = buf.readByte();
        byte type = buf.readByte();
        byte mask = buf.readByte();
        if (version != Protocol.VERSION || type != Protocol.TYPE_UPDATE) {
            return empty();
        }

        int entityCount = Math.min(buf.readInt(), MAX_ENTRIES);
        List<TrackedEntity> entities = new ArrayList<>(entityCount);
        for (int i = 0; i < entityCount; i++) {
            int entityId = buf.readInt();
            byte kind = buf.readByte();
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            String label = new String(buf.readByteArray(), StandardCharsets.UTF_8);
            entities.add(new TrackedEntity(entityId, kind, x, y, z, label));
        }

        int blockCount = Math.min(buf.readInt(), MAX_ENTRIES);
        List<TrackedBlock> blocks = new ArrayList<>(blockCount);
        for (int i = 0; i < blockCount; i++) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            String label = new String(buf.readByteArray(), StandardCharsets.UTF_8);
            blocks.add(new TrackedBlock(x, y, z, label));
        }

        return new TrackerUpdatePayload(mask, entities, blocks);
    }

    public static void encode(TrackerUpdatePayload payload, RegistryByteBuf buf) {
        buf.writeByte(Protocol.VERSION);
        buf.writeByte(Protocol.TYPE_UPDATE);
        buf.writeByte(payload.mask());

        buf.writeInt(payload.entities().size());
        for (TrackedEntity entity : payload.entities()) {
            buf.writeInt(entity.entityId());
            buf.writeByte(entity.kind());
            buf.writeDouble(entity.x());
            buf.writeDouble(entity.y());
            buf.writeDouble(entity.z());
            buf.writeByteArray(entity.label().getBytes(StandardCharsets.UTF_8));
        }

        buf.writeInt(payload.blocks().size());
        for (TrackedBlock block : payload.blocks()) {
            buf.writeInt(block.x());
            buf.writeInt(block.y());
            buf.writeInt(block.z());
            buf.writeByteArray(block.label().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /** 掉落物 / 玩家：用 entityId 每帧重新解析实体，使描边跟随移动 */
    public record TrackedEntity(int entityId, byte kind, double x, double y, double z, String label) {
    }

    /** 方块：固定坐标 */
    public record TrackedBlock(int x, int y, int z, String label) {
    }
}