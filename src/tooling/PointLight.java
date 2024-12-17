package tooling;

import com.jogamp.opengl.GL3;
import gmaths.Vec3;

import java.util.function.Supplier;

public class PointLight extends Light {

    public PointLight(GL3 gl, Supplier<Vec3> posGetter) {
        super(gl, posGetter,
                new Vec3(0.3f, 0.3f, 0.3f),
                new Vec3(0.8f, 0.8f, 0.8f),
                new Vec3(0.8f, 0.8f, 0.8f));
    }

}
