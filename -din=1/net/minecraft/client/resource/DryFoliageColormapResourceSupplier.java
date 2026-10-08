package net.minecraft.client.resource;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.DryFoliageColors;

@Environment(EnvType.CLIENT)
public class DryFoliageColormapResourceSupplier extends SinglePreparationResourceReloader<int[]> {
   private static final Identifier DRY_FOLIAGE_COLORMAP = Identifier.ofVanilla("textures/colormap/dry_foliage.png");

   protected int[] reload(ResourceManager resourceManager, Profiler profiler) {
      try {
         return RawTextureDataLoader.loadRawTextureData(resourceManager, DRY_FOLIAGE_COLORMAP);
      } catch (IOException iOException) {
         throw new IllegalStateException("Failed to load dry foliage color texture", iOException);
      }
   }

   protected void apply(int[] is, ResourceManager resourceManager, Profiler profiler) {
      DryFoliageColors.setColorMap(is);
   }
}
