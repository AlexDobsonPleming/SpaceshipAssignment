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

public class Surface {

  private Camera camera;
  private ILight[] lights;

  private Model surface;


  private static Material defaultMaterial = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
  public Surface(GL3 gl, float xSize, float zSize, Camera cameraIn, ILight[] lights, Texture texture1, Mat4 translateIn) {
    this(gl, xSize, zSize, cameraIn, lights, texture1, null, translateIn, defaultMaterial);
  }

  public Surface(GL3 gl, float xSize, float zSize, Camera cameraIn, ILight[] lights, Texture texture1, Texture specular, Mat4 translateIn, Material material) {
    camera = cameraIn;
    this.lights = lights;

    String name = "floor";
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_m_2t.glsl");
    Mat4 scale = Mat4Transform.scale(xSize,1f,zSize);
    Mat4 modelMatrix = Mat4.multiply(translateIn, scale);
    surface = new Model(name, mesh, modelMatrix, shader, material, lights, camera, texture1, specular);
  }


  public void render(GL3 gl) {
    surface.render(gl);
  }

  public void dispose(GL3 gl) { surface.dispose(gl); }

}