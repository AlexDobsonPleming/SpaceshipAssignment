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
  private Shapes shapes;
  private SGNode root;

  private TransformNode globeRotate;

  public Globe(GL3 gl, Shapes shapes, TextureLibrary textures) {

    this.shapes = shapes;

    sphere = shapes.makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = shapes.makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

    root = new NameNode("globe root");

    Branch base = new Branch(cube, 1f, 1f, 1f);
    Branch pedestal = new Branch(sphere, 0.2f,0.5f,0.2f);
    Branch globe = new Branch(sphere, 1f,1f,1f);

    TransformNode translateBase = new TransformNode("translate base", Mat4Transform.translate(5, 0, 5));

    TransformNode translateAboveBase = new TransformNode("translate above base", Mat4Transform.translate(0, base.scaleY, 0));
    TransformNode translateToTopOfPedestal = new TransformNode("translate(0,"+ pedestal.scaleY +",0)",Mat4Transform.translate(0,pedestal.scaleY,0));


    globeRotate = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateBase);
    translateBase.addChild(base);
        base.addChild(translateAboveBase);
          translateAboveBase.addChild(pedestal);
            pedestal.addChild(translateToTopOfPedestal);
              translateToTopOfPedestal.addChild(globeRotate);
                globeRotate.addChild(globe);

      root.update();  // IMPORTANT – must be done every time any part of the scene graph changes

  }

  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }


  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;

    globeRotate.setTransform(Mat4Transform.rotateAroundY((angularVelocity * (float)elapsedTime) % 360));

    root.update(); // IMPORTANT – the scene graph has changed
  }

  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}