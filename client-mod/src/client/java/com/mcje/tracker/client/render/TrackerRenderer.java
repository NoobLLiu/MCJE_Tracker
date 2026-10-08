package com.mcje.tracker.client.render;

import com.mcje.tracker.client.net.Protocol;
import com.mcje.tracker.client.net.TrackerUpdatePayload;
import com.mcje.tracker.client.state.TrackerState;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * 在客户端视角内为被追踪目标绘制高亮描边（线框盒）。
 */
public final class TrackerRenderer {

    private static final float LINE_WIDTH = 2.0F;
    private static final int COLOR_ITEM = ColorHelper.fromFloats(1.0F, 1.0F, 0.2F, 1.0F);
    private static final int COLOR_BLOCK = ColorHelper.fromFloats(0.2F, 1.0F, 1.0F, 1.0F);
    private static final int COLOR_PLAYER = ColorHelper.fromFloats(0.2F, 1.0F, 0.2F, 1.0F);

    private TrackerRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(TrackerRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        TrackerUpdatePayload snapshot = TrackerState.snapshot();
        if (snapshot.mask() == 0) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) {
            return;
        }

        Vec3d camera = client.gameRenderer.getCamera().getCameraPos();
        MatrixStack matrices = context.matrices();
        VertexConsumer buffer = context.consumers().getBuffer(RenderLayers.lines());

        for (TrackerUpdatePayload.TrackedEntity entity : snapshot.entities()) {
            drawBox(matrices, buffer, entityBox(world, entity), camera, colorOf(entity.kind()));
        }

        for (TrackerUpdatePayload.TrackedBlock block : snapshot.blocks()) {
            Box box = new Box(block.x(), block.y(), block.z(),
                    block.x() + 1.0, block.y() + 1.0, block.z() + 1.0);
            drawBox(matrices, buffer, box, camera, COLOR_BLOCK);
        }
    }

    private static Box entityBox(ClientWorld world, TrackerUpdatePayload.TrackedEntity target) {
        Entity entity = world.getEntityById(target.entityId());
        if (entity != null) {
            return entity.getBoundingBox().expand(0.1);
        }
        // 实体尚未加载时退回服务端下发的位置，用近似大小的盒子兜底
        return new Box(target.x() - 0.3, target.y() - 0.3, target.z() - 0.3,
                target.x() + 0.3, target.y() + 0.3, target.z() + 0.3);
    }

    private static void drawBox(MatrixStack matrices, VertexConsumer buffer, Box box, Vec3d camera, int color) {
        VoxelShape shape = VoxelShapes.cuboid(box);
        // 与 vanilla 一致：形状用世界坐标，偏移取相机坐标的相反数（相机相对渲染）
        VertexRendering.drawOutline(matrices, buffer, shape,
                -camera.x, -camera.y, -camera.z, color, LINE_WIDTH);
    }

    private static int colorOf(byte kind) {
        return switch (kind) {
            case Protocol.KIND_BLOCK -> COLOR_BLOCK;
            case Protocol.KIND_PLAYER -> COLOR_PLAYER;
            default -> COLOR_ITEM;
        };
    }
}