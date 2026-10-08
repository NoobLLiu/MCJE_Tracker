package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.SalmonEntity.Variant;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderState extends LivingEntityRenderState {
   public Variant variant = Variant.MEDIUM;
}
