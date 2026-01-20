package com.example.terrain;

public final class Vec3 {
    public final float x;
    public final float y;
    public final float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 multiply(float scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public float dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize() {
        float len = length();
        if (len == 0f) {
            return new Vec3(0f, 1f, 0f);
        }
        return new Vec3(x / len, y / len, z / len);
    }
}
