package net.earthcomputer.minimapsync.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import net.earthcomputer.minimapsync.ducks.IConfigureMinimapSyncPlayer;
import net.minecraft.network.Connection;
import net.minecraft.server.network.CommonListenerCookie;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.earthcomputer.minimapsync.ducks.IHasProtocolVersion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    private void configurePlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        if (connection.getPacketListener() instanceof IConfigureMinimapSyncPlayer configuration) {
            configuration.minimapsync_configurePlayer(player);
        }
    }

    @ModifyArg(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/UnboundProtocol;bind(Ljava/util/function/Function;Ljava/lang/Object;)Lnet/minecraft/network/ProtocolInfo;"), index = 0)
    private Function<ByteBuf, RegistryFriendlyByteBuf> addProtocolToBufDecorator(Function<ByteBuf, RegistryFriendlyByteBuf> decorator, @Local(argsOnly = true) ServerPlayer player) {
        int protocol = ((IHasProtocolVersion) player).minimapsync_getProtocolVersion();
        return decorator.andThen(buf -> {
            ((IHasProtocolVersion) buf).minimapsync_setProtocolVersion(protocol);
            return buf;
        });
    }
}
