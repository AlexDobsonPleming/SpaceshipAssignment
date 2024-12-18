package tooling.scenegraph;

import gmaths.*;
import java.util.ArrayList;
import com.jogamp.opengl.*;

/**
 * This class encapsulates a TransformNode
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 */

public class SGNode {

  protected String name;
  protected ArrayList<SGNode> children;
  protected Mat4 worldTransform;

  public SGNode(String name) {
    children = new ArrayList<SGNode>();
    this.name = name;
    worldTransform = new Mat4(1);
  }

  public void addChild(SGNode child) {
    children.add(child);
  }

  public void addChild(ISGNodeContainer child) {
    children.add(child.getNode());
  }
  
  public void update() {
    update(worldTransform);
  }
  
  protected void update(Mat4 t) {
    worldTransform = t;
    for (SGNode child : children) {
      child.update(t);
    }
  }

  protected String getIndentString(int indent) {
    String s = ""+indent+" ";
    for (int i=0; i<indent; ++i) {
      s+="  ";
    }
    return s;
  }
  
  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent)+"Name: "+name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
    }
    for (int i=0; i<children.size(); i++) {
      children.get(i).print(indent+1, inFull);
    }
  }
  
  public void draw(GL3 gl) {
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }

  public Vec3 getPosition() {
    return new Vec3(worldTransform.get(0, 3),
            worldTransform.get(1, 3),
            worldTransform.get(2, 3));
  }

  public Vec3 getDirection() {
    // Assuming forward direction corresponds to the z-axis
    Vec3 returnValue = new Vec3(worldTransform.get(0, 2),
            worldTransform.get(1, 2),
            worldTransform.get(2, 2));
    returnValue.normalize();
    return returnValue;
  }

}