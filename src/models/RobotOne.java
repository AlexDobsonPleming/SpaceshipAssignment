package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import tooling.*;
import tooling.scenegraph.*;

/**
 * This class encapsulates the dancing robot
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class RobotOne {

  private TextureLibrary textures;

  private Shapes shapes;
  private Model sphere;
  private Model cube;
  private SGNode root;

  private TransformNode translateX, rotateLowerLeg, rotateUpperLeg, rotateBody, waveLeft, waveRight;
  private float xPosition = 0;
  private float rotateLowerLegAngleStart = 10, rotateLowerLegAngle = rotateLowerLegAngleStart;
  private float rotateUpperLegAngleStart = -60, rotateUpperLegAngle = rotateUpperLegAngleStart;
  private float rotateBodyAngleStart = 30, rotateBodyAngle = rotateBodyAngleStart;
  private float waveLeftAngleStart = 45, waveLeftAngle = waveLeftAngleStart;
  private float waveRightAngleStart = 45, waveRightAngle = waveRightAngleStart;

  public RobotOne(GL3 gl, Shapes shapeFactory, TextureLibrary textures) {

    shapes = shapeFactory;

    root = new NameNode("two-branch structure");



    sphere = shapes.makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = shapes.makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

    Branch base = new Branch(cube, 3f, 0.1f, 3f);
    Branch lowerLeg = new Branch(sphere, 0.5f,2.0f,0.5f);
    Branch upperLeg = new Branch(sphere, 0.5f,2f,0.5f);
    Branch body = new Branch(sphere, 1f,3.1f, 1.4f);
    Branch leftArm = new Branch(sphere, 0.3f, 1f, 0.3f);
    Branch rightArm = new Branch(sphere, 0.3f, 1f, 0.3f);
    Branch head = new Branch(cube, 1f, 1f, 1f);

    TransformNode translateAboveBase = new TransformNode("translate above base", Mat4Transform.translate(0, base.scaleY, 0));
    TransformNode translateToTopOfLowerLeg = new TransformNode("translate(0,"+ lowerLeg.scaleY +",0)",Mat4Transform.translate(0,lowerLeg.scaleY,0));
    TransformNode translateToTopOfUpperLeg = new TransformNode("translate(0,"+ upperLeg.scaleY +",0)",Mat4Transform.translate(0,upperLeg.scaleY,0));

    Mat4 leftArmTranslation = Mat4Transform.translate(-1 * body.scaleX + 0.5f, body.scaleY / 2, 0);
    TransformNode translateToLeftArmPosition = new TransformNode("translate to left arm pos", leftArmTranslation);
    TransformNode initialRotationLeftArmPosition = new TransformNode("initial rotation left arm", Mat4Transform.rotateAroundZ(90));

    TransformNode translateToRightArmPosition = new TransformNode("translate to right arm pos", Mat4Transform.translate(body.scaleX - 0.5f, body.scaleY / 2, 0));
    TransformNode initialRotationRightArmPosition = new TransformNode("initial rotation right arm", Mat4Transform.rotateAroundZ(-90));

    TransformNode translateToHeadPosition = new TransformNode("transform to head pos", Mat4Transform.translate(0, body.scaleY, 0));


    // The next few are global variables so they can be updated in other methods
    translateX = new TransformNode("translate("+xPosition+",0,0)", Mat4Transform.translate(xPosition,0,0));
    rotateLowerLeg = new TransformNode("rotateAroundZ("+ rotateLowerLegAngle +")", Mat4Transform.rotateAroundZ(rotateLowerLegAngle));
    rotateUpperLeg = new TransformNode("rotateAroundZ("+ rotateUpperLegAngle +")",Mat4Transform.rotateAroundZ(rotateUpperLegAngle));
    rotateBody = new TransformNode("rotateAroundZ("+ rotateBodyAngle +")",Mat4Transform.rotateAroundZ(rotateBodyAngle));
    waveLeft = new TransformNode("rotate left arm", Mat4Transform.rotateAroundZ(waveLeftAngle));
    waveRight = new TransformNode("rotate left arm", Mat4Transform.rotateAroundZ(0));


    root.addChild(translateX);
      translateX.addChild(base);
        base.addChild(translateAboveBase);
          translateAboveBase.addChild(rotateLowerLeg);
            rotateLowerLeg.addChild(lowerLeg);
              lowerLeg.addChild(translateToTopOfLowerLeg);
                translateToTopOfLowerLeg.addChild(rotateUpperLeg);
                  rotateUpperLeg.addChild(upperLeg.node);
                    upperLeg.addChild(translateToTopOfUpperLeg);
                      translateToTopOfUpperLeg.addChild(rotateBody);
                        rotateBody.addChild(body);
                          body.addChild(translateToLeftArmPosition);
                            translateToLeftArmPosition.addChild(initialRotationLeftArmPosition);
                              initialRotationLeftArmPosition.addChild(waveLeft);
                                waveLeft.addChild(leftArm);
                          body.addChild(translateToRightArmPosition);
                            translateToRightArmPosition.addChild(initialRotationRightArmPosition);
                              initialRotationRightArmPosition.addChild(waveRight);
                                waveRight.addChild(rightArm);
                          body.addChild(translateToHeadPosition);
                            translateToHeadPosition.addChild(head);



      root.update();  // IMPORTANT – must be done every time any part of the scene graph changes

  }

  public void render(GL3 gl) {
    root.draw(gl);
  }


  // only does left arm
  public void updateAnimation(double elapsedTime) {
    rotateLowerLegAngle = rotateLowerLegAngleStart *(float)Math.sin(elapsedTime);
    rotateLowerLeg.setTransform(Mat4Transform.rotateAroundZ(rotateLowerLegAngle));

    rotateUpperLegAngle = rotateUpperLegAngleStart *(float)Math.sin(elapsedTime*0.7f);
    rotateUpperLeg.setTransform(Mat4Transform.rotateAroundZ(rotateUpperLegAngle));

    rotateBodyAngle = rotateBodyAngleStart *(float)Math.sin(elapsedTime*0.7f);
    rotateBody.setTransform(Mat4Transform.rotateAroundZ(rotateBodyAngle));

    waveLeftAngle = waveLeftAngleStart * (float) Math.sin(elapsedTime * 0.7f);
    waveLeft.setTransform(Mat4Transform.rotateAroundZ(waveLeftAngle));

    waveRightAngle = -1 * waveRightAngleStart * (float) Math.sin(elapsedTime * 0.7f);
    waveRight.setTransform(Mat4Transform.rotateAroundZ(waveRightAngle));

    root.update(); // IMPORTANT – the scene graph has changed
  }



  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}