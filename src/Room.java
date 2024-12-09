import gmaths.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

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
  private Object windowWall;
  private Surface oppositeWall;

  private float floorToCeilingHeight = 10f;
   
  public Room(GL3 gl, float xSize, float zSize, Camera cameraIn, Light lightIn, Texture texture1) {


    floor = new Surface(gl, xSize, zSize, cameraIn, lightIn, texture1, Mat4Transform.translate(new Vec3()));

    Mat4 ceilingTranslate = Mat4Transform.translate(new Vec3(0, floorToCeilingHeight, 0));
    Mat4 ceilingRotate = Mat4Transform.rotateAroundX(180);
    Mat4 ceilingTransform = Mat4.multiply(ceilingTranslate, ceilingRotate);

    ceiling = new Surface(gl, xSize, zSize, cameraIn, lightIn, texture1, ceilingTransform);

    Mat4 rearWallRotate = Mat4Transform.rotateAroundX(90);
    Mat4 rearWallRaise = Mat4Transform.translate(new Vec3(0, 0, -1 * floorToCeilingHeight / 2));
    Mat4 rearWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize / 2, 0));
    Mat4 rearWallTransform = Mat4.multiply(Mat4.multiply(rearWallRotate, rearWallRaise), rearWallPushBack);
    rearWall = new Surface(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, texture1, rearWallTransform);

    Mat4 oppositeWallRotate = Mat4.multiply(Mat4Transform.rotateAroundZ(90), Mat4Transform.rotateAroundY(90));
    Mat4 oppositeWallRaise = Mat4Transform.translate(new Vec3(0, 0, floorToCeilingHeight / 2));
    Mat4 oppositeWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * xSize / 2, 0));
    Mat4 oppositeWallTransform = Mat4.multiply(Mat4.multiply(oppositeWallRotate, oppositeWallRaise), oppositeWallPushBack);
    oppositeWall = new Surface(gl, xSize, floorToCeilingHeight, cameraIn, lightIn, texture1, oppositeWallTransform);
  }

  public void render(GL3 gl) {

    floor.render(gl);
    ceiling.render(gl);
    rearWall.render(gl);
    oppositeWall.render(gl);
  }

}