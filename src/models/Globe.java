package models;

import com.jogamp.opengl.GL3;
import gmaths.Mat4Transform;
import tooling.*;
import tooling.scenegraph.*;

/**
 * This class encapsulates the globe scene graph
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

public class Globe {

  private TextureLibrary textures;

  private Camera camera;
  private Model globeSphere, pedestalSphere;
  private Model baseCube;
  private SGNode root;

  private TransformNode globeRotate;

  public Globe(GL3 gl, Shapes shapes, TextureLibrary textures) {

    globeSphere = shapes.makeSphere(gl, textures.get("globe"), textures.get("globe_specular"));
    pedestalSphere = shapes.makeSphere(gl, textures.get("wood"), textures.get("wood_specular"));
    baseCube = shapes.makeCube(gl, textures.get("wood"), textures.get("jade_specular"));

    root = new NameNode("globe root");

    Branch base = new Branch(baseCube, 2f, 2f, 2f);
    Branch pedestal = new Branch(pedestalSphere, 0.2f,3.4f,0.2f);
    Branch globe = new Branch(globeSphere, 2f,2f,2f);

    TransformNode translateBase = new TransformNode("translate base", Mat4Transform.translate(3, 0, 8));

    TransformNode translateAboveBase = new TransformNode("translate above base", Mat4Transform.translate(0, base.scaleY, 0));
    TransformNode sinkIntoBaseSlightly = new TransformNode("sink into base", Mat4Transform.translate(0, -0.4f, 0));
    TransformNode translateToTopOfPedestal = new TransformNode("translate(0,"+ 0.5f +",0)",Mat4Transform.translate(0,1f,0));


    globeRotate = new TransformNode("rotateAroundY", Mat4Transform.rotateAroundY(0));


    root.addChild(translateBase);
    translateBase.addChild(base);
        base.addChild(translateAboveBase);
          translateAboveBase.addChild(sinkIntoBaseSlightly);
            sinkIntoBaseSlightly.addChild(pedestal);
              pedestal.addChild(translateToTopOfPedestal);
                translateToTopOfPedestal.addChild(globeRotate);
                  globeRotate.addChild(globe);

      root.update();

  }

  public void render(GL3 gl, double elapsedTime) {
    updateAnimation(elapsedTime);
    root.draw(gl);
  }


  public void updateAnimation(double elapsedTime) {
    float angularVelocity = 45.0f;

    globeRotate.setTransform(Mat4Transform.rotateAroundY(-1 * (angularVelocity * (float)elapsedTime) % 360));

    root.update();
  }

  public void dispose(GL3 gl) {
    baseCube.dispose(gl);
    pedestalSphere.dispose(gl);
    globeSphere.dispose(gl);
  }
}