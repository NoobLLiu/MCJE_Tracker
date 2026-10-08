package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity.Data;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity.TextLines;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TextDisplayEntityRenderState extends DisplayEntityRenderState {
   public @Nullable Data data;
   public @Nullable TextLines textLines;

   @Override
   public boolean canRender() {
      return this.data != null;
   }
}
