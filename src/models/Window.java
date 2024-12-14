package models;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import models.meshes.TwoTriangles;
import tooling.*;

/**
 * This class stores the Floor
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Window {

  private Camera camera;
  private Light light;

  private Model floor;


  public Window(GL3 gl, float xSize, float zSize, Camera cameraIn, Light lightIn, Texture texture1, Mat4 translateIn) {

    camera = cameraIn;
    light = lightIn;

    String name = "window";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_1t.glsl");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 scale = Mat4Transform.scale(xSize,1f,zSize);
    Mat4 modelMatrix = Mat4.multiply(translateIn, scale);
    floor = new Model(name, mesh, modelMatrix, shader, material, light, camera, texture1);

  }

  public void render(GL3 gl) {
    floor.render(gl);
  }

}