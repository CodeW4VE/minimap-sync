package net.earthcomputer.minimapsync.mixin.xaerominimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderContext;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderReader;

@Mixin(value = WaypointMapRenderReader.class, remap = false)
public class WaypointReaderMixin {
    @Inject(method = "getRenderBoxRight(Lxaero/common/minimap/waypoints/Waypoint;Lxaero/hud/minimap/waypoint/render/WaypointMapRenderContext;F)I", at = @At("HEAD"), cancellable = true)
    private void modifyRenderBoxForCustomIcon(Waypoint element, WaypointMapRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        if (element.getSymbol().startsWith("minimapsync_")) {
            cir.setReturnValue(5);
        }
    }
}
