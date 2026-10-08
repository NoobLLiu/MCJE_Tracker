package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.Cracks.CrackLevel;

@Environment(EnvType.CLIENT)
public class IronGolemEntityRenderState extends LivingEntityRenderState {
   public float attackTicksLeft;
   public int lookingAtVillagerTicks;
   public CrackLevel crackLevel = CrackLevel.NONE;
}
