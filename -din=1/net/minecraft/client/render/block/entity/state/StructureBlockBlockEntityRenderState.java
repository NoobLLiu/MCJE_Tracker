package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBoxRendering.RenderMode;
import net.minecraft.block.entity.StructureBoxRendering.StructureBox;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StructureBlockBlockEntityRenderState extends BlockEntityRenderState {
   public boolean visible;
   public RenderMode renderMode;
   public StructureBox structureBox;
   public StructureBlockBlockEntityRenderState.@Nullable InvisibleRenderType @Nullable [] invisibleBlocks;
   public boolean @Nullable [] field_62682;

   @Environment(EnvType.CLIENT)
   public enum InvisibleRenderType {
      AIR,
      BARRIER,
      LIGHT,
      STRUCTURE_VOID;
   }
}
