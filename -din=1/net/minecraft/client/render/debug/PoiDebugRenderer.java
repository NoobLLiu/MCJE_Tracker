package net.minecraft.client.render.debug;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.PoiDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(EnvType.CLIENT)
public class PoiDebugRenderer implements DebugRenderer.Renderer {
   private static final int field_62976 = 30;
   private static final float field_62977 = 0.32F;
   private static final int ORANGE_COLOR = -23296;
   private final BrainDebugRenderer brainDebugRenderer;

   public PoiDebugRenderer(BrainDebugRenderer brainDebugRenderer) {
      this.brainDebugRenderer = brainDebugRenderer;
   }

   @Override
   public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
      BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
      store.forEachBlockData(DebugSubscriptionTypes.POIS, (pos, data) -> {
         if (blockPos.isWithinDistance(pos, 30.0)) {
            accentuatePoi(pos);
            this.drawPoiInfo(data, store);
         }
      });
      this.brainDebugRenderer.getGhostPointsOfInterest(store).forEach((pos, ghostPois) -> {
         if (store.getBlockData(DebugSubscriptionTypes.POIS, pos) == null) {
            if (blockPos.isWithinDistance(pos, 30.0)) {
               this.drawGhostPoi(pos, (List<String>)ghostPois);
            }
         }
      });
   }

   private static void accentuatePoi(BlockPos pos) {
      float f = 0.05F;
      GizmoDrawing.box(pos, 0.05F, DrawStyle.filled(ColorHelper.fromFloats(0.3F, 0.2F, 0.2F, 1.0F)));
   }

   private void drawGhostPoi(BlockPos pos, List<String> ghostPois) {
      float f = 0.05F;
      GizmoDrawing.box(pos, 0.05F, DrawStyle.filled(ColorHelper.fromFloats(0.3F, 0.2F, 0.2F, 1.0F)));
      GizmoDrawing.blockLabel(ghostPois.toString(), pos, 0, -256, 0.32F);
      GizmoDrawing.blockLabel("Ghost POI", pos, 1, -65536, 0.32F);
   }

   private void drawPoiInfo(PoiDebugData data, DebugDataStore store) {
      int i = 0;
      if (SharedConstants.BRAIN) {
         List<String> list = this.getTicketHolders(data, false, store);
         if (list.size() < 4) {
            drawTextOverPoi("Owners: " + list, data, i, -256);
         } else {
            drawTextOverPoi(list.size() + " ticket holders", data, i, -256);
         }

         i++;
         List<String> list2 = this.getTicketHolders(data, true, store);
         if (list2.size() < 4) {
            drawTextOverPoi("Candidates: " + list2, data, i, -23296);
         } else {
            drawTextOverPoi(list2.size() + " potential owners", data, i, -23296);
         }

         i++;
      }

      drawTextOverPoi("Free tickets: " + data.freeTicketCount(), data, i, -256);
      drawTextOverPoi(data.poiType().getIdAsString(), data, ++i, -1);
   }

   private static void drawTextOverPoi(String string, PoiDebugData data, int yOffset, int color) {
      GizmoDrawing.blockLabel(string, data.pos(), yOffset, color, 0.32F);
   }

   private List<String> getTicketHolders(PoiDebugData poiData, boolean potential, DebugDataStore store) {
      List<String> list = new ArrayList<>();
      store.forEachEntityData(DebugSubscriptionTypes.BRAINS, (entity, grainData) -> {
         boolean bl2 = potential ? grainData.potentialPoiContains(poiData.pos()) : grainData.poiContains(poiData.pos());
         if (bl2) {
            list.add(NameGenerator.name(entity.getUuid()));
         }
      });
      return list;
   }
}
