package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.WeightedPool;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WeightedBlockStateModel implements BlockStateModel {
   private final WeightedPool<BlockStateModel> models;
   private final Sprite particleSprite;

   public WeightedBlockStateModel(WeightedPool<BlockStateModel> models) {
      this.models = models;
      BlockStateModel blockStateModel = (BlockStateModel)((Weighted)models.getEntries().getFirst()).value();
      this.particleSprite = blockStateModel.particleSprite();
   }

   @Override
   public Sprite particleSprite() {
      return this.particleSprite;
   }

   @Override
   public void addParts(Random random, List<BlockModelPart> parts) {
      ((BlockStateModel)this.models.get(random)).addParts(random, parts);
   }

   @Environment(EnvType.CLIENT)
   public record Unbaked(WeightedPool<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked {
      @Override
      public BlockStateModel bake(Baker baker) {
         return new WeightedBlockStateModel(this.entries.transform(model -> model.bake(baker)));
      }

      @Override
      public void resolve(ResolvableModel.Resolver resolver) {
         this.entries.getEntries().forEach(entry -> ((BlockStateModel.Unbaked)entry.value()).resolve(resolver));
      }
   }
}
