package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.GetAllMailRspOuterClass.GetAllMailRsp;
import lombok.val;

import java.time.Instant;
import java.util.List;

public class PacketGetAllMailRsp extends BasePacket {

    public PacketGetAllMailRsp(Player player, boolean isGiftMail) {
        super(PacketOpcodes.GetAllMailRsp);

        val packet = GetAllMailRsp.newBuilder()
            .setIsCollected(isGiftMail);

        val inbox = player.getAllMail();
        if (!isGiftMail && inbox.size() > 0) {
            packet.addAllMailList(inbox.stream()
                .filter(mail -> mail.stateValue == 1)
                .filter(mail -> mail.expireTime > Instant.now().getEpochSecond())
                .map(mail -> mail.toProto(player)).toList());
        } else {
            // Empty mailbox.
            // TODO: Implement the gift mailbox.
            packet.addAllMailList(List.of());
        }
        // When enabled this will send a notification to the user telling them their inbox is full, and they should delete old messages when opening the mailbox.
        packet.setIsTruncated(inbox.size() > 1000);
        this.setData(packet);
    }
}
