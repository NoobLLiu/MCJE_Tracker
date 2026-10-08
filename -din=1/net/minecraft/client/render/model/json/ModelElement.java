package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ModelElement(
   Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces, @Nullable ModelElementRotation rotation, boolean shade, int lightEmission
) {
   private static final boolean field_32785 = false;
   private static final float field_32786 = -16.0F;
   private static final float field_32787 = 32.0F;

   public ModelElement(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces) {
      this(from, to, faces, null, true, 0);
   }

   @Environment(EnvType.CLIENT)
   protected static class Deserializer implements JsonDeserializer<ModelElement> {
      private static final boolean DEFAULT_SHADE = true;
      private static final int field_53160 = 0;
      private static final String SHADE_KEY = "shade";
      private static final String LIGHT_EMISSION_KEY = "light_emission";
      private static final String ROTATION_KEY = "rotation";
      private static final String ORIGIN_KEY = "origin";
      private static final String ANGLE_KEY = "angle";
      private static final String X_KEY = "x";
      private static final String Y_KEY = "y";
      private static final String Z_KEY = "z";
      private static final String AXIS_KEY = "axis";
      private static final String RESCALE_KEY = "rescale";
      private static final String FACES_KEY = "faces";
      private static final String TO_KEY = "to";
      private static final String FROM_KEY = "from";

      public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Vector3f vector3f = deserializeFromTo(jsonObject, "from");
         Vector3f vector3f2 = deserializeFromTo(jsonObject, "to");
         ModelElementRotation modelElementRotation = this.deserializeRotation(jsonObject);
         Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
         if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
            throw new JsonParseException("Expected 'shade' to be a Boolean");
         }

         boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
         int i = 0;
         if (jsonObject.has("light_emission")) {
            boolean bl2 = JsonHelper.hasNumber(jsonObject, "light_emission");
            if (bl2) {
               i = JsonHelper.getInt(jsonObject, "light_emission");
            }

            if (!bl2 || i < 0 || i > 15) {
               throw new JsonParseException("Expected 'light_emission' to be an Integer between (inclusive) 0 and 15");
            }
         }

         return new ModelElement(vector3f, vector3f2, map, modelElementRotation, bl, i);
      }

      private @Nullable ModelElementRotation deserializeRotation(JsonObject object) {
         if (!object.has("rotation")) {
            return null;
         }

         JsonObject jsonObject = JsonHelper.getObject(object, "rotation");
         Vector3f vector3f = deserializeVec3f(jsonObject, "origin");
         vector3f.mul(0.0625F);
         ModelElementRotation.RotationValue rotationValue;
         if (!jsonObject.has("axis") && !jsonObject.has("angle")) {
            if (!jsonObject.has("x") && !jsonObject.has("y") && !jsonObject.has("z")) {
               throw new JsonParseException("Missing rotation value, expected either 'axis' and 'angle' or 'x', 'y' and 'z'");
            }

            float g = JsonHelper.getFloat(jsonObject, "x", 0.0F);
            float f = JsonHelper.getFloat(jsonObject, "y", 0.0F);
            float h = JsonHelper.getFloat(jsonObject, "z", 0.0F);
            rotationValue = new ModelElementRotation.OfEuler(g, f, h);
         } else {
            Axis axis = this.deserializeAxis(jsonObject);
            float f = JsonHelper.getFloat(jsonObject, "angle");
            rotationValue = new ModelElementRotation.OfAxisAngle(axis, f);
         }

         boolean bl = JsonHelper.getBoolean(jsonObject, "rescale", false);
         return new ModelElementRotation(vector3f, rotationValue, bl);
      }

      private Axis deserializeAxis(JsonObject object) {
         String string = JsonHelper.getString(object, "axis");
         Axis axis = Axis.fromId(string.toLowerCase(Locale.ROOT));
         if (axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + string);
         } else {
            return axis;
         }
      }

      private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
         Map<Direction, ModelElementFace> map = this.deserializeFaces(context, object);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext context, JsonObject object) {
         Map<Direction, ModelElementFace> map = Maps.newEnumMap(Direction.class);
         JsonObject jsonObject = JsonHelper.getObject(object, "faces");

         for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Direction direction = this.getDirection(entry.getKey());
            map.put(direction, (ModelElementFace)context.deserialize(entry.getValue(), ModelElementFace.class));
         }

         return map;
      }

      private Direction getDirection(String name) {
         Direction direction = Direction.byId(name);
         if (direction == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return direction;
         }
      }

      private static Vector3f deserializeFromTo(JsonObject json, String key) {
         Vector3f vector3f = deserializeVec3f(json, key);
         if (!(vector3f.x() < -16.0F)
            && !(vector3f.y() < -16.0F)
            && !(vector3f.z() < -16.0F)
            && !(vector3f.x() > 32.0F)
            && !(vector3f.y() > 32.0F)
            && !(vector3f.z() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'" + key + "' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private static Vector3f deserializeVec3f(JsonObject json, String key) {
         JsonArray jsonArray = JsonHelper.getArray(json, key);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
         }

         float[] fs = new float[3];

         for (int i = 0; i < fs.length; i++) {
            fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
         }

         return new Vector3f(fs[0], fs[1], fs[2]);
      }
   }
}
