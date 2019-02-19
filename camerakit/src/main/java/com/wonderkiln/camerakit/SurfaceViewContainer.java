//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class SurfaceViewContainer extends FrameLayout {
    private Size mPreviewSize;
    private int mDisplayOrientation;

    public SurfaceViewContainer(@NonNull Context context) {
        super(context);
    }

    public SurfaceViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(this.getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(this.getSuggestedMinimumHeight(), heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && this.getChildCount() > 0) {
            this.layoutChild(r - l, b - t);
        }

    }

    private void layoutChild(int width, int height) {
        View child = this.getChildAt(0);
        int previewWidth = width;
        int previewHeight = height;
        if (this.mPreviewSize != null) {
            previewWidth = this.mPreviewSize.getWidth();
            previewHeight = this.mPreviewSize.getHeight();
        }

        int scaledChildHeight;
        if (width * previewHeight > height * previewWidth) {
            scaledChildHeight = previewHeight * width / previewWidth;
            child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
        } else {
            scaledChildHeight = previewWidth * height / previewHeight;
            child.layout((width - scaledChildHeight) / 2, 0, (width + scaledChildHeight) / 2, height);
        }

    }

    public void setPreviewSize(Size previewSize) {
        this.setPreviewSize(previewSize, this.mDisplayOrientation);
    }

    public void setPreviewSize(Size previewSize, int displayOrientation) {
        if (this.mDisplayOrientation != 0 && this.mDisplayOrientation != 180) {
            if ((displayOrientation == 90 || displayOrientation == 270) && this.mDisplayOrientation != 90 && this.mDisplayOrientation != 270) {
                this.mPreviewSize = new Size(previewSize.getHeight(), previewSize.getWidth());
            }
        } else {
            this.mPreviewSize = previewSize;
        }

        if (this.getChildCount() > 0) {
            this.post(new Runnable() {
                public void run() {
                    SurfaceViewContainer.this.layoutChild(SurfaceViewContainer.this.getWidth(), SurfaceViewContainer.this.getHeight());
                }
            });
        }

    }

    public void setDisplayOrientation(int displayOrientation) {
        if (this.mPreviewSize != null) {
            this.setPreviewSize(this.mPreviewSize, displayOrientation);
        } else {
            this.mDisplayOrientation = displayOrientation;
        }

    }
}
