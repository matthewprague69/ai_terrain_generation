package com.example.terrain;

import java.util.Random;

public final class Noise {
    private final int[] permutation;

    public Noise(long seed) {
        permutation = new int[512];
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        Random random = new Random(seed);
        for (int i = 255; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = p[i];
            p[i] = p[index];
            p[index] = temp;
        }
        for (int i = 0; i < 512; i++) {
            permutation[i] = p[i & 255];
        }
    }

    public float fractalNoise(float x, float z, int octaves, float lacunarity, float gain) {
        float amplitude = 1f;
        float frequency = 1f;
        float value = 0f;
        for (int i = 0; i < octaves; i++) {
            value += perlin(x * frequency, z * frequency) * amplitude;
            amplitude *= gain;
            frequency *= lacunarity;
        }
        return value;
    }

    private float perlin(float x, float z) {
        int xi = floor(x) & 255;
        int zi = floor(z) & 255;
        float xf = x - floor(x);
        float zf = z - floor(z);
        float u = fade(xf);
        float v = fade(zf);

        int aa = permutation[permutation[xi] + zi];
        int ab = permutation[permutation[xi] + zi + 1];
        int ba = permutation[permutation[xi + 1] + zi];
        int bb = permutation[permutation[xi + 1] + zi + 1];

        float x1 = lerp(grad(aa, xf, zf), grad(ba, xf - 1f, zf), u);
        float x2 = lerp(grad(ab, xf, zf - 1f), grad(bb, xf - 1f, zf - 1f), u);
        return lerp(x1, x2, v);
    }

    private int floor(float value) {
        return value >= 0 ? (int) value : (int) value - 1;
    }

    private float fade(float t) {
        return t * t * t * (t * (t * 6f - 15f) + 10f);
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private float grad(int hash, float x, float z) {
        switch (hash & 3) {
            case 0:
                return x + z;
            case 1:
                return -x + z;
            case 2:
                return x - z;
            default:
                return -x - z;
        }
    }
}
