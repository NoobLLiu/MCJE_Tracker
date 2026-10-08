package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.block.SkullBlock.Type;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SkullBlockEntityRenderState extends BlockEntityRenderState {
   public float poweredTicks;
   public Direction facing = Direction.NORTH;
   public float yaw;
   public SkullType skullType = Type.ZOMBIE;
   public RenderLayer renderLayer;
}
