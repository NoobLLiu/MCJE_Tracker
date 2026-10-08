package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TropicalFishEntity.Pattern;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderState extends LivingEntityRenderState {
   public Pattern variety = Pattern.FLOPPER;
   public int baseColor = -1;
   public int patternColor = -1;
}
