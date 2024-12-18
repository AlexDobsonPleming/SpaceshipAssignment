package tooling;

import gmaths.Vec3;

/**
 * Interface for spotlight functionality
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public interface ISpotLight extends ILight {
    Vec3 getDirection();
    float getCutoff();
    float getOuterCutoff();
    float getConstant();
    float getLinear();
    float getQuadratic();
}
