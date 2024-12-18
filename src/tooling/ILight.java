package tooling;

import gmaths.Vec3;

/**
 * Interface for light functionality
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public interface ILight {
    Vec3 getPosition();
    Vec3 getAmbient();
    Vec3 getDiffuse();
    Vec3 getSpecular();

    void enable();
    void disable();
    boolean isEnabled();
}
