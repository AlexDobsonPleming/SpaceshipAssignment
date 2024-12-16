package tooling;

import gmaths.Vec3;

public interface ILight {
    Vec3 getPosition();
    Vec3 getAmbient();
    Vec3 getDiffuse();
    Vec3 getSpecular();

    void enable();
    void disable();
    boolean isEnabled();
}
