//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.support.annotation.Nullable;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import java.io.File;

abstract class CameraImpl {
    protected final EventDispatcher mEventDispatcher;
    protected final PreviewImpl mPreview;

    CameraImpl(EventDispatcher eventDispatcher, PreviewImpl preview) {
        this.mEventDispatcher = eventDispatcher;
        this.mPreview = preview;
    }

    abstract void start();

    abstract void stop();

    abstract void setDisplayAndDeviceOrientation(int var1, int var2);

    abstract void setFacing(int var1);

    abstract void setFlash(int var1);

    abstract void setFocus(int var1);

    abstract void setMethod(int var1);

    abstract void setTextDetector(Detector<TextBlock> var1);

    abstract void setVideoQuality(int var1);

    abstract void setVideoBitRate(int var1);

    abstract void setLockVideoAspectRatio(boolean var1);

    abstract void setZoom(float var1);

    abstract void modifyZoom(float var1);

    abstract void setFocusArea(float var1, float var2);

    abstract void captureImage(CameraImpl.ImageCapturedCallback var1);

    void captureVideo(File videoFile, CameraImpl.VideoCapturedCallback callback) {
        this.captureVideo(videoFile, 0, callback);
    }

    abstract void captureVideo(File var1, int var2, CameraImpl.VideoCapturedCallback var3);

    abstract void stopVideo();

    abstract Size getCaptureResolution();

    abstract Size getVideoResolution();

    abstract Size getPreviewResolution();

    abstract boolean isCameraOpened();

    abstract boolean frontCameraOnly();

    @Nullable
    abstract CameraProperties getCameraProperties();

    interface VideoCapturedCallback {
        void videoCaptured(File var1);
    }

    interface ImageCapturedCallback {
        void imageCaptured(byte[] var1);
    }
}
