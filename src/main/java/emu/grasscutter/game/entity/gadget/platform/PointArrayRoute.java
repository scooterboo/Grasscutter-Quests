package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.scripts.data.SceneGadget;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import messages.scene.entity.MovingPlatformType;
import messages.scene.entity.PlatformInfo;

/**
 * TODO implement point array routes, read from missing resources
 */
public class PointArrayRoute extends BaseRoute {

    @Getter @Setter int currentPoint;
    @Getter @Setter int pointArrayId;

    public PointArrayRoute(SceneGadget gadget) {
        super(gadget);
    }

    public PointArrayRoute(Position startRot, boolean startRoute, boolean isActive, int pointArrayId) {
        super(startRot, startRoute, isActive);
        this.pointArrayId = pointArrayId;
    }

    @Override
    public PlatformInfo toProto() {
        val proto = super.toProto();
        proto.setMovingPlatformType(MovingPlatformType.MOVING_PLATFORM_ROUTE);
        return proto;
    }
}
