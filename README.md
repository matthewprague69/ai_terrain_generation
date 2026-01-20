# LWJGL Terrain Generator

This project is a lightweight LWJGL 3 terrain generator demo. It focuses on modern OpenGL practices (VAO/VBO, indexed drawing, and shader-based lighting) while keeping the code small and approachable.

## Running

```bash
./gradlew run
```

If you are on Windows and see a Gradle error about missing Java toolchains, make sure a JDK is installed and selected in IntelliJ (File → Project Structure → Project SDK). This project targets Java 17 but should compile on newer JDKs like 21.

Controls:
- WASD: move
- Mouse: look
- Q/E: descend/ascend
- Esc: release mouse
```
