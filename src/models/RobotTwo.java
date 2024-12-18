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

  private TransformNode translateRoot, cornerRotation, rotationRoot, rotateSpotlight;

  private Vec2[] anchorPoints = {
          new Vec2(-6.25f, 13.5f),
          new Vec2(-6.25f, -11f),
          new Vec2(6.25f, -11f),
          new Vec2(6.25f, 13.5f),
  };
  private double speed = 3.0;
  private double radius = 2; // radius of the curved path at corners
  private double totalPathTime;
  private double[] straightDurations;
  private double cornerDuration; // Time to traverse the curved corner
  private int previousSegment = -1;

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
    cornerRotation = new TransformNode("translate corner", Mat4Transform.translate(0,0,0));
    rotationRoot = new TransformNode("rotation root", Mat4Transform.rotateAroundY(0));
    rotateSpotlight = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateRoot);
      translateRoot.addChild(cornerRotation);
        cornerRotation.addChild(baseRotation);
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
    int numPoints = anchorPoints.length;
    straightDurations = new double[numPoints];
    totalPathTime = 0;

    for (int i = 0; i < numPoints; i++) {
      Vec2 p1 = anchorPoints[i];
      Vec2 p2 = anchorPoints[(i + 1) % numPoints]; // Loop back to start
      double distance = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
      straightDurations[i] = (distance - radius * Math.PI) / speed; // Adjust for curved corners
      totalPathTime += straightDurations[i];
    }

    cornerDuration = (Math.PI * radius) / speed; // Time to traverse a quarter-circle
    totalPathTime += cornerDuration * anchorPoints.length;
  }




  private Vec2 linearInterpolate(Vec2 start, Vec2 end, float t) {
    return new Vec2(
            start.x + (end.x - start.x) * t,
            start.y + (end.y - start.y) * t
    );
  }
  private Vec2 linearInterpolate(Vec2 start, Vec2 end, double t) {return linearInterpolate(start, end, (float)t);}


  public void updateRailwayPosition(double elapsedTime) {
    double timeInCycle = elapsedTime % totalPathTime; // Loop time
    double accumulatedTime = 0;

    for (int i = 0; i < straightDurations.length; i++) {
      Vec2 current = anchorPoints[i];
      Vec2 next = anchorPoints[(i + 1) % anchorPoints.length];
      Vec2 cornerCenter = calculateCornerCenter(current, next, anchorPoints[(i + 2) % anchorPoints.length]);

      // Straight segment
      if (timeInCycle < accumulatedTime + straightDurations[i]) {
        double segmentProgress = (timeInCycle - accumulatedTime) / straightDurations[i];
        translateRoot.setTransform(translateXZ(linearInterpolate(current, next, segmentProgress)));
        return;
      }
      accumulatedTime += straightDurations[i];

      // Curved corner segment
      if (timeInCycle < accumulatedTime + cornerDuration) {
        double arcProgress = (timeInCycle - accumulatedTime) / cornerDuration;
        cornerRotation.setTransform(translateXZ(traverseArc(cornerCenter, current, next, arcProgress)));
        return;
      }
      accumulatedTime += cornerDuration;
    }
  }

  private Vec2 calculateCornerCenter(Vec2 prev, Vec2 current, Vec2 next) {
    // Find the center of the circular arc between two straight segments
    Vec2 direction1 = new Vec2(current.y - prev.y, prev.x - current.x);
    direction1.normalize(); // Perpendicular
    Vec2 direction2 = new Vec2(next.y - current.y, current.x - next.x); // Perpendicular
    direction2.normalize();
    Vec2 midpoint1 = new Vec2((prev.x + current.x) / 2, (prev.y + current.y) / 2);
    Vec2 midpoint2 = new Vec2((current.x + next.x) / 2, (current.y + next.y) / 2);
    // Solve for intersection of the two perpendicular lines to get the center
    return intersectLines(midpoint1, direction1, midpoint2, direction2);
  }

  private Vec2 traverseArc(Vec2 center, Vec2 start, Vec2 end, double progress) {
    double startAngle = Math.atan2(start.y - center.y, start.x - center.x);
    double endAngle = Math.atan2(end.y - center.y, end.x - center.x);
    double angle = startAngle + (endAngle - startAngle) * progress;

    rotationRoot.setTransform(Mat4Transform.rotateAroundY((float)Math.toDegrees(angle))); // Call rotate() with the current angle

    return new Vec2(
            (float)(center.x + radius * Math.cos(angle)),
            (float)(center.y + radius * Math.sin(angle))
    );
  }

  private void rotate(double angle) {
    System.out.println("Rotating robot to angle: " + angle);
    // Add rotation logic for the robot here
  }

  // Simulate line intersection
  private Vec2 intersectLines(Vec2 p1, Vec2 dir1, Vec2 p2, Vec2 dir2) {
    double a1 = dir1.y, b1 = -dir1.x, c1 = a1 * p1.x + b1 * p1.y;
    double a2 = dir2.y, b2 = -dir2.x, c2 = a2 * p2.x + b2 * p2.y;
    double det = a1 * b2 - a2 * b1;

    if (Math.abs(det) < 1e-6) return p1; // Parallel lines, fallback
    return new Vec2(
            (float)((b2 * c1 - b1 * c2) / det),
            (float)((a1 * c2 - a2 * c1) / det)
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