package models.meshes;

/**
 * This class handles importing texture. A combination of lab code and my own
 *
 * @author    Dr Steve Maddock
 * @email     s.maddock@sheffield.ac.uk
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that the code marked as my own is my own work
 */


public final class TwoTriangles implements IMesh {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  //lab code
  public static final float[] vertices = {      // position, colour, tex coords
    -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
    -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
     0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
     0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
  };

  //lab code
  public static final int[] indices = {         // Note that we start from 0!
      0, 1, 2,
      0, 2, 3
  };

  //my code
  @Override
  public float[] getVertices() {
    return vertices;
  }

  //my code
  @Override
  public int[] getIndices() {
    return indices;
  }
}