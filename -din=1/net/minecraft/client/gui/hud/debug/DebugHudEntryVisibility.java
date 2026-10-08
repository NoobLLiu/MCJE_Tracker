package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.StringIdentifiable.EnumCodec;

@Environment(EnvType.CLIENT)
public enum DebugHudEntryVisibility implements StringIdentifiable {
   ALWAYS_ON("alwaysOn"),
   IN_OVERLAY("inOverlay"),
   NEVER("never");

   public static final EnumCodec<DebugHudEntryVisibility> CODEC = StringIdentifiable.createCodec(DebugHudEntryVisibility::values);
   private final String id;

   DebugHudEntryVisibility(final String id) {
      this.id = id;
   }

   public String asString() {
      return this.id;
   }
}
