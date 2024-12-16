package models;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import models.meshes.Cube;
import models.meshes.Sphere;
import tooling.*;
import tooling.scenegraph.*;

/**
 * This class stores the models.Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Globe {

  private TextureLibrary textures;

  private Camera camera;
  private Model sphere;
  private Model cube;
  private Light light;
  private SGNode root;

  private TransformNode globeRotate;
  private float globeRotateAngleStart = 0, globeRotateAngle = globeRotateAngleStart;


  public Globe(GL3 gl, Camera cameraIn, Light lightIn, TextureLibrary textures) {

    this.camera = cameraIn;
    this.light = lightIn;

    sphere = makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

    root = new NameNode("globe root");

    Branch base = new Branch(cube, 1f, 1f, 1f);
    Branch pedestal = new Branch(sphere, 0.2f,0.5f,0.2f);
    Branch globe = new Branch(sphere, 1f,1f,1f);

    TransformNode translateBase = new TransformNode("translate base", Mat4Transform.translate(5, 0, 5));

    TransformNode translateAboveBase = new TransformNode("translate above base", Mat4Transform.translate(0, base.scaleY, 0));
    TransformNode translateToTopOfPedestal = new TransformNode("translate(0,"+ pedestal.scaleY +",0)",Mat4Transform.translate(0,pedestal.scaleY,0));


    globeRotate = new TransformNode("rotateAroundY("+ globeRotateAngle +")", Mat4Transform.rotateAroundY(globeRotateAngle));


    root.addChild(translateBase);
    translateBase.addChild(base);
        base.addChild(translateAboveBase);
          translateAboveBase.addChild(pedestal);
            pedestal.addChild(translateToTopOfPedestal);
              translateToTopOfPedestal.addChild(globeRotate);
                globeRotate.addChild(globe);

      root.update();  // IMPORTANT – must be done every time any part of the scene graph changes

  }

  private Model makeSphere(GL3 gl, Texture t1, Texture t2) {
    String name= "sphere";
    Mesh mesh = new Mesh(gl, new Sphere());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_2t.glsl");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model sphere = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return sphere;
  } 

  private Model makeCube(GL3 gl, Texture t1, Texture t2) {
    String name= "cube";
    Mesh mesh = new Mesh(gl, new Cube());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_2t.glsl");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model cube = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return cube;
  }


  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }


  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;

    globeRotateAngle = globeRotateAngleStart *(float)Math.sin(elapsedTime);
    globeRotate.setTransform(Mat4Transform.rotateAroundY((angularVelocity * (float)elapsedTime) % 360));

    root.update(); // IMPORTANT – the scene graph has changed
  }

  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}