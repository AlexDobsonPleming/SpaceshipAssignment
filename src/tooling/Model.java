package tooling;

import gmaths.*;
import java.nio.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
import tooling.*;

/**
 * This class encapuslates rendering a model
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 *
 * It is a combination of lab code and my code
 * I declare that any sections marked as my code are wholly my own work
 */

public class Model {

  //lab code
  private String name;
  private Mesh mesh;
  private Mat4 modelMatrix;
  private Shader shader;
  private Material material;
  private Camera camera;
  private Light[] lights;
  private Texture diffuse;
  private Texture specular;

  //lab code
  public Model() {
    name = null;
    mesh = null;
    modelMatrix = null;
    material = null;
    camera = null;
    lights = null;
    shader = null;
  }

  //lab code
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                             Camera camera, Texture diffuse, Texture specular) {
    this.name = name;
    this.mesh = mesh;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.material = material;
    this.lights = lights;
    this.camera = camera;
    this.diffuse = diffuse;
    this.specular = specular;
  }

  //lab code
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                             Camera camera, Texture diffuse) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
  }

  //lab code
  public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                             Camera camera) {
    this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
  }

  //lab code
  public void setName(String s) {
    this.name = s;
  }

  //lab code
  public void setMesh(Mesh m) {
    this.mesh = m;
  }

  //lab code
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  //lab code
  public void setMaterial(Material material) {
    this.material = material;
  }

  //lab code
  public void setShader(Shader shader) {
    this.shader = shader;
  }

  //lab code
  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  //lab code
  public void setLights(Light[] lights) {
    this.lights = lights;
  }

  //lab code
  public void setDiffuse(Texture t) {
    this.diffuse = t;
  }

  //lab code
  public void setSpecular(Texture t) {
    this.specular = t;
  }

  //lab code
  public void renderName(GL3 gl) {
    System.out.println("Name = " + name);
  }

  //lab code
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  //my code
  public void render(GL3 gl, Mat4 modelMatrix) {
    if (mesh_null()) {
      System.out.println("Error: null in model render");
      return;
    }

    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());


    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setInt(gl,"numLights", lights.length);

    List<Light> enabledLights = Arrays.stream(lights)
            .filter(Light::isEnabled)
            .collect(Collectors.toList());

    PointLight[] pointLights = enabledLights.stream()
            .filter(PointLight.class::isInstance)
            .map(PointLight.class::cast)         // cast to pointlight
            .toArray(PointLight[]::new);

    for (int i=0; i<pointLights.length; i++) {
      shader.setVec3(gl, "lights["+i+"].position", lights[i].getPosition());
      shader.setVec3(gl, "lights["+i+"].ambient", lights[i].getAmbient());
      shader.setVec3(gl, "lights["+i+"].diffuse", lights[i].getDiffuse());
      shader.setVec3(gl, "lights["+i+"].specular", lights[i].getSpecular());
    }

    SpotLight spotlight = enabledLights.stream()
            .filter(SpotLight.class::isInstance)
            .map(SpotLight.class::cast)         // cast to subclass
            .findFirst()
            .orElse(null);
    if (spotlight != null) {
      shader.setVec3(gl, "spotlight.position", spotlight.getPosition());
      shader.setVec3(gl, "spotlight.direction", spotlight.getDirection());
      shader.setVec3(gl, "spotlight.ambient", spotlight.getAmbient());
      shader.setVec3(gl, "spotlight.diffuse", spotlight.getDiffuse());
      shader.setVec3(gl, "spotlight.specular", spotlight.getSpecular());
      shader.setFloat(gl, "spotlight.cutoff", (float) Math.cos(Math.toRadians(spotlight.getCutoff())));
      shader.setFloat(gl, "spotlight.outerCutoff", (float) Math.cos(Math.toRadians(spotlight.getOuterCutoff())));
      shader.setFloat(gl, "spotlight.constant", spotlight.getConstant());
      shader.setFloat(gl, "spotlight.linear", spotlight.getLinear());
      shader.setFloat(gl, "spotlight.quadratic", spotlight.getQuadratic());
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (diffuse!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      diffuse.bind(gl);
    }
    if (specular!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      specular.bind(gl);
    }

    mesh.render(gl);
  }

  //lab code
  private boolean mesh_null() {
    return (mesh==null);
  }

  //lab code
  public void dispose(GL3 gl) {
    mesh.dispose(gl);  // only need to dispose of mesh
  }

}