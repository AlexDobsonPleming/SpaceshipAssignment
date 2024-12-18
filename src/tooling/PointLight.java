package tooling;

import com.jogamp.opengl.GL3;
import gmaths.Vec3;

import java.util.function.Supplier;

/**
 * This class encapsulates the point light's implementation
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */
public class PointLight extends Light {

    public PointLight(GL3 gl, Supplier<Vec3> posGetter) {
        super(gl, posGetter,
                new Vec3(0.3f, 0.3f, 0.3f),
                new Vec3(0.8f, 0.8f, 0.8f),
                new Vec3(0.8f, 0.8f, 0.8f));
    }

}
