import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import gmaths.Vec3;
import tooling.Camera;
import tooling.ILight;

/**
 * This class runs the main window
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 *
 * It is a combination of lab code and my code
 * I declare that any sections marked as my code are wholly my own work
 */

//this is my code
public class Spaceship extends JFrame {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private final Spaceship_GLEventListener glEventListener;
  private final FPSAnimator animator; 
  private Camera camera;

  JCheckBox alwaysDancing, robotTwoMoving;

  public static void main(String[] args) {
    Spaceship window = new Spaceship("Please watch WALL-E - it's a beautiful film");
    window.getContentPane().setPreferredSize(dimension);
    ImageIcon icon = new ImageIcon("assets/textures/icon.png");
    window.setIconImage(icon.getImage());
    window.pack();
    window.setVisible(true);
  }

  public Spaceship(String textForTitleBar) {
    super(textForTitleBar);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 300);

    // move monitor to window 2 so it doesnt hide my code
    final boolean iAmDebuggingMyCode = false;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] devices = ge.getScreenDevices();
    if (iAmDebuggingMyCode && devices.length > 1) {
      // monitor 2
      GraphicsDevice secondMonitor = devices[1];
      Rectangle bounds = secondMonitor.getDefaultConfiguration().getBounds();
      setLocation(bounds.x + 50, bounds.y + 50);
    } else {
      System.out.println("Only one monitor detected.");
    }


    //this is lab code
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    camera = new Camera(new Vec3(3.5f,7.9f,25), new Vec3(-2,2,0), Camera.DEFAULT_UP);
    glEventListener = new Spaceship_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MouseControls(camera));
    canvas.addKeyListener(new KeyboardControls(camera));
    getContentPane().add(canvas, BorderLayout.CENTER);
    
    JMenuBar menuBar=new JMenuBar();
    this.setJMenuBar(menuBar);
      JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this::quit_click);
        fileMenu.add(quitItem);
      JMenu helpMenu = new JMenu("Help");
        JMenuItem attributionsItem = new JMenuItem("Attributions");
        JMenuItem aboutItem = new JMenuItem("About");
        attributionsItem.addActionListener(this::attributions_click);
        aboutItem.addActionListener(this::quit_click);
        helpMenu.add(attributionsItem);
        helpMenu.add(aboutItem);
    menuBar.add(fileMenu);
    menuBar.add(helpMenu);

    //this is my code
    
    JPanel bottomPanel = new JPanel();
    JLabel label = new JLabel("Camera", JLabel.LEFT);
    bottomPanel.add(label);

    JButton cameraToX = new JButton("X");
    cameraToX.addActionListener(this::cameraX_click);
    bottomPanel.add(cameraToX);

    JButton cameraToY = new JButton("Y");
    cameraToY.addActionListener(this::cameraY_click);
    bottomPanel.add(cameraToY);

    JButton cameraToZ = new JButton("Z");
    cameraToZ.addActionListener(this::cameraZ_click);
    bottomPanel.add(cameraToZ);

    JButton cameraToXYZ = new JButton("XYZ");
    cameraToXYZ.addActionListener(this::cameraXYZ_click);
    bottomPanel.add(cameraToXYZ);

    JPanel lightControlPanel = new JPanel();
    glEventListener.addPostInitRunner(() -> {
      for (ILight light : glEventListener.getLights()) {
        JLabel lightLabel = new JLabel(light.getDisplayName() + ":");
        lightLabel.setBounds(10, 10, 100, 20);
        JSlider lightSlider = new JSlider(0, 100, 50); // Range: 0-100, Default: 50
        lightSlider.setBounds(120, 10, 150, 20);
        final float multiplier = 0.01f;
        lightSlider.setValue(Math.round(light.getScaleFactor()/ multiplier));
        lightSlider.addChangeListener((ChangeEvent e) -> {

          light.setScaleFactor(multiplier * lightSlider.getValue());
        });
        lightControlPanel.add(lightLabel);
        lightControlPanel.add(lightSlider);

        lightControlPanel.revalidate();
        lightControlPanel.repaint();
      }
    });
    bottomPanel.add(lightControlPanel);

    JLabel alwaysDancingLabel = new JLabel("force dance:");
    alwaysDancing = new JCheckBox();

    alwaysDancing.addActionListener(this::alwaysDance_change);
    bottomPanel.add(alwaysDancingLabel);
    bottomPanel.add(alwaysDancing);
    glEventListener.addPostInitRunner(() -> {alwaysDancing.setSelected(glEventListener.isAlwaysDancing());});

    JLabel robotTwoMovingLabel = new JLabel("robot 2:");
    robotTwoMoving = new JCheckBox();

    robotTwoMoving.addActionListener(this::robotTwoMoving_change);
    bottomPanel.add(robotTwoMovingLabel);
    bottomPanel.add(robotTwoMoving);
    glEventListener.addPostInitRunner(() -> {robotTwoMoving.setSelected(glEventListener.isMOMoving());});

    this.add(bottomPanel, BorderLayout.SOUTH);
    
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        animator.stop();
        remove(canvas);
        dispose();
        System.exit(0);
      }
    });
    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }

  private void attributions_click(ActionEvent actionEvent) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Attribution");
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(null);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JTextArea textArea = new JTextArea();
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);

    String filePath = "assets/textures/attribution.txt";
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        textArea.append(line + "\n");
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(dialog, "Error reading file: " + e.getMessage(),
              "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Add the scroll pane to the dialog
    dialog.add(scrollPane, BorderLayout.CENTER);

    // Make the dialog visible
    dialog.setVisible(true);
  }

  public void cameraX_click(ActionEvent e) {
    camera.setCamera(Camera.CameraType.X);
    canvas.requestFocusInWindow();
  }
  public void cameraY_click(ActionEvent e) {
    camera.setCamera(Camera.CameraType.Y);
    canvas.requestFocusInWindow();
  }
  public void cameraZ_click(ActionEvent e) {
    camera.setCamera(Camera.CameraType.Z);
    canvas.requestFocusInWindow();
  }
  public void cameraXYZ_click(ActionEvent e) {
    camera.setCamera(Camera.CameraType.XYZ);
    canvas.requestFocusInWindow();
  }
  public void alwaysDance_change(ActionEvent e) {
    glEventListener.setAlwaysDancing(alwaysDancing.isSelected());
  }
  private void robotTwoMoving_change(ActionEvent actionEvent) {
    glEventListener.setMOMoving(robotTwoMoving.isSelected());
  }
  public void quit_click(ActionEvent e) {
    System.exit(0);
  }
  
}

//this is from the lab code
 
class KeyboardControls extends KeyAdapter  {
  private Camera camera;
  
  public KeyboardControls(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

//lab code
class MouseControls extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MouseControls(Camera camera) {
    this.camera = camera;
  }

  //lab code
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }
}