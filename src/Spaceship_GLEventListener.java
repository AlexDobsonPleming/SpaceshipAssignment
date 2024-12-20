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
  ///my code
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    room.dispose(gl);
    robotOne.dispose(gl);
    robotTwo.dispose(gl);
    globe.dispose(gl);
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
    textures = new TextureLibrary();
    textures.add(gl, "container_diffuse", "assets/textures/container2.jpg");
    textures.add(gl, "container_specular", "assets/textures/container2_specular.jpg");
    textures.add(gl, "window", "assets/textures/window.png");
    textures.add(gl, "bridge", "assets/textures/bridge.jpg");
    textures.add(gl, "arrow", "assets/textures/arrow.png");
    textures.add(gl, "asphalt", "assets/textures/asphalt1.jpg");
    textures.add(gl, "floor", "assets/textures/floor.jpg");
    textures.add(gl, "alexander_diffuse", "assets/textures/alexander_diffuse.jpg");
    textures.add(gl, "alexander_specular", "assets/textures/alexander_specular.jpg");
    textures.addRepeating(gl, "repeating_small", "assets/textures/repeating_small.png");
    textures.add(gl, "globe", "assets/textures/globe.png");
    textures.add(gl, "globe_specular", "assets/textures/globe_specular.png");
    textures.add(gl, "wood", "assets/textures/wood.jpg");
    textures.add(gl, "wood_specular", "assets/textures/wood.jpg");
    textures.add(gl, "auto_face", "assets/textures/autoFace.png");
    textures.add(gl, "auto_face_plant", "assets/textures/auto_face_plant.png");
    textures.add(gl, "auto_face_plant_inverted", "assets/textures/auto_face_plant_inverted.png");
    textures.add(gl, "auto_ring", "assets/textures/auto_ring.png");
    textures.add(gl, "handle", "assets/textures/handle.png");
    textures.add(gl, "auto_default", "assets/textures/auto_default.png");
    textures.add(gl, "auto_default_specular", "assets/textures/auto_default_specular.png");
    textures.add(gl, "auto_body", "assets/textures/auto_body.png");
    textures.add(gl, "auto_base", "assets/textures/auto_base.png");
    textures.add(gl, "mo_eye", "assets/textures/mo_eye.png");
    textures.add(gl, "bulb_housing", "assets/textures/bulb_housing.png");

    shapes = new Shapes(camera, lights);

    skybox = new Skybox(gl);
    // floor


    room = new Room(gl, camera, lights, textures);
    
    robotOne = new RobotOne(gl, shapes, textures);

    robotTwo = new RobotTwo(gl, shapes, textures);
    robotTwo.setRobotOneLocationSupplier(robotOne::getLocation);
    robotOne.setDancingSupplier(robotTwo::isCloseToRobotOne);

    lights[0] = room.getRoomLight();
    lights[0].setCamera(camera);

    lights[1] = robotTwo.getSpotLight();
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

    robotOne.render(gl, elapsedTime);
    robotTwo.render(gl, elapsedTime);
    globe.render(gl, elapsedTime);
  }

  public boolean isAlwaysDancing() {
    return robotOne.isAlwaysDancing();
  }
  public void setAlwaysDancing(boolean alwaysDancing) {
    this.robotOne.setAlwaysDancing(alwaysDancing);
  }

  public boolean isMOMoving() {
    return robotTwo.isMoving();
  }
  public void setMOMoving(boolean moving) {
    this.robotTwo.setMoving(moving);
  }

  public ILight[] getLights() {return lights;}


  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  
}