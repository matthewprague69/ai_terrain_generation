package com.example.terrain;

public final class Mat4 {
    private final float[] data;

    private Mat4(float[] data) {
        this.data = data;
    }

    public static Mat4 identity() {
        float[] m = new float[16];
        m[0] = 1f;
        m[5] = 1f;
        m[10] = 1f;
        m[15] = 1f;
        return new Mat4(m);
    }

    public static Mat4 perspective(float fovRadians, float aspect, float near, float far) {
        float f = 1f / (float) Math.tan(fovRadians / 2f);
        float[] m = new float[16];
        m[0] = f / aspect;
        m[5] = f;
        m[10] = (far + near) / (near - far);
        m[11] = -1f;
        m[14] = (2f * far * near) / (near - far);
        return new Mat4(m);
    }

    public static Mat4 lookAt(Vec3 eye, Vec3 center, Vec3 up) {
        Vec3 f = center.subtract(eye).normalize();
        Vec3 s = f.cross(up).normalize();
        Vec3 u = s.cross(f);

        float[] m = new float[16];
        m[0] = s.x;
        m[4] = s.y;
        m[8] = s.z;
        m[1] = u.x;
        m[5] = u.y;
        m[9] = u.z;
        m[2] = -f.x;
        m[6] = -f.y;
        m[10] = -f.z;
        m[15] = 1f;
        m[12] = -s.dot(eye);
        m[13] = -u.dot(eye);
        m[14] = f.dot(eye);
        return new Mat4(m);
    }

    public static Mat4 multiply(Mat4 left, Mat4 right) {
        float[] a = left.data;
        float[] b = right.data;
        float[] result = new float[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[col * 4 + row] =
                    a[0 * 4 + row] * b[col * 4 + 0] +
                    a[1 * 4 + row] * b[col * 4 + 1] +
                    a[2 * 4 + row] * b[col * 4 + 2] +
                    a[3 * 4 + row] * b[col * 4 + 3];
            }
        }
        return new Mat4(result);
    }

    public float[] data() {
        return data;
    }
}
