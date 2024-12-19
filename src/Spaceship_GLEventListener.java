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

public class Spaceship_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;

  //lab code
  public Spaceship_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));

    lights = new Light[2];
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
  //lab code
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    for (ILight light : lights) {
      light.dispose(gl);
    }
    //my code
    room.dispose(gl);
    robotOne.dispose(gl);
    robotTwo.dispose(gl);
    globe.dispose(gl);
  }
  

  // ***************************************************
  /* INTERACTION
   *
   *
   */
  //lab code
  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
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
  private Skybox skybox;
  private Room room;
  private ILight[] lights;
  private RobotOne robotOne;
  private RobotTwo robotTwo;
  private Globe globe;
  private List<Runnable> postInitialisationRunners = new ArrayList<Runnable>();

  // this is my code
  private void initialise(GL3 gl) {
    createRandomNumbers();

    textures = new TextureLibrary();
    textures.add(gl, "chequerboard", "assets/textures/chequerboard.jpg");
    textures.add(gl, "jade_diffuse", "assets/textures/jade.jpg");
    textures.add(gl, "jade_specular", "assets/textures/jade_specular.jpg");
    textures.add(gl, "container_diffuse", "assets/textures/container2.jpg");
    textures.add(gl, "container_specular", "assets/textures/container2_specular.jpg");
    textures.add(gl, "watt_diffuse", "assets/textures/wattBook.jpg");
    textures.add(gl, "watt_specular", "assets/textures/wattBook_specular.jpg");
    textures.add(gl, "watt", "assets/textures/wattBook.jpg");
    textures.add(gl, "window", "assets/textures/window.png");
    textures.add(gl, "bridge", "assets/textures/bridge.jpg");
    textures.add(gl, "arrow", "assets/textures/arrow.png");
    textures.add(gl, "asphalt", "assets/textures/asphalt1.jpg");
    textures.add(gl, "floor", "assets/textures/floor.jpg");
    textures.add(gl, "alexander_diffuse", "assets/textures/alexander_diffuse.jpg");
    textures.add(gl, "alexander_specular", "assets/textures/alexander_specular.jpg");
    textures.addRepeating(gl, "repeating_small", "assets/textures/repeating_small.jpg");

    shapes = new Shapes(camera, lights);

    skybox = new Skybox(gl);
    // floor


    room = new Room(gl, camera, lights, textures);
    
    robotOne = new RobotOne(gl, shapes, textures);

    robotTwo = new RobotTwo(gl, shapes, textures);

    lights[0] = new PointLight(gl, this::getPointLightPosition);
    lights[0].setCamera(camera);

    lights[1] = new SpotLight(
            gl,
            robotTwo::getSpotlightPosition,
            robotTwo::getSpotlightDirection
            );
    lights[1].setCamera(camera);

    globe = new Globe(gl, shapes, textures);

    postInitialisationRunners.forEach(Runnable::run);
  }

  public void addPostInitRunner(Runnable r) { postInitialisationRunners.add(r); }
 
  // animation control of start stop is poor and needs improving

  //this is my code
  private void render(GL3 gl) {
    double elapsedTime = getSeconds()-startTime;
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    skybox.render(gl, camera.getViewMatrix(), camera.getPerspectiveMatrix(), elapsedTime);
    lights[0].render(gl);
    room.render(gl);

    if (animation) {
      robotOne.updateAnimation(elapsedTime);
    }
    robotOne.render(gl);
    robotTwo.render(gl, elapsedTime);
    globe.render(gl, elapsedTime);
  }

  public ILight[] getLights() {return lights;}

  // this is lab code
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getPointLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */

  // this is lab code
  private int NUM_RANDOMS = 1000;
  private float[] randoms;

  // this is lab code
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}