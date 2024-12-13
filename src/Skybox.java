import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.io.File;
import java.nio.FloatBuffer;

/**
 * This class stores the Floor
 *
 * @author    Dr Steve Maddock
 * @version   1.0 (31/08/2022)
 */

public class Skybox {
  private float[] skyboxVertices = {
          // positions
          -1.0f,  1.0f, -1.0f,
          -1.0f, -1.0f, -1.0f,
          1.0f, -1.0f, -1.0f,
          1.0f, -1.0f, -1.0f,
          1.0f,  1.0f, -1.0f,
          -1.0f,  1.0f, -1.0f,

          -1.0f, -1.0f,  1.0f,
          -1.0f, -1.0f, -1.0f,
          -1.0f,  1.0f, -1.0f,
          -1.0f,  1.0f, -1.0f,
          -1.0f,  1.0f,  1.0f,
          -1.0f, -1.0f,  1.0f,

          1.0f, -1.0f, -1.0f,
          1.0f, -1.0f,  1.0f,
          1.0f,  1.0f,  1.0f,
          1.0f,  1.0f,  1.0f,
          1.0f,  1.0f, -1.0f,
          1.0f, -1.0f, -1.0f,

          -1.0f, -1.0f,  1.0f,
          -1.0f,  1.0f,  1.0f,
          1.0f,  1.0f,  1.0f,
          1.0f,  1.0f,  1.0f,
          1.0f, -1.0f,  1.0f,
          -1.0f, -1.0f,  1.0f,

          -1.0f,  1.0f, -1.0f,
          1.0f,  1.0f, -1.0f,
          1.0f,  1.0f,  1.0f,
          1.0f,  1.0f,  1.0f,
          -1.0f,  1.0f,  1.0f,
          -1.0f,  1.0f, -1.0f,

          -1.0f, -1.0f, -1.0f,
          -1.0f, -1.0f,  1.0f,
          1.0f, -1.0f, -1.0f,
          1.0f, -1.0f, -1.0f,
          -1.0f, -1.0f,  1.0f,
          1.0f, -1.0f,  1.0f
  };

  private Texture loadTexture(GL3 gl3) {
    // Step 1: Create and initialize the cubemap texture
    Texture cubemap = new Texture(GL3.GL_TEXTURE_CUBE_MAP);

    // Step 2: Set texture parameters using the Texture class
    cubemap.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
    cubemap.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
    cubemap.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
    cubemap.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
    cubemap.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);

    // Step 3: Load texture data for each face of the cubemap
    String[] faces = {
            "assets/textures/skybox/right.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_X
            "assets/textures/skybox/left.png",   // GL_TEXTURE_CUBE_MAP_NEGATIVE_X
            "assets/textures/skybox/top.png",    // GL_TEXTURE_CUBE_MAP_POSITIVE_Y
            "assets/textures/skybox/bottom.png", // GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
            "assets/textures/skybox/front.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_Z
            "assets/textures/skybox/back.png"    // GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };

    int[] cubeMapTargets = {
            GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GL3.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GL3.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GL3.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };
    for (int i = 0; i < 6; i++) {
      try {
        // Load the image data for the current face
        TextureData data = TextureIO.newTextureData(gl3.getGLProfile(), new File(faces[i]), false, "png");

        // Upload texture data for the specific cubemap face
        cubemap.updateImage(gl3, data, cubeMapTargets[i]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Step 4: Bind the texture to GL_TEXTURE_CUBE_MAP
    cubemap.bind(gl3);

    return cubemap;
  }

  private Camera camera;

  private Texture cubemap;

  private int skyboxVAO;
  private int skyboxVBO;

  private Shader skyboxShader;

  public Skybox(GL3 gl, Camera cameraIn) {
    camera = cameraIn;

    cubemap = loadTexture(gl);

    // Generate and bind VAO
    int[] temp = new int[1];
    gl.glGenVertexArrays(1, temp, 0);
    skyboxVAO = temp[0];
    gl.glBindVertexArray(skyboxVAO);

    // Generate and bind VBO
    gl.glGenBuffers(1, temp, 0);
    skyboxVBO = temp[0];
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, skyboxVBO);
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, skyboxVertices.length * Float.BYTES,
            FloatBuffer.wrap(skyboxVertices), GL3.GL_STATIC_DRAW);

    // Define vertex attribute
    gl.glEnableVertexAttribArray(0);
    gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 3 * Float.BYTES, 0);

    // Unbind VAO and VBO
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    gl.glBindVertexArray(0);

    skyboxShader = new Shader(gl, "assets/shaders/vs_skybox.glsl", "assets/shaders/fs_skybox.glsl");

  }

  public void renderSkybox(GL3 gl) {
    int cubemapTexture = cubemap.getTextureObject(gl);

    Mat4 viewMatrix = camera.getViewMatrix();
//    viewMatrix.set(0, 0, 0);

    Mat4 projectionMatrix = new Mat4();


    // Disable depth writing so the skybox is always in the background
    gl.glDepthMask(false);

    // Bind the skybox VAO
    gl.glBindVertexArray(skyboxVAO);

    // Use the skybox shader program
    skyboxShader.use(gl);
    skyboxShader.setFloatArray(gl, "view", viewMatrix.toFloatArrayForGLSL());
    skyboxShader.setFloatArray(gl, "projection", projectionMatrix.toFloatArrayForGLSL());
//    skyboxShader.setUniformMatrix4fv(gl, "view", viewMatrix);
//    skyboxShader.setUniformMatrix4fv(gl, "projection", projectionMatrix);

    // Bind the cubemap texture
    gl.glActiveTexture(GL3.GL_TEXTURE0);
    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, cubemapTexture);  //glTEXCUBEMAP is in the target param??
    skyboxShader.setFloat(gl, "skybox", 0);

    // Draw the skybox
    gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 36);

    // Unbind VAO and re-enable depth writing
    gl.glBindVertexArray(0);
    gl.glDepthMask(true);
  }

}