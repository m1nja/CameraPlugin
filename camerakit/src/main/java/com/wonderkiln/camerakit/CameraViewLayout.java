//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.FrameLayout;

public abstract class CameraViewLayout extends FrameLayout {
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private SimpleOnGestureListener onGestureListener;
    private OnScaleGestureListener onScaleGestureListener;

    public CameraViewLayout(@NonNull Context context) {
        this(context, (AttributeSet)null);
    }

    public CameraViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.onGestureListener = new SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                CameraViewLayout.this.onToggleFacing();
                return super.onDoubleTap(e);
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                CameraViewLayout.this.onTapToFocus(e.getX() / (float)CameraViewLayout.this.getWidth(), e.getY() / (float)CameraViewLayout.this.getHeight());
                return super.onSingleTapConfirmed(e);
            }
        };
        this.onScaleGestureListener = new OnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                CameraViewLayout.this.onZoom(scaleGestureDetector.getScaleFactor(), false);
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                CameraViewLayout.this.onZoom(scaleGestureDetector.getScaleFactor(), true);
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            }
        };
        this.gestureDetector = new GestureDetector(context, this.onGestureListener);
        this.scaleGestureDetector = new ScaleGestureDetector(context, this.onScaleGestureListener);
    }

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        this.scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    protected abstract CameraImpl getCameraImpl();

    protected abstract PreviewImpl getPreviewImpl();

    protected abstract void onZoom(float var1, boolean var2);

    protected abstract void onTapToFocus(float var1, float var2);

    protected abstract void onToggleFacing();
}
