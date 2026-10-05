package net.earthcomputer.minimapsync.mixin.xaerominimap;
import net.earthcomputer.minimapsync.client.XaerosMapCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.lib.client.graphics.XaeroBufferProvider;
import com.mojang.blaze3d.vertex.VertexConsumer;
@Mixin(value=WaypointMapRenderer.class, remap=false)
public class WaypointsGuiRendererMixin {
 @Inject(method="drawIconGUI(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lxaero/common/minimap/waypoints/Waypoint;III)V",at=@At("HEAD"),cancellable=true)
 private void customGui(GuiGraphicsExtractor graphics,Waypoint waypoint,int x,int y,int opacity,CallbackInfo ci){
  var texture=XaerosMapCompat.customIconTexture(waypoint.getSymbol());
  if(texture!=null){graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,texture,x-4,y-4,0,0,9,9,9,9,((Math.clamp(opacity,0,100)*255/100)<<24)|0xffffff);ci.cancel();}
 }
 @Inject(method="drawIcon(Lxaero/hud/minimap/element/render/MinimapElementGraphics;Lxaero/common/minimap/render/MinimapRendererHelper;Lxaero/common/minimap/waypoints/Waypoint;IIILxaero/lib/client/graphics/XaeroBufferProvider;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V",at=@At("HEAD"),cancellable=true)
 private void customMap(MinimapElementGraphics graphics,MinimapRendererHelper helper,Waypoint waypoint,int x,int y,int opacity,XaeroBufferProvider buffers,VertexConsumer background,VertexConsumer texture,CallbackInfo ci){
  if(XaerosMapCompat.customIconTexture(waypoint.getSymbol())!=null){XaerosMapCompat.drawCustomIcon(graphics.pose(),waypoint.getSymbol(),x-4,y-4,9,9,1,1,1,opacity/100f);ci.cancel();}
 }
}
