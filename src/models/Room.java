package models;

import gmaths.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import tooling.Camera;
import tooling.Light;

/**
 * This class stores the Floor
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Room {

  private Surface floor;
  private Surface ceiling;
  private Surface rearWall;
  private Window windowWall;
  private Surface wallBehindWindowTest;
  private Surface oppositeWall;

  private float floorToCeilingHeight = 10f;
   
  public Room(GL3 gl, float xSize, float zSize, Camera cameraIn, Light lightIn, TextureLibrary textures) {
    Texture chequerboard = textures.get("chequerboard");

    floor = new Surface(gl, xSize, zSize, cameraIn, lightIn, chequerboard, Mat4Transform.translate(new Vec3()));

    Mat4 ceilingTranslate = Mat4Transform.translate(new Vec3(0, floorToCeilingHeight, 0));
    Mat4 ceilingRotate = Mat4Transform.rotateAroundX(180);
    Mat4 ceilingTransform = Mat4.multiply(ceilingTranslate, ceilingRotate);

    ceiling = new Surface(gl, xSize, zSize, cameraIn, lightIn, chequerboard, ceilingTransform);

    Mat4 rearWallRotate = Mat4Transform.rotateAroundX(90);
    Mat4 rearWallRaise = Mat4Transform.translate(new Vec3(0, 0, -1 * floorToCeilingHeight / 2));
    Mat4 rearWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize / 2, 0));
    Mat4 rearWallTransform = Mat4.multiply(Mat4.multiply(rearWallRotate, rearWallRaise), rearWallPushBack);
    rearWall = new Surface(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, chequerboard, rearWallTransform);

    Texture window = textures.get("window");
    Mat4 windowWallRotate = Mat4.multiply(Mat4Transform.rotateAroundZ(270), Mat4Transform.rotateAroundY(90));
    Mat4 windowWallRaise = Mat4Transform.translate(new Vec3(0, 0, -1 * floorToCeilingHeight / 2));
    Mat4 windowWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize / 2, 0));
    Mat4 windowWallTransform = Mat4.multiply(Mat4.multiply(windowWallRotate, windowWallRaise), windowWallPushBack);
    windowWall = new Window(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, window, windowWallTransform);

    Texture watt = textures.get("watt");
    Mat4 wattWallRotate = Mat4.multiply(Mat4Transform.rotateAroundZ(270), Mat4Transform.rotateAroundY(90));
    Mat4 wattWallRaise = Mat4Transform.translate(new Vec3(0, 0, -1 * floorToCeilingHeight / 2));
    Mat4 wattWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize, 0));
    Mat4 wattWallTransform = Mat4.multiply(Mat4.multiply(wattWallRotate, wattWallRaise), wattWallPushBack);
    wallBehindWindowTest = new Surface(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, watt, wattWallTransform);

    Mat4 oppositeWallRotate = Mat4.multiply(Mat4Transform.rotateAroundZ(90), Mat4Transform.rotateAroundY(90));
    Mat4 oppositeWallRaise = Mat4Transform.translate(new Vec3(0, 0, floorToCeilingHeight / 2));
    Mat4 oppositeWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize / 2, 0));
    Mat4 oppositeWallTransform = Mat4.multiply(Mat4.multiply(oppositeWallRotate, oppositeWallRaise), oppositeWallPushBack);
    oppositeWall = new Surface(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, chequerboard, oppositeWallTransform);
  }

  public void render(GL3 gl) {

    floor.render(gl);
    ceiling.render(gl);
    rearWall.render(gl);
    windowWall.render(gl);
    oppositeWall.render(gl);
    wallBehindWindowTest.render(gl);
  }

}