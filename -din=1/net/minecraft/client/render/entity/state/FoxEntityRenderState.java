package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.FoxEntity.Variant;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderState extends ItemHolderEntityRenderState {
   public float headRoll;
   public float bodyRotationHeightOffset;
   public boolean inSneakingPose;
   public boolean sleeping;
   public boolean sitting;
   public boolean walking;
   public boolean chasing;
   public Variant type = Variant.DEFAULT;
}
