package tooling;

import gmaths.*;
import java.nio.*;
import java.util.function.Supplier;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/**
 * This class encapuslates rendering a model
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 *
 * It is a combination of lab code and my code
 * I declare that any sections marked as my code are wholly my own work
 */

public class Light implements ILight {

  //lab code
  private Material material;

  private Mat4 model;
  private Shader shader;
  private Camera camera;

  //my code
  private Supplier<Vec3> positionGetter;
  private Vec3 ambient, diffuse, specular;

  //my code
  private boolean enabled = true;

  //my code
  public Vec3 getAmbient() {
    return new Vec3(ambient);
  }
  public Vec3 getDiffuse() {
    return new Vec3(diffuse);
  }
  public Vec3 getSpecular() {
    return new Vec3(specular);
  }

  //my code
  public Light(GL3 gl, Supplier<Vec3> position, Vec3 ambient, Vec3 diffuse, Vec3 specular) {
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.positionGetter = position;
    model = new Mat4(1);
    shader = new Shader(gl, "assets/shaders/vs_light_01.glsl", "assets/shaders/fs_light_01.glsl");
    fillBuffers(gl);
  }

  //my code
  static final Vec3 defaultAmbient = new Vec3(0.5f, 0.5f, 0.5f);
  static final Vec3 defaultDiffuse = new Vec3(0.8f, 0.8f, 0.8f);
  static final Vec3 defaultSpecular = new Vec3(0.8f, 0.8f, 0.8f);

  //my code
  public Light(GL3 gl, Supplier<Vec3> position) {
    this(
            gl,
            position,
            defaultAmbient,
            defaultDiffuse,
            defaultSpecular
    );
  }

  //lab code
  public Vec3 getPosition() {
    return positionGetter.get();
  }

  //my code
  public void enable() { enabled = true; }
  public void disable() { enabled = false; }
  public boolean isEnabled() { return enabled;}

  //lab code
  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  //lab code
  public void render(GL3 gl) {
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
    model = Mat4.multiply(Mat4Transform.translate(getPosition()), model);
    
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));
    
    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

    // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
    //lab code
    private float[] vertices = new float[] {  // x,y,z
      -0.5f, -0.5f, -0.5f,  // 0
      -0.5f, -0.5f,  0.5f,  // 1
      -0.5f,  0.5f, -0.5f,  // 2
      -0.5f,  0.5f,  0.5f,  // 3
       0.5f, -0.5f, -0.5f,  // 4
       0.5f, -0.5f,  0.5f,  // 5
       0.5f,  0.5f, -0.5f,  // 6
       0.5f,  0.5f,  0.5f   // 7
     };
    
    private int[] indices =  new int[] {
      0,1,3, // x -ve 
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      1,5,7, // z +ve
      7,3,1, // z +ve
      6,4,0, // z -ve
      0,2,6, // z -ve
      0,4,5, // y -ve
      5,1,0, // y -ve
      2,3,7, // y +ve
      7,6,2  // y +ve
    };

  //lab code
  private int vertexStride = 3;
  private int vertexXYZFloats = 3;
  
  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  //lab code
  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];

  //lab code
  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
     
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    //gl.glBindVertexArray(0);
  } 

}