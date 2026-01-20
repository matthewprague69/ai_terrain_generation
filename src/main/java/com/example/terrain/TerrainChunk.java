package com.example.terrain;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public final class TerrainChunk {
    private final int vao;
    private final int vbo;
    private final int ebo;
    private final int indexCount;

    public TerrainChunk(int size, float spacing, float heightScale, Noise noise) {
        int vertexCount = size * size;
        float[] heights = new float[vertexCount];
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                int idx = z * size + x;
                float sampleX = x * 0.08f;
                float sampleZ = z * 0.08f;
                float height = noise.fractalNoise(sampleX, sampleZ, 5, 2f, 0.5f);
                heights[idx] = height * heightScale;
            }
        }

        float[] vertices = new float[vertexCount * 6];
        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                int idx = z * size + x;
                float y = heights[idx];
                Vec3 normal = computeNormal(size, heights, x, z, spacing);
                int v = idx * 6;
                vertices[v] = x * spacing;
                vertices[v + 1] = y;
                vertices[v + 2] = z * spacing;
                vertices[v + 3] = normal.x;
                vertices[v + 4] = normal.y;
                vertices[v + 5] = normal.z;
            }
        }

        int quadCount = (size - 1) * (size - 1);
        int[] indices = new int[quadCount * 6];
        int offset = 0;
        for (int z = 0; z < size - 1; z++) {
            for (int x = 0; x < size - 1; x++) {
                int topLeft = z * size + x;
                int topRight = topLeft + 1;
                int bottomLeft = (z + 1) * size + x;
                int bottomRight = bottomLeft + 1;
                indices[offset++] = topLeft;
                indices[offset++] = bottomLeft;
                indices[offset++] = topRight;
                indices[offset++] = topRight;
                indices[offset++] = bottomLeft;
                indices[offset++] = bottomRight;
            }
        }

        indexCount = indices.length;

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int stride = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    private Vec3 computeNormal(int size, float[] heights, int x, int z, float spacing) {
        int left = Math.max(x - 1, 0);
        int right = Math.min(x + 1, size - 1);
        int down = Math.max(z - 1, 0);
        int up = Math.min(z + 1, size - 1);

        float hL = heights[z * size + left];
        float hR = heights[z * size + right];
        float hD = heights[down * size + x];
        float hU = heights[up * size + x];

        Vec3 normal = new Vec3(hL - hR, 2f * spacing, hD - hU);
        return normal.normalize();
    }

    public void render() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }
}
