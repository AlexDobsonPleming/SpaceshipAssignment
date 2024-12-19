package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec2;
import tooling.*;
import tooling.scenegraph.*;

import java.util.function.Supplier;

/**
 * This class encapsulates the dancing robot
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class RobotOne {

  private TextureLibrary textures;

  private Supplier<Boolean> dancing = () -> true;
  private boolean alwaysDancing = false;

  private Shapes shapes;
  private Model sphere, headSphere, ringSphere, auto_body, handleShape, auto_default, auto_base;
  private Model cube;
  private SGNode root;

  private TransformNode rootTranslate, rotateLowerLeg, rotateUpperLeg, rotateBody, waveLeft, waveRight;
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

    auto_default = shapes.makeSphere(gl, textures.get("auto_default"), textures.get("auto_default_specular"));

    auto_base = shapes.makeCube(gl, textures.get("auto_base"), textures.get("auto_body_specular"));
    Branch base = new Branch(auto_base, 3f, 0.1f, 3f);

    Branch lowerLeg = new Branch(auto_default, 0.5f,2.0f,0.5f);
    Branch upperLeg = new Branch(auto_default, 0.5f,2f,0.5f);

    auto_body = shapes.makeSphere(gl, textures.get("auto_body"), textures.get("auto_body_specular"));
    Branch body = new Branch(auto_body, 1f,3.1f, 1.4f);
    Branch leftArm = new Branch(auto_default, 0.3f, 1f, 0.3f);
    Branch rightArm = new Branch(auto_default, 0.3f, 1f, 0.3f);

    headSphere = shapes.makeSphere(gl, textures.get("auto_face"), textures.get("auto_face"));
    ringSphere = shapes.makeSphere(gl, textures.get("auto_ring"), textures.get("auto_ring"));
    Branch neck = new Branch(auto_default, 0.2f, 2, 0.2f);
    Branch head = new Branch(headSphere, 1.5f, 1.5f, 0.1f);
    Branch ring = new Branch(ringSphere, head.scaleX * 2, head.scaleY * 2, 0.5f);

    handleShape = shapes.makeSphere(gl, textures.get("handle"), textures.get("jade_specular"));
    float handleX = 0.2f;
    float handleY = 0.8f;
    float handleZ = 0.2f;
    Branch handle1 = new Branch(handleShape, handleX, handleY, handleZ);
    Branch handle2 = new Branch(handleShape, handleX, handleY, handleZ);
    Branch handle3 = new Branch(handleShape, handleX, handleY, handleZ);
    Branch handle4 = new Branch(handleShape, handleX, handleY, handleZ);
    Branch handle5 = new Branch(handleShape, handleX, handleY, handleZ);

    TransformNode translateAboveBase = new TransformNode("translate above base", Mat4Transform.translate(0, base.scaleY, 0));
    TransformNode translateToTopOfLowerLeg = new TransformNode("translate(0,"+ lowerLeg.scaleY +",0)",Mat4Transform.translate(0,lowerLeg.scaleY,0));
    TransformNode translateToTopOfUpperLeg = new TransformNode("translate(0,"+ upperLeg.scaleY +",0)",Mat4Transform.translate(0,upperLeg.scaleY,0));

    Mat4 leftArmTranslation = Mat4Transform.translate(-1 * body.scaleX + 0.5f, body.scaleY / 2, 0);
    TransformNode translateToLeftArmPosition = new TransformNode("translate to left arm pos", leftArmTranslation);
    TransformNode initialRotationLeftArmPosition = new TransformNode("initial rotation left arm", Mat4Transform.rotateAroundZ(90));

    TransformNode translateToRightArmPosition = new TransformNode("translate to right arm pos", Mat4Transform.translate(body.scaleX - 0.5f, body.scaleY / 2, 0));
    TransformNode initialRotationRightArmPosition = new TransformNode("initial rotation right arm", Mat4Transform.rotateAroundZ(-90));

    TransformNode translateToHeadPosition = new TransformNode("transform to head pos", Mat4Transform.translate(0, body.scaleY, 0));
    TransformNode spinHeadAround = new TransformNode("fix head rotation", Mat4Transform.rotateAroundY(180));
    TransformNode translateToEndOfNeck = new TransformNode("translate to end of neck", Mat4Transform.translate(0, neck.scaleY - head.scaleY / 2, head.scaleZ * 1.4f));
    TransformNode centreRing = new TransformNode("spin ring up", Mat4Transform.translate(0, -1 * head.scaleY / 2, 0));
    TransformNode centreHandleTransforms = new TransformNode("centre handle transforms", Mat4Transform.translate(0, ring.scaleY / 2, 0));

//    TransformNode translateToRingEdge = new TransformNode("transform to ring edge", Mat4Transform.translate(0, ring.scaleY * 0.8f,0));
    Mat4 toRingEdge = Mat4Transform.translate(0, ring.scaleY * 0.5f,0);

    TransformNode rotateToHandle1Pos = new TransformNode("transform to handle pos", Mat4.multiply(Mat4Transform.rotateAroundZ(45), toRingEdge));
    TransformNode rotateToHandle2Pos = new TransformNode("transform to handle pos", Mat4.multiply(Mat4Transform.rotateAroundZ(115), toRingEdge));
    TransformNode rotateToHandle3Pos = new TransformNode("transform to handle pos", Mat4.multiply(Mat4Transform.rotateAroundZ(180), toRingEdge));
    TransformNode rotateToHandle4Pos = new TransformNode("transform to handle pos", Mat4.multiply(Mat4Transform.rotateAroundZ(245), toRingEdge));
    TransformNode rotateToHandle5Pos = new TransformNode("transform to handle pos", Mat4.multiply(Mat4Transform.rotateAroundZ(315), toRingEdge));


    // The next few are global variables so they can be updated in other methods
    rootTranslate = new TransformNode("translate root", Mat4Transform.translate(-2,0,-7));
    rotateLowerLeg = new TransformNode("rotateAroundZ("+ rotateLowerLegAngle +")", Mat4Transform.rotateAroundZ(rotateLowerLegAngle));
    rotateUpperLeg = new TransformNode("rotateAroundZ("+ rotateUpperLegAngle +")",Mat4Transform.rotateAroundZ(rotateUpperLegAngle));
    rotateBody = new TransformNode("rotateAroundZ("+ rotateBodyAngle +")",Mat4Transform.rotateAroundZ(rotateBodyAngle));
    waveLeft = new TransformNode("rotate left arm", Mat4Transform.rotateAroundZ(waveLeftAngle));
    waveRight = new TransformNode("rotate left arm", Mat4Transform.rotateAroundZ(0));


    root.addChild(rootTranslate);
      rootTranslate.addChild(base);
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
                            translateToHeadPosition.addChild(neck);
                              neck.addChild(translateToEndOfNeck);
                                translateToEndOfNeck.addChild(spinHeadAround);
                                  spinHeadAround.addChild(head);
                                    head.addChild(centreRing);
                                      centreRing.addChild(ring);
                                        ring.addChild(centreHandleTransforms);
                                          centreHandleTransforms.addChild(rotateToHandle1Pos);
                                            rotateToHandle1Pos.addChild(handle1);
                                          centreHandleTransforms.addChild(rotateToHandle2Pos);
                                            rotateToHandle2Pos.addChild(handle2);
                                          centreHandleTransforms.addChild(rotateToHandle3Pos);
                                            rotateToHandle3Pos.addChild(handle3);
                                          centreHandleTransforms.addChild(rotateToHandle4Pos);
                                            rotateToHandle4Pos.addChild(handle4);
                                          centreHandleTransforms.addChild(rotateToHandle5Pos);
                                            rotateToHandle5Pos.addChild(handle5);


      root.update();

  }

  public Vec2 getLocation() {
    return new Vec2(rotateLowerLeg.getPosition().x, rotateLowerLeg.getPosition().z);
  }
  public void setDancingSupplier(Supplier<Boolean> dancingSupplier) {
    dancing = dancingSupplier;
  }

  public boolean isAlwaysDancing() {
    return alwaysDancing;
  }
  public void setAlwaysDancing(boolean alwaysDancing) {
    this.alwaysDancing = alwaysDancing;
  }

  private boolean wasDancing = alwaysDancing;
  private double timePaused = 0;
  private double timeSpentNotDancing = 0;
  private boolean shouldDance() {
    return alwaysDancing || dancing.get();
  }
  public void render(GL3 gl, double elapsedTime) {
    if (shouldDance() != wasDancing) {
      if (wasDancing) {
        //stopped dancing
        timePaused = elapsedTime;
      } else {
        //started dancing again
        timeSpentNotDancing += (elapsedTime - timePaused);
      }
      wasDancing = !wasDancing;
    }
    if (shouldDance()) {
      updateAnimation(elapsedTime - timeSpentNotDancing);
    }
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