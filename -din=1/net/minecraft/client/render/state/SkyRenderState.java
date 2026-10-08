package net.minecraft.client.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.MoonPhase;
import net.minecraft.world.dimension.DimensionType.Skybox;

@Environment(EnvType.CLIENT)
public class SkyRenderState {
   public Skybox skybox = Skybox.NONE;
   public boolean shouldRenderSkyDark;
   public float sunAngle;
   public float moonAngle;
   public float starAngle;
   public float rainGradient;
   public float starBrightness;
   public int sunriseAndSunsetColor;
   public MoonPhase moonPhase = MoonPhase.FULL_MOON;
   public int skyColor;
   public float endFlashIntensity;
   public float endFlashPitch;
   public float endFlashYaw;

   public void clear() {
      this.skybox = Skybox.NONE;
   }
}
