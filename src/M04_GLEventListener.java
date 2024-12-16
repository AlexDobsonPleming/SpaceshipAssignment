import gmaths.*;

import com.jogamp.opengl.*;
import models.*;
import tooling.Camera;
import tooling.Light;

import java.awt.*;

public class M04_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  
  public M04_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
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
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    for (Light light : lights) {
      light.dispose(gl);
    }
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
   
  public void incXPosition() {
    robotOne.incXPosition();
  }
   
  public void decXPosition() {
    robotOne.decXPosition();
  }
  
  public void loweredArms() {
    stopAnimation();
//    robot.loweredArms();
  }
   
  public void raisedArms() {
    stopAnimation();
//    robot.raisedArms();
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  // textures
  private TextureLibrary textures;
  private Shapes shapes;

  private Camera camera;
  private Skybox skybox;
  private Room room;
  private Light[] lights;
  private RobotOne robotOne;
  private RobotTwo robotTwo;
  private Globe globe;


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

    lights = new Light[1];
    lights[0] = new Light(gl);
    lights[0].setCamera(camera);

    shapes = new Shapes(camera, lights);

    skybox = new Skybox(gl);
    // floor


    room = new Room(gl, 16f,16f, camera, lights, textures);
    
    robotOne = new RobotOne(gl, shapes, textures);

    robotTwo = new RobotTwo(gl, shapes, textures);

    globe = new Globe(gl, shapes, textures);
  }
 
  // animation control of start stop is poor and needs improving
  
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    skybox.render(gl, camera.getViewMatrix(), camera.getPerspectiveMatrix());
    lights[0].setPosition(getLightPosition());  // changing light position each frame
    lights[0].render(gl);
    room.render(gl);
    double elapsedTime = getSeconds()-startTime;
    if (animation) {
      robotOne.updateAnimation(elapsedTime);
    }
    robotOne.render(gl);
    robotTwo.render(gl, elapsedTime);
    globe.render(gl, elapsedTime);
  }

  
  // The light's position is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
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
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}