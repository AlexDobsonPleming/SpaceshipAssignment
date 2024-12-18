package tooling.scenegraph;

import com.jogamp.opengl.*;
import tooling.Model;

/**
 * This class encapsulates a TransformNode
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (15/10/2017)
 */
public class ModelNode extends SGNode {

  protected Model model;

  public ModelNode(String name, Model m) {
    super(name);
    model = m; 
  }

  public void draw(GL3 gl) {
    model.render(gl, worldTransform);
    for (SGNode child : children) {
      child.draw(gl);
    }
  }

}