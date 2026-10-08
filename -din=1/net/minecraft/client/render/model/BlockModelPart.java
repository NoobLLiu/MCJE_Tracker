package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface BlockModelPart {
   List<BakedQuad> getQuads(@Nullable Direction side);

   boolean useAmbientOcclusion();

   Sprite particleSprite();

   @Environment(EnvType.CLIENT)
   interface Unbaked extends ResolvableModel {
      BlockModelPart bake(Baker baker);
   }
}
