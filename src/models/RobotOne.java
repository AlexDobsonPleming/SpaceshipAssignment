package models;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import models.meshes.Cube;
import models.meshes.Cylinder;
import models.meshes.Sphere;
import tooling.*;
import tooling.scenegraph.ModelNode;
import tooling.scenegraph.NameNode;
import tooling.scenegraph.SGNode;
import tooling.scenegraph.TransformNode;

/**
 * This class stores the models.Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class RobotOne {

  private final Camera camera;
  private final Light light;

  private final Model sphere;
    private final Model cube;
    private final Model cube2;

  private final SGNode robotRoot;
  private float xPosition = 0;
  private final TransformNode robotMoveTranslate;
  private TransformNode lowerLegRotate;
  private TransformNode upperLegRotate;
  private TransformNode upperLegOrigin;

  public RobotOne(GL3 gl, Camera cameraIn, Light lightIn, TextureLibrary textures) {

    this.camera = cameraIn;
    this.light = lightIn;

    Texture t1 = textures.get("jade_diffuse");
    Texture t2 = textures.get("jade_specular");
    Texture t3 = textures.get("container_diffuse");
    Texture t4 = textures.get("container_specular");
    Texture t5 = textures.get("watt_diffuse");
    Texture t6 = textures.get("watt_specular");

    sphere = makeSphere(gl, t1,t2);

    cube = makeCube(gl, t3,t4);
    cube2 = makeCube(gl, t5,t6);

    // robot

    float legLength = 3.5f;
    
    robotRoot = new NameNode("root");
    robotMoveTranslate = new TransformNode("robot transform",Mat4Transform.translate(xPosition,0,0));
    
    TransformNode robotTranslate = new TransformNode("robot transform",Mat4Transform.translate(0,legLength,0));

    // make pieces
    NameNode base = makeBase(gl, cube, t1, t2);
    NameNode lowerLeg = makeLowerLeg(gl, t1, t2);
    NameNode upperLeg = makeUpperLeg(gl, t1, t2);


    
    //Once all the pieces are created, then the whole robot can be created.
    robotRoot.addChild(robotMoveTranslate);
      robotMoveTranslate.addChild(robotTranslate);
        robotTranslate.addChild(base);
          base.addChild(lowerLeg);
            lowerLeg.addChild(upperLeg);
    
    robotRoot.update();  // IMPORTANT - don't forget this

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

  private Model makeCylinder(GL3 gl, Texture t1, Texture t2) {
    String name= "cylinder";
    Mesh mesh = new Mesh(gl, new Cylinder());
    Shader shader = new Shader(gl, "assets/shaders/vs_standard.glsl", "assets/shaders/fs_standard_2t.glsl");
    Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    Model cube = new Model(name, mesh, modelMatrix, shader, material, light, camera, t1, t2);
    return cube;
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

  private NameNode makeLowerLeg(GL3 gl, Texture t1, Texture t2) {
    NameNode lowerLeg = new NameNode("lower leg");
    float bodyWidth = 0.5f;
    float bodyHeight = 2.5f;
    float bodyDepth = 0.5f;

    TransformNode lowerLegTranslate = new TransformNode("lower body translate",
            Mat4Transform.translate(0,-1.5f * bodyHeight,0));
    TransformNode lowerLegFixRotation = new TransformNode("lower leg fix rotation", Mat4Transform.rotateAroundX(180));
    lowerLegRotate = new TransformNode("lower leg rotate",Mat4Transform.rotateAroundX(0));

    TransformNode lowerLegScale = new TransformNode("lower leg scale", Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth));

    ModelNode lowerLegShape = new ModelNode("lower leg model", makeSphere(gl, t1, t2));

    TransformNode lowerLegReorient = new TransformNode("temp", Mat4Transform.translate(0,-0.5f * bodyHeight,0));

    lowerLeg.addChild(lowerLegTranslate);
    lowerLegTranslate.addChild(lowerLegFixRotation);
    lowerLegFixRotation.addChild(lowerLegRotate);
    lowerLegRotate.addChild(lowerLegReorient);
    lowerLegReorient.addChild(lowerLegScale);
    lowerLegScale.addChild(lowerLegShape);
    return lowerLeg;
  }

  private NameNode makeUpperLeg(GL3 gl, Texture t1, Texture t2) {
    NameNode upperLeg = new NameNode("upper leg");
    upperLegOrigin = new TransformNode("ul origin", Mat4Transform.rotateAroundX(0));
    float bodyWidth = 0.5f;
    float bodyHeight = 2.5f;
    float bodyDepth = 0.5f;
    TransformNode upperLegTranslate = new TransformNode("upper body translate",
            Mat4Transform.translate(0,-0.5f * bodyHeight,0));
    TransformNode upperLegFixRotation = new TransformNode("upper leg fix rotation", Mat4Transform.rotateAroundX(180));
    upperLegRotate = new TransformNode("upper leg rotate",Mat4Transform.rotateAroundX(0));

    TransformNode upperLegScale = new TransformNode("upper leg scale", Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth));

    ModelNode upperLegShape = new ModelNode("upper leg model", makeSphere(gl, t1, t2));

    TransformNode upperLegRetranslate = new TransformNode("temp", Mat4Transform.translate(0,-0.5f * bodyHeight,0));

//    upperLeg.addChild(lowerLegRotate);
//    upperLeg.addChild(upperLegOrigin);
    upperLeg.addChild(upperLegTranslate);
    upperLegTranslate.addChild(upperLegFixRotation);
    upperLegFixRotation.addChild(upperLegRotate);
    upperLegRotate.addChild(upperLegRetranslate);
    upperLegRetranslate.addChild(upperLegScale);
    upperLegScale.addChild(upperLegShape);
    return upperLeg;
  }

  private NameNode makeBody(GL3 gl, Model cube, Texture t1, Texture t2) {
    NameNode body = new NameNode("body");
    float bodyWidth = 1;
    float bodyHeight = 5f;
    float bodyDepth = 1;
    Mat4 size = Mat4Transform.scale(bodyWidth,bodyHeight,bodyDepth);
    Mat4 translate = Mat4Transform.translate(0,-3.5f,0);
    Mat4 transform = Mat4.multiply(translate, size);
    TransformNode baseTransform = new TransformNode("body transform", transform);
    ModelNode bodyShape = new ModelNode("body model", makeSphere(gl, t1, t2));
    body.addChild(baseTransform);
    baseTransform.addChild(bodyShape);
    return body;
  }

    
  private NameNode makeHead(GL3 gl, float bodyHeight, float headScale, Model sphere) {
    NameNode head = new NameNode("head"); 
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,bodyHeight,0));
    m = Mat4.multiply(m, Mat4Transform.scale(headScale,headScale,headScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode headTransform = new TransformNode("head transform", m);
    ModelNode headShape = new ModelNode("models.meshes.Sphere(head)", sphere);
    head.addChild(headTransform);
    headTransform.addChild(headShape);
    return head;
  }

  private NameNode makeLeftArm(GL3 gl, float bodyWidth, float bodyHeight, float armLength, float armScale, Model cube) {
    NameNode leftArm = new NameNode("left arm");
    TransformNode leftArmTranslate = new TransformNode("leftarm translate", 
                                          Mat4Transform.translate((bodyWidth*0.5f)+(armScale*0.5f),bodyHeight,0));
    // leftArmRotate is a class attribute with a transform that changes over time
    lowerLegRotate = new TransformNode("leftarm rotate",Mat4Transform.rotateAroundX(180));
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.scale(armScale,armLength,armScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftArmScale = new TransformNode("leftarm scale", m);
    ModelNode leftArmShape = new ModelNode("models.meshes.Cube(left arm)", cube);
    leftArm.addChild(leftArmTranslate);
    leftArmTranslate.addChild(lowerLegRotate);
    lowerLegRotate.addChild(leftArmScale);
    leftArmScale.addChild(leftArmShape);
    return leftArm;
  }


  private NameNode makeLeftLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    NameNode leftLeg = new NameNode("left leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate((bodyWidth*0.5f)-(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode leftLegTransform = new TransformNode("leftleg transform", m);
    ModelNode leftLegShape = new ModelNode("models.meshes.Cube(leftleg)", cube);
    leftLeg.addChild(leftLegTransform);
    leftLegTransform.addChild(leftLegShape);
    return leftLeg;
  }

  private NameNode makeRightLeg(GL3 gl, float bodyWidth, float legLength, float legScale, Model cube) {
    NameNode rightLeg = new NameNode("right leg");
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(-(bodyWidth*0.5f)+(legScale*0.5f),0,0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
    m = Mat4.multiply(m, Mat4Transform.scale(legScale,legLength,legScale));
    m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
    TransformNode rightLegTransform = new TransformNode("rightleg transform", m);
    ModelNode rightLegShape = new ModelNode("models.meshes.Cube(rightleg)", cube);
    rightLeg.addChild(rightLegTransform);
    rightLegTransform.addChild(rightLegShape);
    return rightLeg;
  }

  public void render(GL3 gl) {
    robotRoot.draw(gl);
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
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }


  // only does left arm
  public void updateAnimation(double elapsedTime) {
    float rotateAngle = 45f*(float)Math.sin(elapsedTime);
    lowerLegRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    lowerLegRotate.update();
    upperLegOrigin.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    upperLegOrigin.update();
    upperLegRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    upperLegRotate.update();
  }


  public void dispose(GL3 gl) {
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
}