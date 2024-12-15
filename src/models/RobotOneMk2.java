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

public class RobotOneMk2 {

  private TextureLibrary textures;

  private Camera camera;
  private Model sphere;
  private Model cube;
  private Light light;
  private SGNode twoBranchRoot;

  private TransformNode translateX, rotateLowerLeg, rotateUpperLeg, rotateBody, waveLeft, waveRight;
  private float xPosition = 0;
  private float rotateLowerLegAngleStart = 10, rotateLowerLegAngle = rotateLowerLegAngleStart;
  private float rotateUpperLegAngleStart = -60, rotateUpperLegAngle = rotateUpperLegAngleStart;
  private float rotateBodyAngleStart = 30, rotateBodyAngle = rotateBodyAngleStart;
  private float waveLeftAngleStart = 45, waveLeftAngle = waveLeftAngleStart;
  private float waveRightAngleStart = 45, waveRightAngle = waveRightAngleStart;

  public RobotOneMk2(GL3 gl, Camera cameraIn, Light lightIn, TextureLibrary textures) {

    this.camera = cameraIn;
    this.light = lightIn;

    twoBranchRoot = new NameNode("two-branch structure");



    sphere = makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

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


    twoBranchRoot.addChild(translateX);
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



      twoBranchRoot.update();  // IMPORTANT – must be done every time any part of the scene graph changes

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

  class Branch implements ISGNodeContainer {
    SGNode node;

    public float scaleX;
    public float scaleY;
    public float scaleZ;

    public Branch(Model sphere, float sx, float sy, float sz) {
      scaleX = sx;
      scaleY = sy;
      scaleZ = sz;

      node = new NameNode("upper branch");
      Mat4 m = Mat4Transform.scale(scaleX,scaleY,scaleZ);
      m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
      TransformNode upperBranch = new TransformNode("scale("+sx+","+sy+","+sz+");translate(0,0.5,0)", m);
      ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
      node.addChild(upperBranch);
      upperBranch.addChild(sphereNode);
    }

    public SGNode getNode() { return node; }

    public void addChild(SGNode child) {
      node.addChild(child);
    }
  }

  private NameNode makeBase(GL3 gl, Model cube, Texture t1, Texture t2) {
    NameNode body = new NameNode("base");
    float baseWidth = 3;
    float baseHeight = 0.1f;
    float baseDepth = 3;
    Mat4 size = Mat4Transform.scale(baseWidth,baseHeight,baseDepth);
    Mat4 translate = Mat4Transform.translate(0,-3.5f,0);
    Mat4 transform = Mat4.multiply(translate, size);
    TransformNode baseTransform = new TransformNode("base transform", transform);
    ModelNode bodyShape = new ModelNode("base model", makeCube(gl, t1, t2));
    body.addChild(baseTransform);
    baseTransform.addChild(bodyShape);
    return body;
  }

  public void render(GL3 gl) {
    twoBranchRoot.draw(gl);
  }

  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    translateX.setTransform(Mat4Transform.translate(xPosition,0,0));
    translateX.update();
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

    twoBranchRoot.update(); // IMPORTANT – the scene graph has changed
  }



  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}