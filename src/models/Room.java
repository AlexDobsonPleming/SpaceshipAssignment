package models;

import gmaths.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import tooling.*;

/**
 * This class encapsulates rendering the room
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class Room {

  private Surface floor;
  private RepeatingSurface oppositeWall;

  private PointLight roomLight;

  private float floorToCeilingHeight = 12f;
  private float floorWidth = 16f;
  private float floorDepth = 32;

  public Room(GL3 gl, Camera cameraIn, ILight[] lights, TextureLibrary textures) {
     roomLight = new PointLight(gl, this::getPointLightPosition);

    Texture asphalt = textures.get("asphalt");



    Texture floorTexture = textures.get("floor");
    floor = new Surface(gl, floorWidth, floorDepth, cameraIn, lights, floorTexture, Mat4Transform.translate(new Vec3()));




    Texture repeating = textures.get("repeating_small");
    Mat4 oppositeWallRotate = Mat4.multiply(Mat4Transform.rotateAroundZ(90), Mat4Transform.rotateAroundY(90));
    Mat4 oppositeWallRaise = Mat4Transform.translate(new Vec3(0, 0, floorToCeilingHeight / 2));
    Mat4 oppositeWallPushBack = Mat4Transform.translate(new Vec3(0, -1 * floorWidth / 2, 0));
    Mat4 oppositeWallRotateAgain = Mat4Transform.rotateAroundY(180);
    Mat4 oppositeWallTransform = Mat4.multiply(Mat4.multiply(Mat4.multiply(oppositeWallRotate, oppositeWallRaise), oppositeWallPushBack), oppositeWallRotateAgain);
    oppositeWall = new RepeatingSurface(gl, floorDepth, floorToCeilingHeight, cameraIn, lights, repeating, oppositeWallTransform);
  }

  // this is lab code
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getPointLightPosition() {
    return new Vec3(0,floorToCeilingHeight * 0.95f,0);
    //return new Vec3(5f,3.4f,5f);
  }

  public void render(GL3 gl) {
    floor.render(gl);
    oppositeWall.render(gl);
  }

  public void dispose(GL3 gl) {
    floor.dispose(gl);
    oppositeWall.dispose(gl);
    roomLight.dispose(gl);
  }

    public PointLight getRoomLight() {
        return roomLight;
    }
}