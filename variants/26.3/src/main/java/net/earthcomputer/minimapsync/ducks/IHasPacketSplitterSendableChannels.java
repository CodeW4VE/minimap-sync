package net.earthcomputer.minimapsync.ducks;

import net.minecraft.resources.Identifier;

import java.util.Set;

public interface IHasPacketSplitterSendableChannels {
    void minimapsync_setPacketSplitterSendableChannels(Set<Identifier> packetSplitterSendableChannels);
}
