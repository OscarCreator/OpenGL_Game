package com.oscarcreator.opengl_game.library;

public class Vector4f {

    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f(){

    }

    public Vector4f(float x, float y, float z, float w) {
        this.set(x, y, z, w);
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f normalise(Vector4f dest) {
        float l = this.length();
        if (dest == null) {
            dest = new Vector4f(this.x / l, this.y / l, this.z / l, this.w / l);
        } else {
            dest.set(this.x / l, this.y / l, this.z / l, this.w / l);
        }

        return dest;
    }

    public final float length() {
        return (float)Math.sqrt((double)this.lengthSquared());
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }
}
