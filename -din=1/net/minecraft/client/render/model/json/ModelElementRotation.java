package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.Direction.Axis;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public record ModelElementRotation(Vector3fc origin, ModelElementRotation.RotationValue value, boolean rescale, Matrix4fc transform) {
   public ModelElementRotation(Vector3fc origin, ModelElementRotation.RotationValue value, boolean rescale) {
      this(origin, value, rescale, transform(value, rescale));
   }

   private static Matrix4f transform(ModelElementRotation.RotationValue value, boolean rescale) {
      Matrix4f matrix4f = value.getMatrix();
      if (rescale && !MatrixUtil.isIdentity(matrix4f)) {
         Vector3fc vector3fc = scale(matrix4f);
         matrix4f.scale(vector3fc);
      }

      return matrix4f;
   }

   private static Vector3fc scale(Matrix4fc matrix) {
      Vector3f vector3f = new Vector3f();
      float f = scale(matrix, Axis.X, vector3f);
      float g = scale(matrix, Axis.Y, vector3f);
      float h = scale(matrix, Axis.Z, vector3f);
      return vector3f.set(f, g, h);
   }

   private static float scale(Matrix4fc matrix, Axis axis, Vector3f vec) {
      Vector3f vector3f = vec.set(axis.getPositiveDirection().getFloatVector());
      Vector3f vector3f2 = matrix.transformDirection(vector3f);
      float f = Math.abs(vector3f2.x);
      float g = Math.abs(vector3f2.y);
      float h = Math.abs(vector3f2.z);
      float i = Math.max(Math.max(f, g), h);
      return 1.0F / i;
   }

   @Environment(EnvType.CLIENT)
   public record OfAxisAngle(Axis axis, float angle) implements ModelElementRotation.RotationValue {
      @Override
      public Matrix4f getMatrix() {
         Matrix4f matrix4f = new Matrix4f();
         if (this.angle == 0.0F) {
            return matrix4f;
         }

         Vector3fc vector3fc = this.axis.getPositiveDirection().getFloatVector();
         matrix4f.rotation(this.angle * (float) (java.lang.Math.PI / 180.0), vector3fc);
         return matrix4f;
      }
   }

   @Environment(EnvType.CLIENT)
   public record OfEuler(float x, float y, float z) implements ModelElementRotation.RotationValue {
      @Override
      public Matrix4f getMatrix() {
         return new Matrix4f()
            .rotationZYX(
               this.z * (float) (java.lang.Math.PI / 180.0), this.y * (float) (java.lang.Math.PI / 180.0), this.x * (float) (java.lang.Math.PI / 180.0)
            );
      }
   }

   @Environment(EnvType.CLIENT)
   public interface RotationValue {
      Matrix4f getMatrix();
   }
}
