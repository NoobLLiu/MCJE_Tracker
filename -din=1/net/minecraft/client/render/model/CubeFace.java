package net.minecraft.client.render.model;

import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public enum CubeFace {
   DOWN(
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z)
   ),
   UP(
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z)
   ),
   NORTH(
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z)
   ),
   SOUTH(
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z)
   ),
   WEST(
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MIN_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z)
   ),
   EAST(
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MAX_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MIN_Y, CubeFace.CornerCoord.MIN_Z),
      new CubeFace.Corner(CubeFace.CornerCoord.MAX_X, CubeFace.CornerCoord.MAX_Y, CubeFace.CornerCoord.MIN_Z)
   );

   private static final Map<Direction, CubeFace> DIRECTION_LOOKUP = (Map<Direction, CubeFace>)Util.make(new EnumMap(Direction.class), map -> {
      map.put(Direction.DOWN, DOWN);
      map.put(Direction.UP, UP);
      map.put(Direction.NORTH, NORTH);
      map.put(Direction.SOUTH, SOUTH);
      map.put(Direction.WEST, WEST);
      map.put(Direction.EAST, EAST);
   });
   private final CubeFace.Corner[] corners;

   public static CubeFace getFace(Direction direction) {
      return DIRECTION_LOOKUP.get(direction);
   }

   CubeFace(final CubeFace.Corner... corners) {
      this.corners = corners;
   }

   public CubeFace.Corner getCorner(int corner) {
      return this.corners[corner];
   }

   @Environment(EnvType.CLIENT)
   public record Corner(CubeFace.CornerCoord xSide, CubeFace.CornerCoord ySide, CubeFace.CornerCoord zSide) {
      public Vector3f get(Vector3fc from, Vector3fc to) {
         return new Vector3f(this.xSide.get(from, to), this.ySide.get(from, to), this.zSide.get(from, to));
      }
   }

   @Environment(EnvType.CLIENT)
   public enum CornerCoord {
      MIN_X,
      MIN_Y,
      MIN_Z,
      MAX_X,
      MAX_Y,
      MAX_Z;

      public float get(Vector3fc from, Vector3fc to) {
         return switch (this) {
            case MIN_X -> from.x();
            case MIN_Y -> from.y();
            case MIN_Z -> from.z();
            case MAX_X -> to.x();
            case MAX_Y -> to.y();
            case MAX_Z -> to.z();
         };
      }

      public float get(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
         return switch (this) {
            case MIN_X -> fromX;
            case MIN_Y -> fromY;
            case MIN_Z -> fromZ;
            case MAX_X -> toX;
            case MAX_Y -> toY;
            case MAX_Z -> toZ;
         };
      }
   }
}
