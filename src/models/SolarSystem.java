package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4Transform;
import tooling.Camera;
import tooling.Model;
import tooling.scenegraph.NameNode;
import tooling.scenegraph.SGNode;
import tooling.scenegraph.TransformNode;

/**
 * This class encapsulates the globe scene graph
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class SolarSystem {

  private TextureLibrary textures;

  private Camera camera;
  private Model sunSphere, earthSphere, moonSphere;
  private SGNode root;

  private TransformNode sunRotate, earthOrbit, earthRotate, moonOrbit, moonRotate;

  public SolarSystem(GL3 gl, Shapes shapes, TextureLibrary textures) {
    sunSphere = shapes.makeSphere(gl, textures.get("sun"), textures.get("sun"));
    earthSphere = shapes.makeSphere(gl, textures.get("earth"), textures.get("earth"));
    moonSphere = shapes.makeSphere(gl, textures.get("moon"), textures.get("moon"));

    root = new NameNode("globe root");

    Branch sun = new Branch(sunSphere, 4f, 4f, 4f);
    Branch earth = new Branch(earthSphere, 2f,2f,2f);
    Branch moon = new Branch(moonSphere, 0.5f,0.5f,0.5f);

    TransformNode translateSystem = new TransformNode("translate base", Mat4Transform.translate(-30, 7, -20));

    TransformNode translateToCentreOfSun = new TransformNode("centre of sun", Mat4Transform.translate(0, sun.scaleY / 2, 0));
    TransformNode translateIntoEarthOrbit = new TransformNode("translate",Mat4Transform.translate(10f,0,0));
    TransformNode translateIntoCentreOfEarth = new TransformNode("centre of earth", Mat4Transform.translate(0, earth.scaleY / 2, 0));

    TransformNode translateIntoMoonOrbit = new TransformNode("translate",Mat4Transform.translate(2f,0,0));

    sunRotate = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));
    earthOrbit = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));
    earthRotate = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));

    moonOrbit = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));
    moonRotate = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateSystem);
      translateSystem.addChild(sunRotate);
        sunRotate.addChild(sun);

      translateSystem.addChild(translateToCentreOfSun);
        translateToCentreOfSun.addChild(earthOrbit);
          earthOrbit.addChild(translateIntoEarthOrbit);
            translateIntoEarthOrbit.addChild(earthRotate);
              earthRotate.addChild(earth);
                earth.addChild(translateIntoCentreOfEarth);
                  translateIntoCentreOfEarth.addChild(moonOrbit);
                    moonOrbit.addChild(translateIntoMoonOrbit);
                      translateIntoMoonOrbit.addChild(moonRotate);
                        moonRotate.addChild(moon);

    root.update();

  }

  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }


  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;

    sunRotate.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));
    earthOrbit.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));
    earthRotate.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));

    moonOrbit.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));
    moonRotate.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));

    root.update();
  }

  public void dispose(GL3 gl) {
    earthSphere.dispose(gl);
  }
}