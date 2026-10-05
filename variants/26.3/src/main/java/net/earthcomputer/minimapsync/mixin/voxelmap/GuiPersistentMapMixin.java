package net.earthcomputer.minimapsync.mixin.voxelmap;

import com.mamiyaotaru.voxelmap.gui.overridden.Popup;
import com.mamiyaotaru.voxelmap.persistent.GuiPersistentMap;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.earthcomputer.minimapsync.client.MinimapSyncClient;
import net.earthcomputer.minimapsync.client.VoxelMapCompat;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiPersistentMap.class, remap = false)
public class GuiPersistentMapMixin {
    @Shadow Waypoint selectedWaypoint;

    @Inject(method = "popupAction", at = @At("HEAD"), cancellable = true)
    private void onPopupAction(Popup popup, int action, CallbackInfo ci) {
        if (MinimapSyncClient.isCompatibleServer() && action == 3 && selectedWaypoint != null) { // teleport
            if (VoxelMapCompat.INSTANCE.teleport(selectedWaypoint)) {
                Minecraft.getInstance().setScreenAndShow(null);
                ci.cancel();
            }
        }
    }
}
