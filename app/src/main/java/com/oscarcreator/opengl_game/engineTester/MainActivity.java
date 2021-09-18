package com.oscarcreator.opengl_game.engineTester;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.oscarcreator.opengl_game.renderEngine.CubeRenderer;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        //This is used to check if the device supports opengl version 2.0
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();

        //This makes sure both emulators and device that run this supports OpenGl ES 2.0 or later
        //This also assumes that the emulator supports OpenGL ES 2.0
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2){
            //Request and OpenGL ES 2.0 compatible context.
            //This only works if the device supports the version
            glSurfaceView.setEGLContextClientVersion(2);

            //##### If emulator not working glSurfaceView.setEGLConfigChoose(8,8,8,8,16,0)

            //Assign our renderer.
            glSurfaceView.setRenderer(new CubeRenderer(this, glSurfaceView));

            renderSet = true;
        }else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_LONG).show();
            return;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(glSurfaceView);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //!!Makes sure to pause the glsurfaceview when another activity is
        // started up front
        if (renderSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //!!Makes sure to resume the glsurfaceview when another activity is
        // closed and this becomes the active activity.
        if (renderSet){
            glSurfaceView.onResume();
        }
    }
}
