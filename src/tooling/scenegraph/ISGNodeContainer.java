package tooling.scenegraph;

/**
 * This interface defines a container for SGNode to allow other classes to implement the functionality
 *
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that this code is my own work

 */
public interface ISGNodeContainer {
    SGNode getNode();
    void addChild(SGNode node);
}
