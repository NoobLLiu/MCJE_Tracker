package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GuardianEntityRenderer extends MobEntityRenderer<GuardianEntity, GuardianEntityRenderState, GuardianEntityModel> {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian.png");
   private static final Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/guardian_beam.png");
   private static final RenderLayer LAYER = RenderLayers.entityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

   public GuardianEntityRenderer(EntityRendererFactory.Context context) {
      this(context, 0.5F, EntityModelLayers.GUARDIAN);
   }

   protected GuardianEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius, EntityModelLayer layer) {
      super(ctx, new GuardianEntityModel(ctx.getPart(layer)), shadowRadius);
   }

   public boolean shouldRender(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
      if (super.shouldRender(guardianEntity, frustum, d, e, f)) {
         return true;
      }

      if (guardianEntity.hasBeamTarget()) {
         LivingEntity livingEntity = guardianEntity.getBeamTarget();
         if (livingEntity != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, livingEntity.getHeight() * 0.5, 1.0F);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0F);
            return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
         }
      }

      return false;
   }

   private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
      double d = MathHelper.lerp(delta, entity.lastRenderX, entity.getX());
      double e = MathHelper.lerp(delta, entity.lastRenderY, entity.getY()) + yOffset;
      double f = MathHelper.lerp(delta, entity.lastRenderZ, entity.getZ());
      return new Vec3d(d, e, f);
   }

   public void render(
      GuardianEntityRenderState guardianEntityRenderState,
      MatrixStack matrixStack,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      CameraRenderState cameraRenderState
   ) {
      super.render(guardianEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
      Vec3d vec3d = guardianEntityRenderState.beamTargetPos;
      if (vec3d != null) {
         float f = guardianEntityRenderState.beamTicks * 0.5F % 1.0F;
         matrixStack.push();
         matrixStack.translate(0.0F, guardianEntityRenderState.standingEyeHeight, 0.0F);
         renderBeam(
            matrixStack,
            orderedRenderCommandQueue,
            vec3d.subtract(guardianEntityRenderState.cameraPosVec),
            guardianEntityRenderState.beamTicks,
            guardianEntityRenderState.beamProgress,
            f
         );
         matrixStack.pop();
      }
   }

   private static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, Vec3d offset, float beamTicks, float beamProgress, float f) {
      float g = (float)(offset.length() + 1.0);
      offset = offset.normalize();
      float h = (float)Math.acos(offset.y);
      float i = (float) (Math.PI / 2) - (float)Math.atan2(offset.z, offset.x);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (180.0F / (float)Math.PI)));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * (180.0F / (float)Math.PI)));
      float j = beamTicks * 0.05F * -1.5F;
      float k = beamProgress * beamProgress;
      int l = 64 + (int)(k * 191.0F);
      int m = 32 + (int)(k * 191.0F);
      int n = 128 - (int)(k * 64.0F);
      float o = 0.2F;
      float p = 0.282F;
      float q = MathHelper.cos(j + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
      float r = MathHelper.sin(j + (float) (Math.PI * 3.0 / 4.0)) * 0.282F;
      float s = MathHelper.cos(j + (float) (Math.PI / 4)) * 0.282F;
      float t = MathHelper.sin(j + (float) (Math.PI / 4)) * 0.282F;
      float u = MathHelper.cos(j + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
      float v = MathHelper.sin(j + ((float) Math.PI * 5.0F / 4.0F)) * 0.282F;
      float w = MathHelper.cos(j + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
      float x = MathHelper.sin(j + ((float) Math.PI * 7.0F / 4.0F)) * 0.282F;
      float y = MathHelper.cos(j + (float) Math.PI) * 0.2F;
      float z = MathHelper.sin(j + (float) Math.PI) * 0.2F;
      float aa = MathHelper.cos(j + 0.0F) * 0.2F;
      float ab = MathHelper.sin(j + 0.0F) * 0.2F;
      float ac = MathHelper.cos(j + (float) (Math.PI / 2)) * 0.2F;
      float ad = MathHelper.sin(j + (float) (Math.PI / 2)) * 0.2F;
      float ae = MathHelper.cos(j + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
      float af = MathHelper.sin(j + (float) (Math.PI * 3.0 / 2.0)) * 0.2F;
      float ag = g;
      float ah = 0.0F;
      float ai = 0.4999F;
      float aj = -1.0F + f;
      float ak = aj + g * 2.5F;
      queue.submitCustom(matrices, LAYER, (matricesEntry, vertexConsumer) -> {
         vertex(vertexConsumer, matricesEntry, y, ag, z, l, m, n, 0.4999F, ak);
         vertex(vertexConsumer, matricesEntry, y, 0.0F, z, l, m, n, 0.4999F, aj);
         vertex(vertexConsumer, matricesEntry, aa, 0.0F, ab, l, m, n, 0.0F, aj);
         vertex(vertexConsumer, matricesEntry, aa, ag, ab, l, m, n, 0.0F, ak);
         vertex(vertexConsumer, matricesEntry, ac, ag, ad, l, m, n, 0.4999F, ak);
         vertex(vertexConsumer, matricesEntry, ac, 0.0F, ad, l, m, n, 0.4999F, aj);
         vertex(vertexConsumer, matricesEntry, ae, 0.0F, af, l, m, n, 0.0F, aj);
         vertex(vertexConsumer, matricesEntry, ae, ag, af, l, m, n, 0.0F, ak);
         float acx = MathHelper.floor(beamTicks) % 2 == 0 ? 0.5F : 0.0F;
         vertex(vertexConsumer, matricesEntry, q, ag, r, l, m, n, 0.5F, acx + 0.5F);
         vertex(vertexConsumer, matricesEntry, s, ag, t, l, m, n, 1.0F, acx + 0.5F);
         vertex(vertexConsumer, matricesEntry, w, ag, x, l, m, n, 1.0F, acx);
         vertex(vertexConsumer, matricesEntry, u, ag, v, l, m, n, 0.5F, acx);
      });
   }

   private static void vertex(
      VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v
   ) {
      vertexConsumer.vertex(matrix, x, y, z)
         .color(red, green, blue, 255)
         .texture(u, v)
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(matrix, 0.0F, 1.0F, 0.0F);
   }

   public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
      return TEXTURE;
   }

   public GuardianEntityRenderState createRenderState() {
      return new GuardianEntityRenderState();
   }

   public void updateRenderState(GuardianEntity guardianEntity, GuardianEntityRenderState guardianEntityRenderState, float f) {
      super.updateRenderState(guardianEntity, guardianEntityRenderState, f);
      guardianEntityRenderState.spikesExtension = guardianEntity.getSpikesExtension(f);
      guardianEntityRenderState.tailAngle = guardianEntity.getTailAngle(f);
      guardianEntityRenderState.cameraPosVec = guardianEntity.getCameraPosVec(f);
      Entity entity = getBeamTarget(guardianEntity);
      if (entity != null) {
         guardianEntityRenderState.rotationVec = guardianEntity.getRotationVec(f);
         guardianEntityRenderState.lookAtPos = entity.getCameraPosVec(f);
      } else {
         guardianEntityRenderState.rotationVec = null;
         guardianEntityRenderState.lookAtPos = null;
      }

      LivingEntity livingEntity = guardianEntity.getBeamTarget();
      if (livingEntity != null) {
         guardianEntityRenderState.beamProgress = guardianEntity.getBeamProgress(f);
         guardianEntityRenderState.beamTicks = guardianEntity.getBeamTicks() + f;
         guardianEntityRenderState.beamTargetPos = this.fromLerpedPosition(livingEntity, livingEntity.getHeight() * 0.5, f);
      } else {
         guardianEntityRenderState.beamTargetPos = null;
      }
   }

   private static @Nullable Entity getBeamTarget(GuardianEntity guardian) {
      Entity entity = MinecraftClient.getInstance().getCameraEntity();
      return (Entity)(guardian.hasBeamTarget() ? guardian.getBeamTarget() : entity);
   }
}
