package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.DisplayEntity.RenderState;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class DisplayEntityRenderState extends EntityRenderState {
   public @Nullable RenderState displayRenderState;
   public float lerpProgress;
   public float yaw;
   public float pitch;
   public float cameraYaw;
   public float cameraPitch;

   public abstract boolean canRender();
}
