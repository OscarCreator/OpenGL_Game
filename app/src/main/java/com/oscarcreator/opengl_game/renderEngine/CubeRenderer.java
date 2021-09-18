package com.oscarcreator.opengl_game.renderEngine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.oscarcreator.opengl_game.R;
import com.oscarcreator.opengl_game.entities.Camera;
import com.oscarcreator.opengl_game.entities.Entity;
import com.oscarcreator.opengl_game.entities.Light;
import com.oscarcreator.opengl_game.library.Vector3f;
import com.oscarcreator.opengl_game.models.RawModel;
import com.oscarcreator.opengl_game.models.TexturedModel;
import com.oscarcreator.opengl_game.shaders.StaticShader;
import com.oscarcreator.opengl_game.textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;


public class CubeRenderer implements Renderer, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "CubeRenderer";

    private final Context context;


    private StaticShader shaderProgram;
    private MasterRenderer renderer;
    private Loader loader;
    private Camera camera;
    private Light light;

    private GLSurfaceView surfaceView;

    private List<Entity> cubes;

    //private int texture;

    private ScaleGestureDetector detector;

    private float firstX, firstY;

    @SuppressLint("ClickableViewAccessibility")
    public CubeRenderer(Context context, GLSurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        detector = new ScaleGestureDetector(context, this);
        surfaceView.setOnTouchListener((v, event) -> {
            detector.onTouchEvent(event);


            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                firstX = event.getX();
                firstY = event.getY();
            }
            if (event.getPointerCount() == 2) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    //- = left/up + = right/down

                    float dx = event.getX() - firstX;
                    float dy = event.getY() - firstY;
                    camera.move(-dx / 50, dy / 50   );

                    firstX = event.getX();
                    firstY = event.getY();
                    Log.i(TAG, String.format("fx:%f fy:%f x:%f y:%f", firstX, firstY, event.getX(), event.getY()));
                }
            }


            return true;
        });
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //Clear color to black
        glClearColor(0, 0, 0, 0);


        Log.i("CubeRenderer", "width:" + surfaceView.getWidth() + " height:" +
                surfaceView.getHeight());

        shaderProgram = new StaticShader(context);

        loader = new Loader();


        RawModel model = OBJLoader.loadObjModel(context, R.raw.cube, loader);

        TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture(context, R.drawable.white)));

        Random random = new Random();
        cubes = new ArrayList<>();

        for (int i = 0; i < 2000; i++) {
            float x = random.nextFloat() * 100 - 50;
            float y = random.nextFloat() * 100 - 50;
            float z = random.nextFloat() * -300;
            cubes.add(new Entity(texturedModel, new Vector3f(x, y, z), random.nextFloat() * 180f, random.nextFloat() * 180f, 0f, 1f));
        }

        light = new Light(new Vector3f(3000, 2000, 3000), new Vector3f(1, 1, 1));
        camera = new Camera();

        renderer = new MasterRenderer(surfaceView.getWidth(), surfaceView.getHeight(), context);

    }


    @Override
    public void onDrawFrame(GL10 gl) {

        for (Entity entity : cubes) {
            renderer.processEntity(entity);
        }

        renderer.render(light, camera);

    }


    //This is called when the size on the screen is changed.
    //For example from portrait to landscape mode.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        camera.scale(detector.getScaleFactor());
        Log.i(TAG, String.format("factor %f", detector.getScaleFactor()));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.i(TAG, "Scale start");

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.i(TAG, "Scale end");
    }
}
