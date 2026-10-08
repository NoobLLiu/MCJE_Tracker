package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, ModelElementFace.@Nullable UV uvs, AxisRotation rotation) {
   public static final int field_32789 = -1;

   public static float getUValue(ModelElementFace.UV uv, AxisRotation rotation, int index) {
      return uv.getUVertices(rotation.rotate(index)) / 16.0F;
   }

   public static float getVValue(ModelElementFace.UV uv, AxisRotation rotation, int index) {
      return uv.getVVertices(rotation.rotate(index)) / 16.0F;
   }

   @Environment(EnvType.CLIENT)
   protected static class Deserializer implements JsonDeserializer<ModelElementFace> {
      private static final int DEFAULT_TINT_INDEX = -1;
      private static final int field_56927 = 0;

      public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Direction direction = deserializeCullFace(jsonObject);
         int i = deserializeTintIndex(jsonObject);
         String string = deserializeTexture(jsonObject);
         ModelElementFace.UV uV = getUV(jsonObject);
         AxisRotation axisRotation = getRotation(jsonObject);
         return new ModelElementFace(direction, i, string, uV, axisRotation);
      }

      private static int deserializeTintIndex(JsonObject json) {
         return JsonHelper.getInt(json, "tintindex", -1);
      }

      private static String deserializeTexture(JsonObject json) {
         return JsonHelper.getString(json, "texture");
      }

      private static @Nullable Direction deserializeCullFace(JsonObject json) {
         String string = JsonHelper.getString(json, "cullface", "");
         return Direction.byId(string);
      }

      private static AxisRotation getRotation(JsonObject json) {
         int i = JsonHelper.getInt(json, "rotation", 0);
         return AxisRotation.fromDegrees(i);
      }

      private static ModelElementFace.@Nullable UV getUV(JsonObject json) {
         if (!json.has("uv")) {
            return null;
         }

         JsonArray jsonArray = JsonHelper.getArray(json, "uv");
         if (jsonArray.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
         }

         float f = JsonHelper.asFloat(jsonArray.get(0), "minU");
         float g = JsonHelper.asFloat(jsonArray.get(1), "minV");
         float h = JsonHelper.asFloat(jsonArray.get(2), "maxU");
         float i = JsonHelper.asFloat(jsonArray.get(3), "maxV");
         return new ModelElementFace.UV(f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public record UV(float minU, float minV, float maxU, float maxV) {
      public float getUVertices(int i) {
         return i != 0 && i != 1 ? this.maxU : this.minU;
      }

      public float getVVertices(int i) {
         return i != 0 && i != 3 ? this.maxV : this.minV;
      }
   }
}
