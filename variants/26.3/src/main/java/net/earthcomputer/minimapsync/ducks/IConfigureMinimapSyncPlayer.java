package net.earthcomputer.minimapsync.ducks;

import net.minecraft.server.level.ServerPlayer;

public interface IConfigureMinimapSyncPlayer {
    void minimapsync_configurePlayer(ServerPlayer player);
}
