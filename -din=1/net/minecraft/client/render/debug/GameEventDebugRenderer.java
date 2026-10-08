package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo.Style;

@Environment(EnvType.CLIENT)
public class GameEventDebugRenderer implements DebugRenderer.Renderer {
   private static final float field_32900 = 1.0F;

   private void forEachEventData(DebugDataStore dataStore, GameEventDebugRenderer.EventConsumer consumer) {
      dataStore.forEachBlockData(DebugSubscriptionTypes.GAME_EVENT_LISTENERS, (pos, data) -> consumer.accept(pos.toCenterPos(), data.listenerRadius()));
      dataStore.forEachEntityData(DebugSubscriptionTypes.GAME_EVENT_LISTENERS, (entity, data) -> consumer.accept(entity.getEntityPos(), data.listenerRadius()));
   }

   @Override
   public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
      this.forEachEventData(store, (pos, radius) -> {
         double d = radius * 2.0;
         GizmoDrawing.box(Box.of(pos, d, d, d), DrawStyle.filled(ColorHelper.fromFloats(0.35F, 1.0F, 1.0F, 0.0F)));
      });
      this.forEachEventData(
         store,
         (pos, radius) -> GizmoDrawing.box(Box.of(pos, 0.5, 1.0, 0.5).offset(0.0, 0.5, 0.0), DrawStyle.filled(ColorHelper.fromFloats(0.35F, 1.0F, 1.0F, 0.0F)))
      );
      this.forEachEventData(store, (pos, radius) -> {
         GizmoDrawing.text("Listener Origin", pos.add(0.0, 1.8, 0.0), Style.left().scaled(0.4F));
         GizmoDrawing.text(BlockPos.ofFloored(pos).toString(), pos.add(0.0, 1.5, 0.0), Style.left(-6959665).scaled(0.4F));
      });
      store.forEachEvent(DebugSubscriptionTypes.GAME_EVENTS, (data, remainingTime, expiry) -> {
         Vec3d vec3d = data.pos();
         double d = 0.4;
         Box box = Box.of(vec3d.add(0.0, 0.5, 0.0), 0.4, 0.9, 0.4);
         GizmoDrawing.box(box, DrawStyle.filled(ColorHelper.fromFloats(0.2F, 1.0F, 1.0F, 1.0F)));
         GizmoDrawing.text(data.event().getIdAsString(), vec3d.add(0.0, 0.85, 0.0), Style.left(-7564911).scaled(0.12F));
      });
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   interface EventConsumer {
      void accept(Vec3d pos, int radius);
   }
}
