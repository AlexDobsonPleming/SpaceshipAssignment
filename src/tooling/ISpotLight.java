package tooling;

import gmaths.Vec3;

public interface ISpotLight extends ILight {
    Vec3 getDirection();
    float getCutoff();
    float getOuterCutoff();
    float getConstant();
    float getLinear();
    float getQuadratic();
}
