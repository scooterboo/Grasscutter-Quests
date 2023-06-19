package emu.grasscutter.net.packet;

import com.google.protobuf.GeneratedMessageV3;
import emu.grasscutter.server.game.GameSession;
import interfaces.ProtoModel;
import messages.VERSION;

public abstract class BaseTypedPackage<Package extends ProtoModel> extends BasePacket {

    protected Package proto;
    protected BaseTypedPackage(Package proto) {
        super();
        this.proto = proto;
    }
    protected BaseTypedPackage(Package proto, int clientSequence) {
        super();
        this.proto = proto;
        this.buildHeader(clientSequence);
    }

    @Override
    public byte[] getData(VERSION version) {
        return proto.encodeToByteArray(version);
    }

    @Override
    public int getOpcode(GameSession session) {
        return session.getPackageIdProvider().getPackageId(proto.getClass().getSimpleName());
    }

    @Override @Deprecated
    public void setData(byte[] data) {
        throw new UnsupportedOperationException("Not supported, why are you doing this.");
    }
    @Deprecated
    @Override
    public void setData(GeneratedMessageV3 proto) {
        setData((byte[])null);
    }

    @Override @Deprecated
    public void setData(GeneratedMessageV3.Builder proto) {
        setData((byte[])null);
    }
}
