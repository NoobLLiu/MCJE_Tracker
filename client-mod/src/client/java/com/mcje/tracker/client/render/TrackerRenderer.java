package com.mcje.tracker.client.render;

import com.mcje.tracker.client.TrackerClient;
import com.mcje.tracker.client.net.Protocol;
import com.mcje.tracker.client.net.TrackerUpdatePayload;
import com.mcje.tracker.client.state.TrackerState;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * 在客户端视角内为被追踪目标绘制高亮描边（线框盒）。
 *
 * <p>描边使用禁用深度测试的自定义渲染管线，因此可以穿墙透视；
 * 类型用颜色区分：掉落物=紫、方块=青、玩家=绿。
 * 当目标数量过多时，只绘制前 {@link #MAX_HIGHLIGHTS} 个以减少卡顿。
 */
public final class TrackerRenderer {

    private static final float LINE_WIDTH = 2.0F;

    /** 单帧最多绘制的高亮框数量（按服务端下发顺序：物品→玩家→方块） */
    private static final int MAX_HIGHLIGHTS = 15;

    // ColorHelper.fromFloats 的参数顺序为 (alpha, red, green, blue)
    private static final int COLOR_ITEM = ColorHelper.fromFloats(1.0F, 1.0F, 0.2F, 1.0F);
    private static final int COLOR_BLOCK = ColorHelper.fromFloats(1.0F, 0.2F, 1.0F, 1.0F);
    private static final int COLOR_PLAYER = ColorHelper.fromFloats(1.0F, 0.2F, 1.0F, 0.2F);

    /**
     * 复制自原版 {@code rendertype_lines} 管线，但关闭深度测试，从而实现穿墙透视。
     * 着色器与原版 lines 一致，因此运行时会复用已编译的程序。
     */
    private static final RenderPipeline XRAY_LINES_PIPELINE = RenderPipeline.builder()
            .withLocation(Identifier.of(TrackerClient.MOD_ID, "pipeline/xray_lines"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withUniform("Globals", UniformType.UNIFORM_BUFFER)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.LINES)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build();

    private static final RenderLayer XRAY_LINES = RenderLayer.of(
            "mcjetracker_xray_lines",
            RenderSetup.builder(XRAY_LINES_PIPELINE)
                    .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .build());

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
        VertexConsumer buffer = context.consumers().getBuffer(XRAY_LINES);

        int drawn = 0;

        for (TrackerUpdatePayload.TrackedEntity entity : snapshot.entities()) {
            if (drawn >= MAX_HIGHLIGHTS) {
                return;
            }
            drawBox(matrices, buffer, entityBox(world, entity), camera, colorOf(entity.kind()));
            drawn++;
        }

        for (TrackerUpdatePayload.TrackedBlock block : snapshot.blocks()) {
            if (drawn >= MAX_HIGHLIGHTS) {
                return;
            }
            Box box = new Box(block.x(), block.y(), block.z(),
                    block.x() + 1.0, block.y() + 1.0, block.z() + 1.0);
            drawBox(matrices, buffer, box, camera, COLOR_BLOCK);
            drawn++;
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