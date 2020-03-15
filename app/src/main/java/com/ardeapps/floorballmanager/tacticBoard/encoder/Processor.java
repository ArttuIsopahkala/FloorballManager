package com.ardeapps.floorballmanager.tacticBoard.encoder;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES31;

public class Processor extends Thread {

    // vertex shader
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    // fragment shader
    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private EGLContext mGlContext;
    private EGLDisplay mDisplay;
    private EGLSurface mEncoder;
    private SurfaceTexture mPreviewSurface;
    private float mCameraRatio;
    private int mCropWidth = 720;
    private int mCropHeight = 720;

    private void drawFrame() {
        //Select the surface to use
        EGL14.eglMakeCurrent(mDisplay, mEncoder, mEncoder, mGlContext);

        // Setup the viewport to crop
        GLES31.glViewport(0, 0, mCropWidth, mCropHeight);

        /*// Select the program.
        GLES31.glUseProgram(mShaderHandle);

        // Set the texture.
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0);
        GLES31.glBindTexture(mTextureTarget, mTextureId);

        // Copy the model / view / projection matrix over with aspect ratio correction.
        float[] mvpMatrix = IDENTITY_MATRIX;
        Matrix.scaleM(mvpMatrix, 0, 1, mCameraRatio, 1);
        GLES31.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);

        // Copy the texture transformation matrix over.
        GLES31.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);

        // Enable the "aPosition" vertex attribute.
        GLES31.glEnableVertexAttribArray(maPositionLoc);

        // Connect vertexBuffer to "aPosition".
        GLES31.glVertexAttribPointer(maPositionLoc, 2, GLES31.GL_FLOAT, false, VERTEX_STRIDE, mVertexArray);

        // Enable the "aTextureCoord" vertex attribute.
        GLES31.glEnableVertexAttribArray(maTextureCoordLoc);

        // Connect texBuffer to "aTextureCoord".
        GLES31.glVertexAttribPointer(maTextureCoordLoc, 2, GLES31.GL_FLOAT, false, TEX_COORD_STRIDE, mTexCoordArray);

        // Draw the quad.
        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4);

        // Done -- disable vertex array, texture, and program.
        GLES31.glDisableVertexAttribArray(maPositionLoc);
        GLES31.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES31.glBindTexture(mTextureTarget, 0);
        GLES31.glUseProgram(0);*/

        EGL14.eglSwapBuffers(mDisplay, mEncoder);
    }
}
