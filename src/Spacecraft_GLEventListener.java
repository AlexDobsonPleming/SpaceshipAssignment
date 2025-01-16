import gmaths.*;

import com.jogamp.opengl.*;
import models.*;
import tooling.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class captures OpenGL events for the spaceship canvas
 * it's a combination of lab clas code and my code
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 *  @author    Dr Steve Maddock
 *  @email     s.maddock@sheffield.ac.uk
 * I declare that the sections marked as my code are my own work
 */

public class Spacecraft_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;

  //lab code
  public Spacecraft_GLEventListener(Camera camera) {
    this.camera = camera;

    lights = new Light[1];
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  //lab code
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  //lab code
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  //lab code
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  ///my code
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    room.dispose(gl);
  }
  


  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // this is my code
  private TextureLibrary textures;
  private Shapes shapes;

  private Camera camera;
  private Room room;
  private ILight[] lights;
  private List<Runnable> postInitialisationRunners = new ArrayList<Runnable>();

  // this is my code
  private void initialise(GL3 gl) {
    textures = new TextureLibrary();
    textures.add(gl, "floor", "assets/textures/limestone.jpg");
    textures.add(gl, "limestone", "assets/textures/limestoneBaked.png");
    textures.add(gl, "planks", "assets/textures/woodBaked.png");
    textures.addRepeating(gl, "repeating_small", "assets/textures/repeating_small.png");

    shapes = new Shapes(camera, lights);

    room = new Room(gl, camera, lights, textures);

    lights[0] = room.getRoomLight();
    lights[0].setCamera(camera);


    postInitialisationRunners.forEach(Runnable::run);
  }

  public void addPostInitRunner(Runnable r) { postInitialisationRunners.add(r); }
 
  // animation control of start stop is poor and needs improving

  //this is my code
  private void render(GL3 gl) {
    double elapsedTime = getSeconds()-startTime;
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    lights[0].render(gl);
    room.render(gl);

  }


  public ILight[] getLights() {return lights;}


  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  
}