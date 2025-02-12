package models;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import models.meshes.TwoTriangles;
import tooling.*;

/**
 * This class renders a surface
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class RepeatingSurface {

  private Camera camera;
  private ILight[] lights;

  private Model surface;


  public RepeatingSurface(GL3 gl, float xSize, float zSize, Camera cameraIn, ILight[] lights, Texture texture1, Mat4 translateIn) {
    camera = cameraIn;
    this.lights = lights;

    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone(), 4.0f, 3.0f);
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_m_2t.glsl");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 scale = Mat4Transform.scale(xSize,1f,zSize);
    Mat4 modelMatrix = Mat4.multiply(translateIn, scale);
    surface = new Model(name, mesh, modelMatrix, shader, material, lights, camera, texture1);
  }

  public void setSpecularMap(Texture texture2) {
    surface.setSpecular(texture2);
  }

  public void render(GL3 gl) {
    surface.render(gl);
  }

  public void dispose(GL3 gl) { surface.dispose(gl); }

}