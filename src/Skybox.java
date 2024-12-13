import com.jogamp.opengl.*;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.*;
import gmaths.*; // Assuming your Mat4 and Shader classes are in this package

import java.io.File;

public class Skybox {

    private int cubemapTexture;
    private int skyboxVAO;
    private Shader shader;

    public Skybox(GL3 gl) {
        setupSkybox(gl);
        cubemapTexture = loadCubemap(gl, new String[]{
                "assets/textures/skybox/right.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_X
                "assets/textures/skybox/left.png",   // GL_TEXTURE_CUBE_MAP_NEGATIVE_X
                "assets/textures/skybox/top.png",    // GL_TEXTURE_CUBE_MAP_POSITIVE_Y
                "assets/textures/skybox/bottom.png", // GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
                "assets/textures/skybox/front.png",  // GL_TEXTURE_CUBE_MAP_POSITIVE_Z
                "assets/textures/skybox/back.png"    // GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        });

        shader = new Shader(gl, "assets/shaders/vs_skybox.glsl", "assets/shaders/fs_skybox.glsl");
        shader.use(gl);
        shader.setInt(gl, "skybox", 0);
    }

    private void setupSkybox(GL3 gl) {
        float[] skyboxVertices = {
                // Positions
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

        int[] vbo = new int[1];
        int[] vao = new int[1];

        gl.glGenBuffers(1, vbo, 0);
        gl.glGenVertexArrays(1, vao, 0);

        gl.glBindVertexArray(vao[0]);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, skyboxVertices.length * 4, GLBuffers.newDirectFloatBuffer(skyboxVertices), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 3 * 4, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        gl.glBindVertexArray(0);

        skyboxVAO = vao[0];
    }

    private int loadCubemap(GL3 gl, String[] faces) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

        int textureID = textures[0];
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, textureID);

        for (int i = 0; i < faces.length; i++) {
            try {
                File file = new File(faces[i]);
                TextureData textureData = TextureIO.newTextureData(gl.getGLProfile(), file, false, null);

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

        return textureID;
    }

    public void render(GL3 gl, Mat4 view, Mat4 projection) {
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glDepthMask(false);

        shader.use(gl);

        Mat4 viewWithoutTranslation = new Mat4(view);
        viewWithoutTranslation.set(0, 3, 0);
        viewWithoutTranslation.set(1, 3, 0);
        viewWithoutTranslation.set(2, 3, 0);
        shader.setFloatArray(gl, "view", viewWithoutTranslation.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "projection", projection.toFloatArrayForGLSL());

        gl.glBindVertexArray(skyboxVAO);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, cubemapTexture);
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
        gl.glBindVertexArray(0);

        gl.glDepthFunc(GL.GL_LESS);
        gl.glDepthMask(true);
    }
}
