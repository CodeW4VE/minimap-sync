package net.earthcomputer.minimapsync.mixin.xaerominimap;
import net.earthcomputer.minimapsync.client.XaerosMapCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderer;
@Mixin(value=WaypointWorldRenderer.class,remap=false)
public class WaypointsIngameRendererMixin {
 @Inject(method="renderIcon",at=@At("HEAD"),cancellable=true)
 private void customWorld(Waypoint waypoint,boolean highlighted,PoseStack pose,Font font,XaeroBufferProvider buffers,CallbackInfo ci){
  if(XaerosMapCompat.customIconTexture(waypoint.getSymbol())!=null){XaerosMapCompat.drawCustomIcon(pose,waypoint.getSymbol(),-5,-9,9,9,1,1,1,1);ci.cancel();}
 }
}
