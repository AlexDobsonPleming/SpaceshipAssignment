import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import gmaths.Mat4;
import gmaths.Mat4Transform;

import java.io.File;
import java.nio.FloatBuffer;

public class Skybox2 {

  private int skyboxVAO;
  private int skyboxVBO;
  private int cubemapTexture;
  private Shader shaderProgram;

  private static final float[] skyboxVertices = {
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

  public void init(GL3 gl) {
    // Load shaders
    shaderProgram =  new Shader(gl, "assets/shaders/vs_skybox.glsl", "assets/shaders/fs_skybox.glsl");

    // Set up skybox VAO and VBO
    int[] buffers = new int[1];
    gl.glGenVertexArrays(1, buffers, 0);
    skyboxVAO = buffers[0];
    gl.glBindVertexArray(skyboxVAO);

    gl.glGenBuffers(1, buffers, 0);
    skyboxVBO = buffers[0];
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, skyboxVBO);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, skyboxVertices.length * Float.BYTES, FloatBuffer.wrap(skyboxVertices), GL.GL_STATIC_DRAW);

    gl.glEnableVertexAttribArray(0);
    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * Float.BYTES, 0);
    gl.glBindVertexArray(0);

    // Load cubemap texture
    String[] faces = {
            "assets/textures/skybox/right.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_X
            "assets/textures/skybox/left.png",   // GL_TEXTURE_CUBE_MAP_NEGATIVE_X
            "assets/textures/skybox/top.png",    // GL_TEXTURE_CUBE_MAP_POSITIVE_Y
            "assets/textures/skybox/bottom.png", // GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
            "assets/textures/skybox/front.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_Z
            "assets/textures/skybox/back.png"    // GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
    };
    cubemapTexture = loadCubemap(gl, faces);
  }

  public void render(GL3 gl, float[] viewMatrix, float[] projectionMatrix) {
    // Use shader program
    shaderProgram.use(gl);

    gl.glDepthMask(false);

    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);

    float scale = 20.0f; // Scale factor to enlarge the skybox
    Mat4 scaleMatrix = Mat4Transform.scale(scale, scale, scale);
    shaderProgram.setFloatArray(gl, "model", scaleMatrix.toFloatArrayForGLSL());

    // Set view and projection matrices
    float[] viewWithoutTranslation = new float[16];
    System.arraycopy(viewMatrix, 0, viewWithoutTranslation, 0, 12); // Remove translation part
    shaderProgram.setFloatArray(gl, "view", viewWithoutTranslation);
    shaderProgram.setFloatArray(gl, "projection", projectionMatrix);

    // Bind skybox VAO
    gl.glBindVertexArray(skyboxVAO);

    // Bind cubemap texture
    gl.glActiveTexture(GL3.GL_TEXTURE0);
    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, cubemapTexture);

    // Draw skybox
    gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 36);
    gl.glBindVertexArray(0);

    gl.glDepthMask(true);

    // Unbind texture
    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, 0);
  }

  private String getGLErrorString(int errorCode) {
    switch (errorCode) {
      case GL3.GL_NO_ERROR: return "No error";
      case GL3.GL_INVALID_ENUM: return "Invalid enum";
      case GL3.GL_INVALID_VALUE: return "Invalid value";
      case GL3.GL_INVALID_OPERATION: return "Invalid operation";
      case GL3.GL_STACK_OVERFLOW: return "Stack overflow";
      case GL3.GL_STACK_UNDERFLOW: return "Stack underflow";
      case GL3.GL_OUT_OF_MEMORY: return "Out of memory";
      case GL3.GL_INVALID_FRAMEBUFFER_OPERATION: return "Invalid framebuffer operation";
      default: return "Unknown error (code: " + errorCode + ")";
    }
  }

  private void checkGLError(GL3 gl, String operation) {
    int errorCode = gl.glGetError();
    if (errorCode != GL.GL_NO_ERROR) {
      System.err.println("OpenGL Error after " + operation + ": " + getGLErrorString(errorCode));
    }
  }

  private int loadCubemap(GL3 gl, String[] faces) {
    int[] textures = new int[1];
    gl.glGenTextures(1, textures, 0);

    int textureID = textures[0];
    gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, textureID);

    for (int i = 0; i < faces.length; i++) {
      try {
        // Load the texture file
        File file = new File(faces[i]);
        TextureData textureData = TextureIO.newTextureData(gl.getGLProfile(), file, false, null);

        // Upload the texture to the GPU
        gl.glTexImage2D(
                GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, // Target for cubemap face
                0,                                    // Mipmap level
                textureData.getInternalFormat(),      // Internal format
                textureData.getWidth(),               // Width
                textureData.getHeight(),              // Height
                0,                                    // Border (must be 0)
                textureData.getPixelFormat(),         // Format of pixel data
                textureData.getPixelType(),           // Data type of pixel data
                textureData.getBuffer()               // Buffer containing pixel data
        );
        checkGLError(gl, "glTexImage2D");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Set texture parameters
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);

    System.out.println("Cubemap is texture: " + gl.glIsTexture(textureID));
    return textureID;
  }

  public void dispose(GL3 gl) {
    gl.glDeleteVertexArrays(1, new int[]{skyboxVAO}, 0);
    gl.glDeleteBuffers(1, new int[]{skyboxVBO}, 0);
    gl.glDeleteTextures(1, new int[]{cubemapTexture}, 0);
//    shaderProgram.dispose(gl);
  }
}
