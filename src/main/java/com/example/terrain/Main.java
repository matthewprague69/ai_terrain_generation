package com.example.terrain;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public final class Main {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private long window;
    private Camera camera;
    private TerrainChunk terrain;
    private ShaderProgram shader;

    private double lastMouseX = WIDTH / 2.0;
    private double lastMouseY = HEIGHT / 2.0;
    private boolean firstMouse = true;
    private boolean cursorLocked = true;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        initWindow();
        initScene();
        loop();
        cleanup();
    }

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "LWJGL Terrain", NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window, (handle, xpos, ypos) -> onMouseMove(xpos, ypos));
        glfwSetKeyCallback(window, (handle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                cursorLocked = !cursorLocked;
                glfwSetInputMode(window, GLFW_CURSOR, cursorLocked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
                firstMouse = true;
            }
        });

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    private void initScene() {
        camera = new Camera(new Vec3(40f, 35f, 40f), -135f, -25f);
        shader = new ShaderProgram("/shaders/terrain.vert", "/shaders/terrain.frag");
        terrain = new TerrainChunk(200, 1f, 10f, new Noise(1337L));
    }

    private void loop() {
        float lastTime = (float) glfwGetTime();
        while (!glfwWindowShouldClose(window)) {
            float currentTime = (float) glfwGetTime();
            float delta = currentTime - lastTime;
            lastTime = currentTime;

            handleInput(delta);
            render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleInput(float delta) {
        float speed = 25f * delta;
        Vec3 forward = camera.forward();
        Vec3 right = camera.right();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.move(forward.multiply(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.move(forward.multiply(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.move(right.multiply(-speed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.move(right.multiply(speed));
        }
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            camera.move(new Vec3(0f, -speed, 0f));
        }
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            camera.move(new Vec3(0f, speed, 0f));
        }
    }

    private void render() {
        glViewport(0, 0, WIDTH, HEIGHT);
        glClearColor(0.08f, 0.1f, 0.14f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();

        Mat4 projection = Mat4.perspective((float) Math.toRadians(60f), WIDTH / (float) HEIGHT, 0.1f, 500f);
        shader.setUniformMat4("uProjection", projection);
        shader.setUniformMat4("uView", camera.viewMatrix());
        shader.setUniformMat4("uModel", Mat4.identity());
        shader.setUniformVec3("uLightDir", new Vec3(-0.4f, -1f, -0.3f).normalize());
        shader.setUniformVec3("uCameraPos", camera.position());

        terrain.render();
    }

    private void cleanup() {
        terrain.cleanup();
        shader.cleanup();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void onMouseMove(double xpos, double ypos) {
        if (!cursorLocked) {
            return;
        }
        if (firstMouse) {
            lastMouseX = xpos;
            lastMouseY = ypos;
            firstMouse = false;
        }
        float xOffset = (float) (xpos - lastMouseX);
        float yOffset = (float) (lastMouseY - ypos);
        lastMouseX = xpos;
        lastMouseY = ypos;

        float sensitivity = 0.12f;
        camera.rotate(xOffset * sensitivity, yOffset * sensitivity);
    }
}
