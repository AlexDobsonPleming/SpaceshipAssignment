package tooling;

import com.jogamp.opengl.GL3;
import gmaths.Vec3;

import java.util.function.Supplier;

public class SpotLight extends Light implements ISpotLight {
    private Supplier<Vec3> directionGetter;
    private float cutoff;
    private float outerCutoff;
    private float constant;
    private float linear;
    private float quadratic;

    static final Vec3 defaultAmbient = new Vec3(0.5f, 0.5f, 0.5f);
    static final Vec3 defaultDiffuse = new Vec3(2f, 2f, 2f);
    static final Vec3 defaultSpecular = new Vec3(5f, 5f, 5f);

    public SpotLight(
            GL3 gl,
            Supplier<Vec3> positionGetter,
            Supplier<Vec3> directionGetter,
            float cutoff,
            float outerCutoff,
            float constant,
            float linear,
            float quadratic
            ) {
        super(gl, positionGetter, defaultAmbient, defaultDiffuse, defaultSpecular);

        this.directionGetter = directionGetter;
        this.cutoff = cutoff;
        this.outerCutoff = outerCutoff;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public SpotLight(
            GL3 gl,
            Supplier<Vec3> positionGetter,
            Supplier<Vec3> directionGetter
    ) {
        this(
                gl,
                positionGetter,
                directionGetter,
                15.0f, //cutoff
                25.0f, //outer cutoff
                1.0f, //constant attenuation
                0.09f, // lineat attenuation thingy
                0.032f // quadratic attenuation
        );
    }

    @Override
    public Vec3 getDirection() {
        return directionGetter.get();
    }

    @Override
    public float getCutoff() {
        return cutoff;
    }

    @Override
    public float getOuterCutoff() {
        return outerCutoff;
    }

    @Override
    public float getConstant() {
        return constant;
    }

    @Override
    public float getLinear() {
        return linear;
    }

    @Override
    public float getQuadratic() {
        return quadratic;
    }
}