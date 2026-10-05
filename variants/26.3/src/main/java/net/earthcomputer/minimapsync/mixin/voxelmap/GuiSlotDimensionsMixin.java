package net.earthcomputer.minimapsync.mixin.voxelmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "com.mamiyaotaru.voxelmap.gui.GuiListDimensions", remap = false)
public class GuiSlotDimensionsMixin {
    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;<init>(Lnet/minecraft/client/Minecraft;IIII)V"))
    private static void makeRoomForPrivateCheckbox(Args args) {
        args.set(2, args.<Integer>get(2) - 10);
        args.set(3, args.<Integer>get(3) - 10);
    }
}
