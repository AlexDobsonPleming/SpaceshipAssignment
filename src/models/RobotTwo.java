package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import tooling.Model;
import tooling.scenegraph.NameNode;
import tooling.scenegraph.SGNode;
import tooling.scenegraph.TransformNode;

/**
 * This class stores the models.Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class RobotTwo {

  private TextureLibrary textures;

  private Shapes shapes;
  private Model sphere;
  private Model cube;
  private SGNode root;

  private Branch spotlight;

  private TransformNode translateRoot, rotateSpotlight;

  public RobotTwo(GL3 gl, Shapes shapeFactory, TextureLibrary textures) {

    shapes = shapeFactory;

    root = new NameNode("robot two structure");

    sphere = shapes.makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = shapes.makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

    Branch body = new Branch(cube, 1.5f,1f, 1f);
    Branch leftEye = new Branch(sphere, 0.3f, 0.3f, 0.3f);
    Branch rightEye = new Branch(sphere, 0.3f, 0.3f, 0.3f);
    Branch antennae = new Branch(cube, 0.1f, 1f, 0.1f);
    spotlight = new Branch(cube, 0.3f, 0.3f, 0.3f);

    TransformNode translateAboveBody = new TransformNode("translate above base", Mat4Transform.translate(0, body.scaleY, 0));

    TransformNode translateToLeftEye = new TransformNode("translate left eye",Mat4Transform.translate(0, body.scaleY,0));
    TransformNode translateToRightEye = new TransformNode("translate right eye",Mat4Transform.translate(0, body.scaleY,0));

    TransformNode translateAboveAntennae = new TransformNode("translate right eye",Mat4Transform.translate(0, antennae.scaleY,0));


    // The next few are global variables so they can be updated in other methods
    translateRoot = new TransformNode("translate root", Mat4Transform.translate(3,0,-3));
    rotateSpotlight = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateRoot);
      translateRoot.addChild(body);
        body.addChild(translateAboveBody);
          translateAboveBody.addChild(antennae);
            antennae.addChild(translateAboveAntennae);
              translateAboveAntennae.addChild(spotlight);
        body.addChild(translateToLeftEye);
          translateToLeftEye.addChild(leftEye);
        body.addChild(translateToRightEye);
          translateToRightEye.addChild(rightEye);

      root.update();  // IMPORTANT – must be done every time any part of the scene graph changes

  }


  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }

  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;

    rotateSpotlight.setTransform(Mat4Transform.rotateAroundY((angularVelocity * (float)elapsedTime) % 360));

    root.update(); // IMPORTANT – the scene graph has changed
  }

  public Vec3 getSpotlightPosition() {
    return spotlight.getNode().getPosition();
  }

  public Vec3 getSpotlightDirection() {
    return spotlight.getNode().getDirection();
  }



  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}