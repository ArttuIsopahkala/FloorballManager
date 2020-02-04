package com.ardeapps.floorballmanager.tacticBoard;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.ardeapps.floorballmanager.utils.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecordSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    public RecordSurfaceView(Context context) {
        super(context);
        init();
    }

    public RecordSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setPreserveEGLContextOnPause(true);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Logger.log("onSurfaceCreated");
        GLES31.glClearColor(1f, 0, 0, 1f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Logger.log("onSurfaceChanged");
        GLES31.glViewport(0, 0, width, height);
        // for a fixed camera, set the projection too
        float ratio = (float) width / height;
        /*gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);*/
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Logger.log("onDrawFrame");
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);
    }
}
