package net.earthcomputer.minimapsync.mixin.xaerominimap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.earthcomputer.minimapsync.client.XaerosMapCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderer;

// Xaero 25.2.x moved the icon drawing from xaero.common...WaypointsGuiRenderer (now an empty
// subclass) up to xaero.hud...WaypointMapRenderer, and the coloured rect to RenderBufferUtil.
@Mixin(value = WaypointMapRenderer.class, remap = false)
public class WaypointsGuiRendererMixin {
    // Full descriptor of the overload that does the drawing, in runtime (intermediary) names: the
    // mixin is remap = false, so nothing translates it. A bare name never matched at runtime.
    private static final String DRAW_ICON = "drawIconOnGUI(Lnet/minecraft/class_332;Lxaero/common/minimap/render/MinimapRendererHelper;Lxaero/common/minimap/waypoints/Waypoint;IIILnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4588;Lnet/minecraft/class_4588;)V";

    @ModifyExpressionValue(method = DRAW_ICON, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I", remap = true))
    private int modifyFontWidthForCustomIcons(int original, GuiGraphics guiGraphics, MinimapRendererHelper rendererHelper, Waypoint w) {
        if (w.getSymbol().startsWith("minimapsync_")) {
            return 7;
        } else {
            return original;
        }
    }

    @WrapOperation(method = DRAW_ICON, at = @At(value = "INVOKE", target = "Lxaero/hud/render/util/RenderBufferUtil;addColoredRect(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFIIFFFF)V", remap = true))
    private void drawCustomIcon(Matrix4f matrix, VertexConsumer vertexBuffer, float x, float y, int w, int h, float r, float g, float b, float a, Operation<Void> original, GuiGraphics guiGraphics, MinimapRendererHelper rendererHelper, Waypoint waypoint) {
        if (waypoint.getSymbol().startsWith("minimapsync_")) {
            XaerosMapCompat.drawCustomIcon(guiGraphics.pose(), waypoint.getSymbol(), x, y, w, h, r, g, b, a);
        } else {
            original.call(matrix, vertexBuffer, x, y, w, h, r, g, b, a);
        }
    }

    @WrapOperation(method = DRAW_ICON, at = @At(value = "INVOKE", target = "Lxaero/common/misc/Misc;drawNormalText(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFIZLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", remap = true))
    private void dontDrawSymbolStringForCustomIcons(PoseStack matrices, String symbol, float x, float y, int color, boolean shadow, MultiBufferSource.BufferSource renderTypeBuffer, Operation<Void> original) {
        if (!symbol.startsWith("minimapsync_")) {
            original.call(matrices, symbol, x, y, color, shadow, renderTypeBuffer);
        }
    }
}
