//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

abstract class PreviewImpl {
    private PreviewImpl.Callback mCallback;
    private int mWidth;
    private int mHeight;
    protected int mPreviewWidth;
    protected int mPreviewHeight;
    protected int mPreviewFormat;

    PreviewImpl() {
    }

    void setCallback(PreviewImpl.Callback callback) {
        this.mCallback = callback;
    }

    abstract Surface getSurface();

    abstract View getView();

    abstract Class getOutputClass();

    abstract void setDisplayOrientation(int var1);

    abstract boolean isReady();

    protected void dispatchSurfaceChanged() {
        this.mCallback.onSurfaceChanged();
    }

    SurfaceHolder getSurfaceHolder() {
        return null;
    }

    SurfaceTexture getSurfaceTexture() {
        return null;
    }

    void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    int getWidth() {
        return this.mWidth;
    }

    int getHeight() {
        return this.mHeight;
    }

    abstract float getX();

    abstract float getY();

    void setPreviewParameters(int width, int height, int format) {
        this.mPreviewWidth = width;
        this.mPreviewHeight = height;
        this.mPreviewFormat = format;
    }

    int getPreviewWidth() {
        return this.mPreviewWidth;
    }

    int getPreviewHeight() {
        return this.mPreviewHeight;
    }

    int getPreviewFormat() {
        return this.mPreviewFormat;
    }

    interface Callback {
        void onSurfaceChanged();
    }
}
