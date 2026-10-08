package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public interface Baker {
   BakedSimpleModel getModel(Identifier id);

   BlockModelPart getBlockPart();

   ErrorCollectingSpriteGetter getSpriteGetter();

   Baker.Vec3fInterner getVec3fInterner();

   <T> T compute(Baker.ResolvableCacheKey<T> key);

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   interface ResolvableCacheKey<T> {
      T compute(Baker baker);
   }

   @Environment(EnvType.CLIENT)
   interface Vec3fInterner {
      default Vector3fc intern(float x, float y, float z) {
         return this.intern(new Vector3f(x, y, z));
      }

      Vector3fc intern(Vector3fc vec);
   }
}
