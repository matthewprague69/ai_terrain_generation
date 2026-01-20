package com.example.terrain;

public final class Camera {
    private Vec3 position;
    private float yaw;
    private float pitch;

    public Camera(Vec3 position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vec3 position() {
        return position;
    }

    public void move(Vec3 delta) {
        position = position.add(delta);
    }

    public void rotate(float yawDelta, float pitchDelta) {
        yaw += yawDelta;
        pitch += pitchDelta;
        pitch = Math.max(-89f, Math.min(89f, pitch));
    }

    public Vec3 forward() {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);
        float x = (float) (Math.cos(yawRad) * Math.cos(pitchRad));
        float y = (float) Math.sin(pitchRad);
        float z = (float) (Math.sin(yawRad) * Math.cos(pitchRad));
        return new Vec3(x, y, z).normalize();
    }

    public Vec3 right() {
        return forward().cross(new Vec3(0f, 1f, 0f)).normalize();
    }

    public Mat4 viewMatrix() {
        Vec3 center = position.add(forward());
        return Mat4.lookAt(position, center, new Vec3(0f, 1f, 0f));
    }
}
