package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CopperGolemStatueBlock.Pose;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CopperGolemStatueBlockEntityRenderState extends BlockEntityRenderState {
   public Pose pose = Pose.STANDING;
   public Direction facing = Direction.NORTH;
}
