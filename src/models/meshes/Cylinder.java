package models.meshes;

import java.util.ArrayList;
import java.util.List;

public class Cylinder implements IMesh {
    private float radius;
    private float height;
    private int segments;

    private List<Float> vertices;
    private List<Float> normals;
    private List<Integer> indices;

    public Cylinder() {
        this(0.5f, 1.0f, 36); // Default radius: 0.5, height: 1.0, segments: 36
    }

    public Cylinder(float radius, float height, int segments) {
        this.radius = radius;
        this.height = height;
        this.segments = segments;

        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        indices = new ArrayList<>();
        generateMesh();
    }

    private void generateMesh() {
        float angleStep = (float) (2.0 * Math.PI / segments);

        // Generate vertices and normals for the top and bottom circles
        for (int i = 0; i <= segments; i++) {
            float angle = i * angleStep;
            float x = (float) Math.cos(angle);
            float z = (float) Math.sin(angle);

            // Top circle
            vertices.add(radius * x);  // x
            vertices.add(height / 2);  // y (top)
            vertices.add(radius * z); // z

            normals.add(x);           // nx
            normals.add(0.0f);        // ny
            normals.add(z);           // nz

            // Bottom circle
            vertices.add(radius * x);  // x
            vertices.add(-height / 2); // y (bottom)
            vertices.add(radius * z); // z

            normals.add(x);            // nx
            normals.add(0.0f);         // ny
            normals.add(z);            // nz
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

        // Generate indices for the top circle
        int centerTop = vertices.size() / 3; // Add a center vertex for the top
        vertices.add(0.0f);  // x
        vertices.add(height / 2);  // y
        vertices.add(0.0f);  // z

        normals.add(0.0f);  // nx
        normals.add(1.0f);  // ny
        normals.add(0.0f);  // nz

        for (int i = 0; i < segments; i++) {
            indices.add(centerTop);
            indices.add(i * 2);
            indices.add((i * 2 + 2) % (segments * 2));
        }

        // Generate indices for the bottom circle
        int centerBottom = vertices.size() / 3; // Add a center vertex for the bottom
        vertices.add(0.0f);  // x
        vertices.add(-height / 2);  // y
        vertices.add(0.0f);  // z

        normals.add(0.0f);  // nx
        normals.add(-1.0f); // ny
        normals.add(0.0f);  // nz

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

    public float[] getNormals() {
        float[] normalArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            normalArray[i] = normals.get(i);
        }
        return normalArray;
    }

    public int[] getIndices() {
        int[] indexArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        return indexArray;
    }
}
