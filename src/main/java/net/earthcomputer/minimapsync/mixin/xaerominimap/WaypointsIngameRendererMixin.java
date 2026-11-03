package net.earthcomputer.minimapsync.mixin.xaerominimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.earthcomputer.minimapsync.client.XaerosMapCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderer;

// Xaero 25.2.x moved the in-world icon drawing from WaypointsIngameRenderer#drawIconInWorld to
// WaypointWorldRenderer#renderIcon, its new parent class.
@Mixin(value = WaypointWorldRenderer.class, remap = false)
public class WaypointsIngameRendererMixin {
    // Runtime (intermediary) descriptor: the mixin is remap = false, and a bare name never matched.
    private static final String RENDER_ICON = "renderIcon(Lxaero/common/minimap/waypoints/Waypoint;ZLnet/minecraft/class_4587;Lnet/minecraft/class_327;Lnet/minecraft/class_4597$class_4598;)V";

    @ModifyExpressionValue(method = RENDER_ICON, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I", remap = true))
    private int modifyFontWidthForCustomIcons(int original, Waypoint w) {
        if (w.getSymbol().startsWith("minimapsync_")) {
            return 7;
        } else {
            return original;
        }
    }

    @WrapOperation(
        method = RENDER_ICON,
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I", remap = true)),
        at = @At(value = "INVOKE", target = "Lxaero/hud/minimap/waypoint/render/world/WaypointWorldRenderer;renderColorBackground(Lcom/mojang/blaze3d/vertex/PoseStack;IFFFFLcom/mojang/blaze3d/vertex/VertexConsumer;)V", ordinal = 0, remap = true)
    )
    private void drawCustomIcon(WaypointWorldRenderer instance, PoseStack matrixStack, int addedFrame, float r, float g, float b, float a, VertexConsumer waypointBackgroundConsumer, Operation<Void> original, Waypoint w) {
        if (w.getSymbol().startsWith("minimapsync_")) {
            XaerosMapCompat.drawCustomIcon(matrixStack, w.getSymbol(), -5 - addedFrame, -9, 9, 9, r, g, b, a);
        } else {
            original.call(instance, matrixStack, addedFrame, r, g, b, a, waypointBackgroundConsumer);
        }
    }

    @WrapOperation(method = RENDER_ICON, at = @At(value = "INVOKE", target = "Lxaero/common/misc/Misc;drawNormalText(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFIZLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", remap = true))
    private void dontDrawSymbolStringForCustomIcons(PoseStack matrices, String symbol, float x, float y, int color, boolean shadow, MultiBufferSource.BufferSource renderTypeBuffer, Operation<Void> original) {
        if (!symbol.startsWith("minimapsync_")) {
            original.call(matrices, symbol, x, y, color, shadow, renderTypeBuffer);
        }
    }
}
