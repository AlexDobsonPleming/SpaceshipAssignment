package models.meshes;

import models.meshes.IMesh;

import java.util.ArrayList;
import java.util.List;

public class Cylinder implements IMesh {
    private float radius;
    private float height;
    private int segments;

    private List<Float> vertices; // Interleaved vertex data
    private List<Integer> indices;

    // Default constructor with sensible defaults
    public Cylinder() {
        this(0.5f, 1.0f, 36); // Default radius: 0.5, height: 1.0, segments: 36
    }

    // Constructor with specified parameters
    public Cylinder(float radius, float height, int segments) {
        this.radius = radius;
        this.height = height;
        this.segments = segments;

        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        generateMesh();
    }

    private void generateMesh() {
        float angleStep = (float) (2.0 * Math.PI / segments);

        // Generate vertices, normals, and texture coordinates
        for (int i = 0; i <= segments; i++) {
            float angle = i * angleStep;
            float x = (float) Math.cos(angle);
            float z = (float) Math.sin(angle);
            float u = (float) i / segments;

            // Top circle vertex
            vertices.add(radius * x);          // x
            vertices.add(height / 2);          // y
            vertices.add(radius * z);          // z
            vertices.add(x);                   // nx
            vertices.add(0.0f);                // ny
            vertices.add(z);                   // nz
            vertices.add(u);                   // u (texture)
            vertices.add(1.0f);                // v (texture)

            // Bottom circle vertex
            vertices.add(radius * x);          // x
            vertices.add(-height / 2);         // y
            vertices.add(radius * z);          // z
            vertices.add(x);                   // nx
            vertices.add(0.0f);                // ny
            vertices.add(z);                   // nz
            vertices.add(u);                   // u (texture)
            vertices.add(0.0f);                // v (texture)
        }

        // Generate indices for the sides of the cylinder
        for (int i = 0; i < segments; i++) {
            int topStart = i * 2;
            int bottomStart = topStart + 1;

            indices.add(topStart);
            indices.add(bottomStart);
            indices.add(topStart + 2);

            indices.add(bottomStart);
            indices.add(bottomStart + 2);
            indices.add(topStart + 2);
        }

        // Top circle
        int centerTop = vertices.size() / 8; // Add a center vertex
        vertices.add(0.0f);  // x
        vertices.add(height / 2);  // y
        vertices.add(0.0f);  // z
        vertices.add(0.0f);  // nx
        vertices.add(1.0f);  // ny
        vertices.add(0.0f);  // nz
        vertices.add(0.5f);  // u
        vertices.add(0.5f);  // v

        for (int i = 0; i < segments; i++) {
            indices.add(centerTop);
            indices.add(i * 2);
            indices.add((i * 2 + 2) % (segments * 2));
        }

        // Bottom circle
        int centerBottom = vertices.size() / 8; // Add a center vertex
        vertices.add(0.0f);  // x
        vertices.add(-height / 2);  // y
        vertices.add(0.0f);  // z
        vertices.add(0.0f);  // nx
        vertices.add(-1.0f); // ny
        vertices.add(0.0f);  // nz
        vertices.add(0.5f);  // u
        vertices.add(0.5f);  // v

        for (int i = 0; i < segments; i++) {
            indices.add(centerBottom);
            indices.add((i * 2 + 1) % (segments * 2));
            indices.add((i * 2 + 3) % (segments * 2));
        }
    }

    public float[] getVertices() {
        float[] vertexArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vertexArray[i] = vertices.get(i);
        }
        return vertexArray;
    }

    public int[] getIndices() {
        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        return indexArray;
    }
}
