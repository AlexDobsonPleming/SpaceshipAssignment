package models;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.jogamp.opengl.*;
//import com.jogamp.opengl.util.texture.spi.JPEGImage;

import com.jogamp.opengl.util.texture.*;

/**
 * This class handles importing texture. A combination of lab code and my own
 *
 * @author    Dr Steve Maddock
 * @email     s.maddock@sheffield.ac.uk
 * @author    Alex Dobson-Pleming
 * @email     adobson-pleming1@sheffield.ac.uk
 * I declare that the code marked as my own is my own work
 */

public class TextureLibrary {

  //lab code
  private Map<String,Texture> textures;

  //lab code
  public TextureLibrary() {
    textures = new HashMap<String, Texture>();
  }

  //lab code
  public void add(GL3 gl, String name, String filename) {
    Texture texture = loadTexture(gl, filename);
    textures.put(name, texture);
  }

  //my code
  public void addRepeating(GL3 gl, String name, String filename) {
    Texture texture = loadRepeatingTexture(gl, filename);
    textures.put(name, texture);
  }

  //lab code
  public Texture get(String name) {
    return textures.get(name);
  }

  // my code
  public Texture loadTextureBase(GL3 gl3, String filename, Consumer<Texture> setTextParams) {
    Texture t = null; 
    try {
      File f = new File(filename);
      t = TextureIO.newTexture(f, true);
	    t.bind(gl3);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      setTextParams.accept(t);

    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename); 
    }
    return t;
  }

  //my code
  public Texture loadTexture(GL3 gl3, String filename) {
    return loadTextureBase(gl3, filename, (Texture t) -> {
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
    });
  }

  //my code
  public Texture loadRepeatingTexture(GL3 gl3, String filename) {
    return loadTextureBase(gl3, filename, (Texture t) -> {
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
    });
  }

  //lab code
  // mip-mapping is included in the below example
  /*public static Texture loadTexture(GL3 gl3, String filename) {
    Texture t = null; 
    try {
      File f = new File(filename);
      t = (Texture)TextureIO.newTexture(f, true);
      t.bind(gl3);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE); 
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      gl3.glGenerateMipmap(GL3.GL_TEXTURE_2D);
    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename); 
    }
    return t;
  }
*/

  //lab code
  public void destroy(GL3 gl3) {
    for (var entry : textures.entrySet()) {
      entry.getValue().destroy(gl3);
    }
  }
}