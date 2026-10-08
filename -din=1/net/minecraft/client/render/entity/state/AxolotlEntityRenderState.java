package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AxolotlEntity.Variant;

@Environment(EnvType.CLIENT)
public class AxolotlEntityRenderState extends LivingEntityRenderState {
   public Variant variant = Variant.DEFAULT;
   public float playingDeadValue;
   public float isMovingValue;
   public float inWaterValue = 1.0F;
   public float onGroundValue;
}
