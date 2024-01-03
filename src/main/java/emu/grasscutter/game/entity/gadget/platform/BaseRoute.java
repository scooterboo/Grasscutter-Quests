package emu.grasscutter.game.entity.gadget.platform;

import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.SceneGadget;
import emu.grasscutter.utils.Position;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import messages.general.MathQuaternion;
import messages.scene.entity.PlatformInfo;

public abstract class BaseRoute {
    @Getter @Setter private boolean isStarted;
    @Getter @Setter private boolean isActive;
    @Getter @Setter private Position startRot;
    @Getter @Setter private int startSceneTime;
    @Getter @Setter private int stopSceneTime;

    BaseRoute(Position startRot, boolean isStarted, boolean isActive) {
        this.startRot = startRot;
        this.isStarted = isStarted;
        this.isActive = isActive;
    }

    BaseRoute(SceneGadget gadget) {
        this.startRot = new Position(gadget.getRot());
        this.isStarted = gadget.isStart_route();
        this.isActive = gadget.isStart_route();
    }

    public static BaseRoute fromSceneGadget(SceneGadget sceneGadget) {
        if (sceneGadget.getRoute_id() != 0) {
            return new ConfigRoute(sceneGadget);
        } else if (sceneGadget.is_use_point_array()) {
            return new PointArrayRoute(sceneGadget);
        }
        return null;
    }

    public boolean startRoute(Scene scene) {
        if (this.isStarted) {
            return false;
        }
        this.isStarted = true;
        this.isActive = true;
        this.startSceneTime = scene.getSceneTime()+300;

        return true;
    }

    public boolean stopRoute(Scene scene) {
        if (!this.isStarted) {
            return false;
        }
        this.isStarted = false;
        this.isActive = false;
        this.startSceneTime = scene.getSceneTime();
        this.stopSceneTime = scene.getSceneTime();

        return true;
    }

    private MathQuaternion rotAsMathQuaternion() {
        val result = new MathQuaternion();
        if (startRot != null) {
            result.setX(startRot.getX());
            result.setY(startRot.getY());
            result.setZ(startRot.getZ());
        }
        return result;
    }

    public PlatformInfo toProto() {
        val result = new PlatformInfo();
        result.setStarted(isStarted);
        result.setActive(isActive);
        result.setStartRot(rotAsMathQuaternion());
        result.setStartSceneTime(startSceneTime);
        if (!isStarted) {
            result.setStopSceneTime(stopSceneTime);
        }
        return result;
    }
}
