package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec2;
import gmaths.Vec3;
import tooling.Model;
import tooling.scenegraph.NameNode;
import tooling.scenegraph.SGNode;
import tooling.scenegraph.TransformNode;

/**
 * This class encapsulates the robot on rails
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class RobotTwo {

  private TextureLibrary textures;

  private Shapes shapes;
  private Model sphere;
  private Model cube;
  private SGNode root;

  private Branch spotlight;

  private TransformNode translateRoot, rotationRoot, rotateSpotlight;

  private Vec2[] anchorPoints = {
          new Vec2(-6.25f, 13.5f),
          new Vec2(-6.25f, -11f),
          new Vec2(6.25f, -11f),
          new Vec2(6.25f, 13.5f),
  };
  private double speed = 5.0;
  private double[] segmentDurations;
  private double totalPathTime = 0;

  public RobotTwo(GL3 gl, Shapes shapeFactory, TextureLibrary textures) {

    shapes = shapeFactory;

    root = new NameNode("robot two structure");

    sphere = shapes.makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));
    cube = shapes.makeCube(gl, textures.get("arrow"), textures.get("jade_specular"));

    intialiseTrackTime();

    Branch body = new Branch(cube, 1.5f,1f, 1f);
    Branch leftEye = new Branch(sphere, 0.3f, 0.3f, 0.3f);
    Branch rightEye = new Branch(sphere, 0.3f, 0.3f, 0.3f);
    Branch antennae = new Branch(cube, 0.1f, 1f, 0.1f);
    spotlight = new Branch(cube, 0.3f, 0.3f, 0.3f);

    TransformNode translateAboveBody = new TransformNode("translate above base", Mat4Transform.translate(0, body.scaleY, 0));
    TransformNode baseRotation = new TransformNode("base rotation", Mat4Transform.rotateAroundY(90));

    TransformNode translateToLeftEye = new TransformNode("translate left eye",Mat4Transform.translate(0, body.scaleY,0));
    TransformNode translateToRightEye = new TransformNode("translate right eye",Mat4Transform.translate(0, body.scaleY,0));

    TransformNode translateAboveAntennae = new TransformNode("translate right eye",Mat4Transform.translate(0, antennae.scaleY,0));


    translateRoot = new TransformNode("translate root", translateXZ(anchorPoints[0]));
    rotationRoot = new TransformNode("rotation root", Mat4Transform.rotateAroundY(0));
    rotateSpotlight = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateRoot);
      translateRoot.addChild(baseRotation);
        baseRotation.addChild(rotationRoot);
          rotationRoot.addChild(body);
            body.addChild(translateAboveBody);
              translateAboveBody.addChild(antennae);
                antennae.addChild(translateAboveAntennae);
                  translateAboveAntennae.addChild(rotateSpotlight);
                    rotateSpotlight.addChild(spotlight);
            body.addChild(translateToLeftEye);
              translateToLeftEye.addChild(leftEye);
            body.addChild(translateToRightEye);
              translateToRightEye.addChild(rightEye);

      root.update();
  }

  private Mat4 translateXZ(Vec2 point) {
    return Mat4Transform.translate(point.x, 0, point.y);
  }

  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }

  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;
    updateRailwayPosition(elapsedTime);
    rotateSpotlight.setTransform(Mat4Transform.rotateAroundY((angularVelocity * (float)elapsedTime) % 360));

    root.update();
  }

  public void intialiseTrackTime() {
    segmentDurations = new double[anchorPoints.length];

    for (int i = 0; i < anchorPoints.length; i++) {
      Vec2 p1 = anchorPoints[i];
      Vec2 p2 = anchorPoints[(i + 1) % anchorPoints.length]; // Loop back to start
      double distance = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
      segmentDurations[i] = distance / speed;
      totalPathTime += segmentDurations[i];
    }
  }

  private int previousSegment = -1; // Track the last segment index

  public void updateRailwayPosition(double elapsedTime) {
    double adjustedTime = elapsedTime % totalPathTime;

    double cumulativeDuration = 0;

    for (int i = 0; i < segmentDurations.length; i++) {
      if (adjustedTime < cumulativeDuration + segmentDurations[i]) {
        if (previousSegment != i) {
          previousSegment = i;
          rotationRoot.setTransform(Mat4Transform.rotateAroundY(90 * i));
          return;
        }
        double segmentProgress = (adjustedTime - cumulativeDuration) / segmentDurations[i];
        Vec2 start = anchorPoints[i];
        Vec2 end = anchorPoints[(i + 1) % anchorPoints.length];
        Vec2 progressBetweenCorners = linerInterpolate(start, end, (float)segmentProgress);
        translateRoot.setTransform(translateXZ(progressBetweenCorners));
        return;
      }
      cumulativeDuration += segmentDurations[i];
    }

  }

  private Vec2 linerInterpolate(Vec2 start, Vec2 end, float t) {
    return new Vec2(
            start.x + (end.x - start.x) * t,
            start.y + (end.y - start.y) * t
    );
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