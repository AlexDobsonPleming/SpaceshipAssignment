package models;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import models.meshes.Cube;
import models.meshes.Sphere;
import tooling.*;

/**
 * This class handles creating Model objects from meshes
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work

 */
public class Shapes {
    private ILight[] lights;
    private Camera camera;

    public Shapes(Camera c, ILight[] l) {
        camera = c;
        lights = l;
    }

    public Model makeSphere(GL3 gl, Texture t1, Texture t2) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, new Sphere());
        Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_m_2t.glsl");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        Model sphere = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1, t2);
        return sphere;
    }

    public Model makeCube(GL3 gl, Texture t1, Texture t2) {
        String name= "cube";
        Mesh mesh = new Mesh(gl, new Cube());
        Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_m_2t.glsl");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        Model cube = new Model(name, mesh, modelMatrix, shader, material, lights, camera, t1, t2);
        return cube;
    }
}
