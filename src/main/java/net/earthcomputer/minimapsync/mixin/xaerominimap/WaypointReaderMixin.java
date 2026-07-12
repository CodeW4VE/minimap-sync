package net.earthcomputer.minimapsync.mixin.xaerominimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.render.WaypointGuiRenderContext;
import xaero.common.minimap.waypoints.render.WaypointReader;

@Mixin(value = WaypointReader.class, remap = false)
public class WaypointReaderMixin {
    // Xaero 25.2.x removed WaypointReader#getRenderBoxRight (the render-box sizing was
    // refactored into getBoxScale on the xaero.hud superclass). This inject is purely
    // cosmetic (custom-icon hitbox width), so make it optional instead of crashing on
    // newer Xaero. TODO(phase 2): port to the new getBoxScale model.
    @Inject(method = "getRenderBoxRight(Lxaero/common/minimap/waypoints/Waypoint;Lxaero/common/minimap/waypoints/render/WaypointGuiRenderContext;F)I", at = @At("HEAD"), cancellable = true, require = 0)
    private void modifyRenderBoxForCustomIcon(Waypoint element, WaypointGuiRenderContext context, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        if (element.getSymbol().startsWith("minimapsync_")) {
            cir.setReturnValue(5);
        }
    }
}
