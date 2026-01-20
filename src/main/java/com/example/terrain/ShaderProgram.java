package com.example.terrain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        int vertexShader = compileShader(loadResource(vertexPath), GL_VERTEX_SHADER);
        int fragmentShader = compileShader(loadResource(fragmentPath), GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Shader link failed: " + glGetProgramInfoLog(programId));
        }
        glDetachShader(programId, vertexShader);
        glDetachShader(programId, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(programId);
    }

    public void setUniformMat4(String name, Mat4 mat) {
        int location = glGetUniformLocation(programId, name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, stack.floats(mat.data()));
        }
    }

    public void setUniformVec3(String name, Vec3 vec) {
        int location = glGetUniformLocation(programId, name);
        glUniform3f(location, vec.x, vec.y, vec.z);
    }

    public void setUniformFloat(String name, float value) {
        int location = glGetUniformLocation(programId, name);
        glUniform1f(location, value);
    }

    public void cleanup() {
        glDeleteProgram(programId);
    }

    private int compileShader(String source, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Shader compile failed: " + glGetShaderInfoLog(shaderId));
        }
        return shaderId;
    }

    private String loadResource(String path) {
        try (InputStream stream = ShaderProgram.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing shader: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read shader: " + path, ex);
        }
    }
}
