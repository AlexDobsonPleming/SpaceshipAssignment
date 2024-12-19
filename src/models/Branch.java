package models;

import gmaths.Mat4;
import gmaths.Mat4Transform;
import tooling.Model;
import tooling.scenegraph.*;

/**
 * This class handles creating branches for use in scene graphs.
 * It includes default transformations for sphere and square models to ensure they're transformed correctly
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work
 */

class Branch implements ISGNodeContainer {
    SGNode node;

    public float scaleX;
    public float scaleY;
    public float scaleZ;

    public Branch(Model model, float sx, float sy, float sz) {
        scaleX = sx;
        scaleY = sy;
        scaleZ = sz;

        node = new NameNode("branch");
        Mat4 m = Mat4Transform.scale(scaleX, scaleY, scaleZ);
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
        TransformNode upperBranch = new TransformNode("scale(" + sx + "," + sy + "," + sz + ");translate(0,0.5,0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", model);
        node.addChild(upperBranch);
        upperBranch.addChild(sphereNode);
    }

    public SGNode getNode() {
        return node;
    }

    public void addChild(ISGNodeContainer child) {
        node.addChild(child);
    }
}
