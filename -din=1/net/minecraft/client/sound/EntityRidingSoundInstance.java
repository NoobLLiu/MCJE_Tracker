package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EntityRidingSoundInstance extends MovingSoundInstance {
   private final PlayerEntity player;
   private final Entity vehicle;
   private final boolean forUnderwater;
   private final float minVolume;
   private final float maxVolume;
   private final float multiplier;

   public EntityRidingSoundInstance(
      PlayerEntity player, Entity vehicle, boolean forUnderwater, SoundEvent sound, SoundCategory category, float volume, float maxVolume, float multiplier
   ) {
      super(sound, category, SoundInstance.createRandom());
      this.player = player;
      this.vehicle = vehicle;
      this.forUnderwater = forUnderwater;
      this.minVolume = volume;
      this.maxVolume = maxVolume;
      this.multiplier = multiplier;
      this.attenuationType = SoundInstance.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = volume;
   }

   @Override
   public boolean canPlay() {
      return !this.vehicle.isSilent();
   }

   @Override
   public boolean shouldAlwaysPlay() {
      return true;
   }

   protected boolean cannotPlayUnderwater() {
      return this.forUnderwater != this.vehicle.isSubmergedInWater();
   }

   protected float getVehicleSpeed() {
      return (float)this.vehicle.getVelocity().length();
   }

   protected boolean canPlaySound() {
      return true;
   }

   @Override
   public void tick() {
      if (this.vehicle.isRemoved() || !this.player.hasVehicle() || this.player.getVehicle() != this.vehicle) {
         this.setDone();
      } else if (this.cannotPlayUnderwater()) {
         this.volume = this.minVolume;
      } else {
         float f = this.getVehicleSpeed();
         if (f >= 0.01F && this.canPlaySound()) {
            this.volume = this.multiplier * MathHelper.clampedLerp(f, this.minVolume, this.maxVolume);
         } else {
            this.volume = this.minVolume;
         }
      }
   }
}
