package com.oscarcreator.opengl_game.entities;

import com.oscarcreator.opengl_game.library.*;

public class Camera {

    private Vector3f position = new Vector3f(1,1,1);
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(){

    }

    public void move(float dx, float dy){
        position.x += dx;
        position.y += dy;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void scale(float scaleFactor) {
        position.z += (1 - scaleFactor) * 15;
    }
}
