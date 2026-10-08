package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity.Data;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BlockDisplayEntityRenderState extends DisplayEntityRenderState {
   public @Nullable Data data;

   @Override
   public boolean canRender() {
      return this.data != null;
   }
}
