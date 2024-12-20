package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec2;
import gmaths.Vec3;
import tooling.Model;
import tooling.SpotLight;
import tooling.scenegraph.NameNode;
import tooling.scenegraph.SGNode;
import tooling.scenegraph.TransformNode;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class stores the models.Robot
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */
//private class Point
public class RobotTwo {

  private TextureLibrary textures;

  private Shapes shapes;
  private Model sphere;
  private Model body_cube, eyeCube, antennae_cube, spotlight_housing_cube, bulb;
  private SGNode root;

  private SpotLight spotLight;

  public SpotLight getSpotLight() {
    return spotLight;
  }

  private Branch spotlightHousing;
  private Branch spotlightBulb;

  private TransformNode translateRoot, rotationRoot, rotateSpotlight;

  private interface ITraversal {
    double getDuration();
    Vec2 getStart();
    Vec2 getFinish();
  }
  private class StraightTraversal implements ITraversal{
    private Vec2 start;
    private Vec2 finish;
    StraightTraversal(Vec2 start, Vec2 finish) {
      this.start = start; this.finish = finish;
    }

    public Vec2 getStart() { return start; }
    public Vec2 getFinish() { return finish; }
    public double getDuration() {
      double distance = Math.sqrt(Math.pow(finish.x - start.x, 2) + Math.pow(finish.y - start.y, 2));
      return distance / speed;
    }
  }
  private static double cornerDuration = 3;
  private class CornerTraversal implements ITraversal {
    private Vec2 start;
    private Vec2 finish;
    CornerTraversal(Vec2 start, Vec2 finish) {
      this.start = start; this.finish = finish;
    }
    public Vec2 getStart() { return start; }
    public Vec2 getFinish() { return finish; }

//    private Vec2 centreOfCorner() {
//      return new Vec2(Math.min(start.x, finish.x))
//    }
    public double getDuration() {
      return cornerDuration;
    }
  }

  private ITraversal[] newAnchorPoints = {
          new StraightTraversal(new Vec2(-6.25f, 13.5f), new Vec2(-6.25f, -11f)),
          new StraightTraversal(new Vec2(-6.25f, -11f), new Vec2(6.25f, -11f)),
          new StraightTraversal(new Vec2(6.25f, -11f), new Vec2(6.25f, 13.5f)),
          new StraightTraversal(new Vec2(6.25f, 13.5f), new Vec2(-6.25f, 13.5f))
  };
  private double speed = 5.0;

  public RobotTwo(GL3 gl, Shapes shapeFactory, TextureLibrary textures) {
    shapes = shapeFactory;

    spotLight = new SpotLight(gl, this::getSpotlightPosition, this::getSpotlightDirection);

    root = new NameNode("robot two structure");

    sphere = shapes.makeSphere(gl, textures.get("arrow"), textures.get("jade_specular"));

    body_cube = shapes.makeCube(gl, textures.get("auto_default"), textures.get("auto_default_specular"));
    Branch body = new Branch(body_cube, 1f,1.5f, 1f);
    eyeCube = shapes.makeCube(gl, textures.get("mo_eye"), textures.get("mo_eye"));
    Branch leftEye = new Branch(eyeCube, 0.3f, 0.3f, 0.3f);
    Branch rightEye = new Branch(eyeCube, 0.3f, 0.3f, 0.3f);
    antennae_cube = shapes.makeCube(gl, textures.get("auto_default"), textures.get("auto_default_specular"));
    Branch antennae = new Branch(antennae_cube, 0.1f, 1f, 0.1f);
    spotlight_housing_cube = shapes.makeCube(gl, textures.get("bulb_housing"), textures.get("auto_default_specular"));
    spotlightHousing = new Branch(spotlight_housing_cube, 0.3f, 0.3f, 0.3f);

    bulb = shapes.makeLightSphere(gl, textures.get("auto_default"), textures.get("auto_default_specular"));
    spotlightBulb = new Branch(bulb, 0.2f, 0.2f, 0.2f);

    TransformNode translateAboveBody = new TransformNode("translate above base", Mat4Transform.translate(0, body.scaleY, 0));
    TransformNode baseRotation = new TransformNode("base rotation", Mat4Transform.rotateAroundY(90));

    TransformNode translateToLeftEye = new TransformNode("translate left eye",Mat4Transform.translate(body.scaleX / 2, body.scaleY / 2,body.scaleZ / 4));
    TransformNode translateToRightEye = new TransformNode("translate right eye",Mat4Transform.translate(body.scaleX / 2, body.scaleY / 2,-1 * body.scaleZ / 4));

    TransformNode translateAboveAntennae = new TransformNode("translate right eye",Mat4Transform.translate(0, antennae.scaleY,0));
    TransformNode translateToBulbPosition = new TransformNode("translate to bulb position", Mat4Transform.translate(0, spotlightHousing.scaleY / 5, spotlightHousing.scaleZ / 3));



    translateRoot = new TransformNode("translate root", translateXZ(newAnchorPoints[0].getStart()));
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
                    rotateSpotlight.addChild(spotlightHousing);
                      spotlightHousing.addChild(translateToBulbPosition);
                        translateToBulbPosition.addChild(spotlightBulb);
            body.addChild(translateToLeftEye);
              translateToLeftEye.addChild(leftEye);
            body.addChild(translateToRightEye);
              translateToRightEye.addChild(rightEye);

      root.update();
  }

  public Vec2 getLocation() {
    return new Vec2(rotateSpotlight.getPosition().x, rotateSpotlight.getPosition().z);
  }

  private Supplier<Vec2> robotOneLocation;
  public void setRobotOneLocationSupplier(Supplier<Vec2> robotOneLocation) {
    this.robotOneLocation = robotOneLocation;
  }
  private float proximityThreshold = 6.0f;
  public Boolean isCloseToRobotOne() {
    return pythagoras(getLocation(), robotOneLocation.get()) < proximityThreshold;
  }

  public static float pythagoras(Vec2 v1, Vec2 v2) {
    float dx = v2.x - v1.x;
    float dy = v2.y - v1.y;
    return (float) Math.sqrt(dx * dx + dy * dy);
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

//  public void intialiaseTrackTime() {
//    segmentDurations = new double[anchorPoints.length];
//
//    for (int i = 0; i < anchorPoints.length; i++) {
//      Vec2 p1 = anchorPoints[i];
//      Vec2 p2 = anchorPoints[(i + 1) % anchorPoints.length]; // Loop back to start
//      double distance = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
//      segmentDurations[i] = distance / speed;
//      totalPathTime += segmentDurations[i];
//    }
//  }

  private int previousSegment = -1; // Track the last segment index

  public void updateRailwayPosition(double elapsedTime) {
    double totalPathTime = Arrays.stream(newAnchorPoints).map(ITraversal::getDuration).mapToDouble(Double::doubleValue).sum();
    double adjustedTime = elapsedTime % totalPathTime;

    double cumulativeDuration = 0;

    for (int i = 0; i < newAnchorPoints.length; i++) {
      if (adjustedTime < cumulativeDuration + newAnchorPoints[i].getDuration()) {
        if (previousSegment != i) {
          previousSegment = i;
          rotationRoot.setTransform(Mat4Transform.rotateAroundY(-1 * 90 * i));
          return;
        }
        double segmentProgress = (adjustedTime - cumulativeDuration) / newAnchorPoints[i].getDuration();
        Vec2 start = newAnchorPoints[i].getStart();
        Vec2 end = newAnchorPoints[i].getFinish();
        Vec2 progressBetweenCorners = linearInterpolate(start, end, (float)segmentProgress);
        translateRoot.setTransform(translateXZ(progressBetweenCorners));
        return;
      }
      cumulativeDuration +=newAnchorPoints[i].getDuration();
    }

  }

  private Vec2 linearInterpolate(Vec2 start, Vec2 end, float t) {
    return new Vec2(
            start.x + (end.x - start.x) * t,
            start.y + (end.y - start.y) * t
    );
  }

  public Vec3 getSpotlightPosition() {
    return spotlightHousing.getNode().getPosition();
  }

  public Vec3 getSpotlightDirection() {
    return spotlightHousing.getNode().getDirection();
  }

  public void dispose(GL3 gl) {
    sphere.dispose(gl);
  }
}