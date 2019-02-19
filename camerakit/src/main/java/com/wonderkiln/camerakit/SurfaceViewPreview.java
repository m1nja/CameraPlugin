//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnLayoutChangeListener;
import com.wonderkiln.camerakit.core.R.id;
import com.wonderkiln.camerakit.core.R.layout;

public class SurfaceViewPreview extends PreviewImpl {
    private Context mContext;
    private ViewGroup mParent;
    private SurfaceViewContainer mContainer;
    private SurfaceView mSurfaceView;
    private int mDisplayOrientation;

    SurfaceViewPreview(Context context, ViewGroup parent) {
        this.mContext = context;
        this.mParent = parent;
        View view = View.inflate(context, layout.surface_view, parent);
        this.mContainer = (SurfaceViewContainer)view.findViewById(id.surface_view_container);
        this.mContainer.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                SurfaceViewPreview.this.setSize(SurfaceViewPreview.this.mContainer.getWidth(), SurfaceViewPreview.this.mContainer.getHeight());
            }
        });
        this.mSurfaceView = (SurfaceView)this.mContainer.findViewById(id.surface_view);
        SurfaceHolder holder = this.mSurfaceView.getHolder();
        holder.addCallback(new android.view.SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (SurfaceViewPreview.this.isReady()) {
                    SurfaceViewPreview.this.dispatchSurfaceChanged();
                }

            }

            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    SurfaceHolder getSurfaceHolder() {
        return this.mSurfaceView.getHolder();
    }

    Surface getSurface() {
        return this.getSurfaceHolder().getSurface();
    }

    View getView() {
        return this.mContainer;
    }

    Class getOutputClass() {
        return SurfaceHolder.class;
    }

    void setDisplayOrientation(int displayOrientation) {
        this.mDisplayOrientation = displayOrientation;
        this.mContainer.setDisplayOrientation(displayOrientation);
    }

    boolean isReady() {
        return this.getPreviewWidth() != 0 && this.getPreviewHeight() != 0;
    }

    float getX() {
        return this.mContainer.getChildAt(0).getX();
    }

    float getY() {
        return this.mContainer.getChildAt(0).getY();
    }

    @TargetApi(15)
    void setPreviewParameters(int width, int height, int format) {
        super.setPreviewParameters(width, height, format);
        this.mContainer.setPreviewSize(new Size(width, height));
        this.mContainer.post(new Runnable() {
            public void run() {
                SurfaceViewPreview.this.getSurfaceHolder().setFixedSize(SurfaceViewPreview.this.getPreviewWidth(), SurfaceViewPreview.this.getPreviewHeight());
            }
        });
    }
}
