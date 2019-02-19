//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.wonderkiln.camerakit.CameraImpl.ImageCapturedCallback;
import com.wonderkiln.camerakit.CameraImpl.VideoCapturedCallback;
import com.wonderkiln.camerakit.CameraKit.Internal;
import com.wonderkiln.camerakit.ConstantMapper.Facing;
import com.wonderkiln.camerakit.ConstantMapper.Flash;
import com.wonderkiln.camerakit.PreviewImpl.Callback;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Camera1 extends CameraImpl {
    private static final String TAG = Camera1.class.getSimpleName();
    private static final int FOCUS_AREA_SIZE_DEFAULT = 300;
    private static final int FOCUS_METERING_AREA_WEIGHT_DEFAULT = 1000;
    private static final int DELAY_MILLIS_BEFORE_RESETTING_FOCUS = 3000;
    private int mCameraId;
    private Camera mCamera;
    private Parameters mCameraParameters;
    private CameraProperties mCameraProperties;
    private CameraInfo mCameraInfo;
    private Size mCaptureSize;
    private Size mVideoSize;
    private Size mPreviewSize;
    private MediaRecorder mMediaRecorder;
    private AutoFocusCallback mAutofocusCallback;
    private boolean capturingImage = false;
    private boolean mShowingPreview;
    private boolean mRecording;
    private int mDisplayOrientation;
    private int mDeviceOrientation;
    private int mFacing;
    private int mFlash;
    private int mFocus;
    private int mMethod;
    private int mVideoQuality;
    private Detector<TextBlock> mTextDetector;
    private int mVideoBitRate;
    private boolean mLockVideoAspectRatio;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Handler mHandler = new Handler();
    private FrameProcessingRunnable mFrameProcessor;
    private float mZoom = 1.0F;
    private VideoCapturedCallback mVideoCallback;
    private final Object mCameraLock = new Object();
    private File mMediaRecorderOutputFile;

    Camera1(EventDispatcher eventDispatcher, PreviewImpl preview) {
        super(eventDispatcher, preview);
        preview.setCallback(new Callback() {
            public void onSurfaceChanged() {
                if (Camera1.this.mCamera != null) {
                    if (Camera1.this.mShowingPreview) {
                        Camera1.this.mCamera.stopPreview();
                        Camera1.this.mShowingPreview = false;
                    }

                    Camera1.this.setDisplayAndDeviceOrientation();
                    Camera1.this.setupPreview();
                    if (!Camera1.this.mShowingPreview) {
                        Camera1.this.mCamera.startPreview();
                        Camera1.this.mShowingPreview = true;
                    }
                }

            }
        });
        this.mCameraInfo = new CameraInfo();
    }

    void start() {
        this.setFacing(this.mFacing);
        this.openCamera();
        if (this.mPreview.isReady()) {
            this.setDisplayAndDeviceOrientation();
            this.setupPreview();
            this.mCamera.startPreview();
            this.mShowingPreview = true;
        }

    }

    void stop() {
        this.mHandler.removeCallbacksAndMessages((Object)null);
        if (this.mCamera != null) {
            try {
                this.mCamera.stopPreview();
            } catch (Exception var2) {
                this.notifyErrorListener(var2);
            }
        }

        this.mShowingPreview = false;
        this.releaseMediaRecorder();
        this.releaseCamera();
        if (this.mFrameProcessor != null) {
            this.mFrameProcessor.cleanup();
        }

    }

    void setDisplayAndDeviceOrientation() {
        this.setDisplayAndDeviceOrientation(this.mDisplayOrientation, this.mDeviceOrientation);
    }

    void setDisplayAndDeviceOrientation(int displayOrientation, int deviceOrientation) {
        this.mDisplayOrientation = displayOrientation;
        this.mDeviceOrientation = deviceOrientation;
        Object var3 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.isCameraOpened()) {
                try {
                    this.mCamera.setDisplayOrientation(this.calculatePreviewRotation());
                } catch (RuntimeException var6) {
                    ;
                }
            }

        }
    }

    void setFacing(int facing) {
        Object var2 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            int internalFacing = (new Facing(facing)).map();
            if (internalFacing != -1) {
                int i = 0;

                for(int count = Camera.getNumberOfCameras(); i < count; ++i) {
                    Camera.getCameraInfo(i, this.mCameraInfo);
                    if (this.mCameraInfo.facing == internalFacing) {
                        this.mCameraId = i;
                        this.mFacing = facing;
                        break;
                    }
                }

                if (this.mFacing == facing && this.isCameraOpened()) {
                    this.stop();
                    this.start();
                }

            }
        }
    }

    void setFlash(int flash) {
        Object var2 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mCameraParameters != null) {
                List<String> flashes = this.mCameraParameters.getSupportedFlashModes();
                String internalFlash = (new Flash(flash)).map();
                if (flashes != null && flashes.contains(internalFlash)) {
                    this.mCameraParameters.setFlashMode(internalFlash);
                    this.mFlash = flash;
                } else {
                    String currentFlash = (new Flash(this.mFlash)).map();
                    if (flashes == null || !flashes.contains(currentFlash)) {
                        this.mCameraParameters.setFlashMode("off");
                        this.mFlash = 0;
                    }
                }

                this.mCamera.setParameters(this.mCameraParameters);
            } else {
                this.mFlash = flash;
            }

        }
    }

    void setFocus(int focus) {
        Object var2 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            this.mFocus = focus;
            List modes;
            switch(focus) {
                case 0:
                    if (this.mCameraParameters != null) {
                        modes = this.mCameraParameters.getSupportedFocusModes();
                        if (modes.contains("fixed")) {
                            this.mCameraParameters.setFocusMode("fixed");
                        } else if (modes.contains("infinity")) {
                            this.mCameraParameters.setFocusMode("infinity");
                        } else {
                            this.mCameraParameters.setFocusMode("auto");
                        }
                    }
                    break;
                case 1:
                    if (this.mCameraParameters != null) {
                        modes = this.mCameraParameters.getSupportedFocusModes();
                        if (modes.contains("continuous-picture")) {
                            this.mCameraParameters.setFocusMode("continuous-picture");
                        } else {
                            this.setFocus(0);
                        }
                    }
                    break;
                case 2:
                    if (this.mCameraParameters != null) {
                        modes = this.mCameraParameters.getSupportedFocusModes();
                        if (modes.contains("continuous-picture")) {
                            this.mCameraParameters.setFocusMode("continuous-picture");
                        }
                    }
            }

        }
    }

    void setMethod(int method) {
        this.mMethod = method;
    }

    void setTextDetector(Detector<TextBlock> detector) {
        this.mTextDetector = detector;
    }

    void setVideoQuality(int videoQuality) {
        this.mVideoQuality = videoQuality;
    }

    void setVideoBitRate(int videoBitRate) {
        this.mVideoBitRate = videoBitRate;
    }

    void setZoom(float zoomFactor) {
        Object var2 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            this.mZoom = zoomFactor;
            if (zoomFactor <= 1.0F) {
                this.mZoom = 1.0F;
            } else {
                this.mZoom = zoomFactor;
            }

            if (this.mCameraParameters != null && this.mCameraParameters.isZoomSupported()) {
                int zoomPercent = (int)(this.mZoom * 100.0F);
                this.mCameraParameters.setZoom(this.getZoomForPercent(zoomPercent));
                this.mCamera.setParameters(this.mCameraParameters);
                float maxZoom = (float)(Integer)this.mCameraParameters.getZoomRatios().get(this.mCameraParameters.getZoomRatios().size() - 1) / 100.0F;
                if (this.mZoom > maxZoom) {
                    this.mZoom = maxZoom;
                }
            }

        }
    }

    void modifyZoom(float modifier) {
        Object var2 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            this.setZoom(this.mZoom * modifier);
        }
    }

    private int getZoomForPercent(int zoomPercent) {
        List<Integer> zoomRatios = this.mCameraParameters.getZoomRatios();
        int lowerIndex = -1;
        int upperIndex = -1;

        for(int i = 0; i < zoomRatios.size(); ++i) {
            if ((Integer)zoomRatios.get(i) < zoomPercent) {
                lowerIndex = i;
            } else if ((Integer)zoomRatios.get(i) > zoomPercent) {
                upperIndex = i;
                break;
            }
        }

        if (lowerIndex < 0) {
            return 0;
        } else if (lowerIndex + 1 == upperIndex) {
            return lowerIndex;
        } else {
            return upperIndex >= 0 ? upperIndex : zoomRatios.size() - 1;
        }
    }

    void setFocusArea(float x, float y) {
        Object var3 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mCamera != null) {
                Parameters parameters = this.getCameraParameters();
                if (parameters == null) {
                    return;
                }

                String focusMode = parameters.getFocusMode();
                Rect rect = this.calculateFocusArea(x, y);
                List<Area> meteringAreas = new ArrayList();
                meteringAreas.add(new Area(rect, this.getFocusMeteringAreaWeight()));
                if (parameters.getMaxNumFocusAreas() != 0 && focusMode != null && (focusMode.equals("auto") || focusMode.equals("macro") || focusMode.equals("continuous-picture") || focusMode.equals("continuous-video"))) {
                    parameters.setFocusMode("auto");
                    parameters.setFocusAreas(meteringAreas);
                    if (parameters.getMaxNumMeteringAreas() > 0) {
                        parameters.setMeteringAreas(meteringAreas);
                    }

                    if (!parameters.getSupportedFocusModes().contains("auto")) {
                        return;
                    }

                    this.mCamera.setParameters(parameters);
                    this.mCamera.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            Camera1.this.resetFocus(success, camera);
                        }
                    });
                } else if (parameters.getMaxNumMeteringAreas() > 0) {
                    if (!parameters.getSupportedFocusModes().contains("auto")) {
                        return;
                    }

                    parameters.setFocusMode("auto");
                    parameters.setFocusAreas(meteringAreas);
                    parameters.setMeteringAreas(meteringAreas);
                    this.mCamera.setParameters(parameters);
                    this.mCamera.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            Camera1.this.resetFocus(success, camera);
                        }
                    });
                } else {
                    this.mCamera.autoFocus(new AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (Camera1.this.mAutofocusCallback != null) {
                                Camera1.this.mAutofocusCallback.onAutoFocus(success, camera);
                            }

                        }
                    });
                }
            }

        }
    }

    void setLockVideoAspectRatio(boolean lockVideoAspectRatio) {
        this.mLockVideoAspectRatio = lockVideoAspectRatio;
    }

    void captureImage(final ImageCapturedCallback callback) {
        Object var2;
        switch(this.mMethod) {
            case 0:
                var2 = this.mCameraLock;
                synchronized(this.mCameraLock) {
                    if (!this.capturingImage && this.mCamera != null) {
                        this.capturingImage = true;
                        int captureRotation = this.calculateCaptureRotation();
                        this.mCameraParameters.setRotation(captureRotation);
                        this.mCamera.setParameters(this.mCameraParameters);
                        this.mCamera.takePicture((ShutterCallback)null, (PictureCallback)null, (PictureCallback)null, new PictureCallback() {
                            public void onPictureTaken(byte[] data, Camera camera) {
                                callback.imageCaptured(data);
                                Camera1.this.capturingImage = false;
                                synchronized(Camera1.this.mCameraLock) {
                                    if (Camera1.this.isCameraOpened()) {
                                        try {
                                            Camera1.this.stop();
                                            Camera1.this.start();
                                        } catch (Exception var6) {
                                            Camera1.this.notifyErrorListener(var6);
                                        }
                                    }

                                }
                            }
                        });
                    } else {
                        Log.w(TAG, "Unable, waiting for picture to be taken");
                    }
                    break;
                }
            case 1:
                var2 = this.mCameraLock;
                synchronized(this.mCameraLock) {
                    this.mCamera.setOneShotPreviewCallback(new PreviewCallback() {
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            Parameters parameters = camera.getParameters();
                            int width = parameters.getPreviewSize().width;
                            int height = parameters.getPreviewSize().height;
                            int rotation = Camera1.this.calculateCaptureRotation();
                            YuvOperator yuvOperator = new YuvOperator(data, width, height);
                            yuvOperator.rotate(rotation);
                            data = yuvOperator.getYuvData();
                            int yuvOutputWidth = width;
                            int yuvOutputHeight = height;
                            if (rotation == 90 || rotation == 270) {
                                yuvOutputWidth = height;
                                yuvOutputHeight = width;
                            }

                            YuvImage yuvImage = new YuvImage(data, parameters.getPreviewFormat(), yuvOutputWidth, yuvOutputHeight, (int[])null);
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
                            callback.imageCaptured(out.toByteArray());
                        }
                    });
                }
        }

    }

    void captureVideo(File videoFile, int maxDuration, VideoCapturedCallback callback) {
        Object var4 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            try {
                if (this.prepareMediaRecorder(videoFile, maxDuration)) {
                    this.mMediaRecorder.start();
                    this.mRecording = true;
                    this.mVideoCallback = callback;
                } else {
                    this.releaseMediaRecorder();
                }
            } catch (IOException var7) {
                this.releaseMediaRecorder();
            } catch (RuntimeException var8) {
                this.releaseMediaRecorder();
            }

        }
    }

    void stopVideo() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mRecording) {
                try {
                    this.mMediaRecorder.stop();
                    if (this.mVideoCallback != null) {
                        this.mVideoCallback.videoCaptured(this.mMediaRecorderOutputFile);
                        this.mVideoCallback = null;
                    }
                } catch (RuntimeException var4) {
                    this.mMediaRecorderOutputFile.delete();
                }

                this.releaseMediaRecorder();
                this.mRecording = false;
            }

            this.stop();
            this.start();
        }
    }

    Size getCaptureResolution() {
        if (this.mCaptureSize == null && this.mCameraParameters != null) {
            TreeSet<Size> sizes = new TreeSet();
            Iterator var2 = this.mCameraParameters.getSupportedPictureSizes().iterator();

            while(var2.hasNext()) {
                android.hardware.Camera.Size size = (android.hardware.Camera.Size)var2.next();
                sizes.add(new Size(size.width, size.height));
            }

            TreeSet<AspectRatio> aspectRatios = this.findCommonAspectRatios(this.mCameraParameters.getSupportedPreviewSizes(), this.mCameraParameters.getSupportedPictureSizes());
            AspectRatio targetRatio = aspectRatios.size() > 0 ? (AspectRatio)aspectRatios.last() : null;
            Iterator descendingSizes = sizes.descendingIterator();

            Size size;
            do {
                if (!descendingSizes.hasNext() || this.mCaptureSize != null) {
                    return this.mCaptureSize;
                }

                size = (Size)descendingSizes.next();
            } while(targetRatio != null && !targetRatio.matches(size));

            this.mCaptureSize = size;
        }

        return this.mCaptureSize;
    }

    Size getVideoResolution() {
        if (this.mVideoSize == null && this.mCameraParameters != null) {
            if (this.mCameraParameters.getSupportedVideoSizes() == null) {
                this.mVideoSize = this.getCaptureResolution();
                return this.mVideoSize;
            }

            TreeSet<Size> sizes = new TreeSet();
            Iterator var2 = this.mCameraParameters.getSupportedVideoSizes().iterator();

            while(var2.hasNext()) {
                android.hardware.Camera.Size size = (android.hardware.Camera.Size)var2.next();
                sizes.add(new Size(size.width, size.height));
            }

            TreeSet<AspectRatio> aspectRatios = this.findCommonAspectRatios(this.mCameraParameters.getSupportedPreviewSizes(), this.mCameraParameters.getSupportedVideoSizes());
            AspectRatio targetRatio = aspectRatios.size() > 0 ? (AspectRatio)aspectRatios.last() : null;
            Iterator descendingSizes = sizes.descendingIterator();

            Size size;
            do {
                if (!descendingSizes.hasNext() || this.mVideoSize != null) {
                    return this.mVideoSize;
                }

                size = (Size)descendingSizes.next();
            } while(targetRatio != null && !targetRatio.matches(size));

            this.mVideoSize = size;
        }

        return this.mVideoSize;
    }

    Size getPreviewResolution() {
        if (this.mPreviewSize == null && this.mCameraParameters != null) {
            label83: {
                TreeSet<Size> sizes = new TreeSet();
                Iterator var2 = this.mCameraParameters.getSupportedPreviewSizes().iterator();

                while(var2.hasNext()) {
                    android.hardware.Camera.Size size = (android.hardware.Camera.Size)var2.next();
                    sizes.add(new Size(size.width, size.height));
                }

                TreeSet<AspectRatio> aspectRatios = this.findCommonAspectRatios(this.mCameraParameters.getSupportedPreviewSizes(), this.mCameraParameters.getSupportedPictureSizes());
                AspectRatio targetRatio = null;
                if (this.mLockVideoAspectRatio) {
                    TreeSet<AspectRatio> videoAspectRatios = this.findCommonAspectRatios(this.mCameraParameters.getSupportedPreviewSizes(), this.mCameraParameters.getSupportedPictureSizes());
                    Iterator descendingIterator = aspectRatios.descendingIterator();

                    while(targetRatio == null && descendingIterator.hasNext()) {
                        AspectRatio ratio = (AspectRatio)descendingIterator.next();
                        if (videoAspectRatios.contains(ratio)) {
                            targetRatio = ratio;
                        }
                    }
                }

                if (targetRatio == null) {
                    targetRatio = aspectRatios.size() > 0 ? (AspectRatio)aspectRatios.last() : null;
                }

                Iterator descendingSizes = sizes.descendingIterator();

                Size size;
                do {
                    if (!descendingSizes.hasNext() || this.mPreviewSize != null) {
                        break label83;
                    }

                    size = (Size)descendingSizes.next();
                } while(targetRatio != null && !targetRatio.matches(size));

                this.mPreviewSize = size;
            }
        }

        boolean invertPreviewSizes = (this.mCameraInfo.orientation + this.mDeviceOrientation) % 180 == 90;
        return this.mPreviewSize != null && invertPreviewSizes ? new Size(this.mPreviewSize.getHeight(), this.mPreviewSize.getWidth()) : this.mPreviewSize;
    }

    boolean isCameraOpened() {
        return this.mCamera != null;
    }

    boolean frontCameraOnly() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(0, cameraInfo);
        boolean isFrontCameraOnly = Camera.getNumberOfCameras() == 1 && cameraInfo.facing == 1;
        return isFrontCameraOnly;
    }

    @Nullable
    CameraProperties getCameraProperties() {
        return this.mCameraProperties;
    }

    private void openCamera() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mCamera != null) {
                this.releaseCamera();
            }

            this.mCamera = Camera.open(this.mCameraId);
            this.mCameraParameters = this.mCamera.getParameters();
            this.collectCameraProperties();
            this.adjustCameraParameters();
            if (VERSION.SDK_INT >= 16) {
                this.mCamera.setAutoFocusMoveCallback(new AutoFocusMoveCallback() {
                    public void onAutoFocusMoving(boolean b, Camera camera) {
                        CameraKitEvent event = new CameraKitEvent("CKFocusMovedEvent");
                        event.getData().putBoolean("started", b);
                        Camera1.this.mEventDispatcher.dispatch(event);
                    }
                });
            }

            this.mEventDispatcher.dispatch(new CameraKitEvent("CKCameraOpenedEvent"));
            if (this.mTextDetector != null) {
                this.mFrameProcessor = new FrameProcessingRunnable(this.mTextDetector, this.mPreviewSize, this.mCamera);
                this.mFrameProcessor.start();
            }

        }
    }

    private void setupPreview() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mCamera != null) {
                try {
                    this.mCamera.reconnect();
                    this.mCamera.setPreviewDisplay(this.mPreview.getSurfaceHolder());
                } catch (IOException var4) {
                    throw new RuntimeException(var4);
                }
            }

        }
    }

    private void releaseCamera() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mCamera != null) {
                this.mCamera.lock();
                this.mCamera.release();
                this.mCamera = null;
                this.mCameraParameters = null;
                this.mPreviewSize = null;
                this.mCaptureSize = null;
                this.mVideoSize = null;
                this.mEventDispatcher.dispatch(new CameraKitEvent("CKCameraStoppedEvent"));
                if (this.mFrameProcessor != null) {
                    this.mFrameProcessor.release();
                }
            }

        }
    }

    private int calculatePreviewRotation() {
        return this.mCameraInfo.facing == 1 ? (360 - (this.mCameraInfo.orientation + this.mDisplayOrientation) % 360) % 360 : (this.mCameraInfo.orientation - this.mDisplayOrientation + 360) % 360;
    }

    private int calculateCaptureRotation() {
        int captureRotation;
        if (this.mCameraInfo.facing == 1) {
            captureRotation = (this.mCameraInfo.orientation + this.mDisplayOrientation) % 360;
        } else {
            captureRotation = (this.mCameraInfo.orientation - this.mDisplayOrientation + 360) % 360;
        }

        if (this.mCameraInfo.facing == 1) {
            captureRotation = (captureRotation - (this.mDisplayOrientation - this.mDeviceOrientation) + 360) % 360;
        } else {
            captureRotation = (captureRotation + (this.mDisplayOrientation - this.mDeviceOrientation) + 360) % 360;
        }

        return captureRotation;
    }

    private void notifyErrorListener(@NonNull String details) {
        CameraKitError error = new CameraKitError();
        error.setMessage(details);
        this.mEventDispatcher.dispatch(error);
    }

    private void notifyErrorListener(@NonNull Exception e) {
        CameraKitError error = new CameraKitError(e);
        this.mEventDispatcher.dispatch(error);
    }

    private Parameters getCameraParameters() {
        if (this.mCamera != null) {
            try {
                return this.mCamera.getParameters();
            } catch (Exception var2) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void adjustCameraParameters() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mShowingPreview) {
                this.mCamera.stopPreview();
            }

            this.adjustCameraParameters(0);
            if (this.mShowingPreview) {
                this.mCamera.startPreview();
            }

        }
    }

    private void adjustCameraParameters(int currentTry) {
        boolean haveToReadjust = false;
        Parameters resolutionLess = this.mCamera.getParameters();
        if (this.getPreviewResolution() != null) {
            this.mPreview.setPreviewParameters(this.getPreviewResolution().getWidth(), this.getPreviewResolution().getHeight(), this.mCameraParameters.getPreviewFormat());
            this.mCameraParameters.setPreviewSize(this.getPreviewResolution().getWidth(), this.getPreviewResolution().getHeight());

            try {
                this.mCamera.setParameters(this.mCameraParameters);
                resolutionLess = this.mCameraParameters;
            } catch (Exception var9) {
                this.notifyErrorListener(var9);
                this.mCameraParameters = resolutionLess;
            }
        } else {
            haveToReadjust = true;
        }

        if (this.getCaptureResolution() != null) {
            this.mCameraParameters.setPictureSize(this.getCaptureResolution().getWidth(), this.getCaptureResolution().getHeight());

            try {
                this.mCamera.setParameters(this.mCameraParameters);
                resolutionLess = this.mCameraParameters;
            } catch (Exception var8) {
                this.notifyErrorListener(var8);
                this.mCameraParameters = resolutionLess;
            }
        } else {
            haveToReadjust = true;
        }

        int rotation = this.calculateCaptureRotation();
        this.mCameraParameters.setRotation(rotation);
        this.setFocus(this.mFocus);

        try {
            this.setFlash(this.mFlash);
        } catch (Exception var7) {
            this.notifyErrorListener(var7);
        }

        if (this.mCameraParameters.isZoomSupported()) {
            this.setZoom(this.mZoom);
        }

        this.mCamera.setParameters(this.mCameraParameters);
        if (haveToReadjust && currentTry < 100) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.notifyErrorListener(String.format("retryAdjustParam Failed, attempt #: %d", currentTry));
            this.adjustCameraParameters(currentTry + 1);
        }

    }

    private void collectCameraProperties() {
        this.mCameraProperties = new CameraProperties(this.mCameraParameters.getVerticalViewAngle(), this.mCameraParameters.getHorizontalViewAngle());
    }

    private TreeSet<AspectRatio> findCommonAspectRatios(List<android.hardware.Camera.Size> previewSizes, List<android.hardware.Camera.Size> pictureSizes) {
        Set<AspectRatio> previewAspectRatios = new HashSet();
        Iterator var4 = previewSizes.iterator();

        AspectRatio aspectRatio;
        while(var4.hasNext()) {
            android.hardware.Camera.Size size = (android.hardware.Camera.Size)var4.next();
            AspectRatio deviceRatio = AspectRatio.of(Internal.screenHeight, Internal.screenWidth);
            aspectRatio = AspectRatio.of(size.width, size.height);
            if (deviceRatio.equals(aspectRatio)) {
                previewAspectRatios.add(aspectRatio);
            }
        }

        Set<AspectRatio> captureAspectRatios = new HashSet();
        Iterator var11 = pictureSizes.iterator();

        android.hardware.Camera.Size maxSize;
        while(var11.hasNext()) {
            maxSize = (android.hardware.Camera.Size)var11.next();
            captureAspectRatios.add(AspectRatio.of(maxSize.width, maxSize.height));
        }

        TreeSet<AspectRatio> output = new TreeSet();
        if (previewAspectRatios.size() == 0) {
            maxSize = (android.hardware.Camera.Size)previewSizes.get(0);
            aspectRatio = AspectRatio.of(maxSize.width, maxSize.height);
            Iterator var8 = captureAspectRatios.iterator();

            while(var8.hasNext()) {
                AspectRatio mspectRatio = (AspectRatio)var8.next();
                if (mspectRatio.equals(aspectRatio)) {
                    output.add(aspectRatio);
                }
            }
        } else {
            Iterator var14 = previewAspectRatios.iterator();

            while(var14.hasNext()) {
                aspectRatio = (AspectRatio)var14.next();
                if (captureAspectRatios.contains(aspectRatio)) {
                    output.add(aspectRatio);
                }
            }
        }

        return output;
    }

    private boolean prepareMediaRecorder(File videoFile, int maxDuration) throws IOException {
        Object var3 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            this.mCamera.unlock();
            this.mMediaRecorder = new MediaRecorder();
            this.mMediaRecorder.setCamera(this.mCamera);
            this.mMediaRecorder.setAudioSource(1);
            this.mMediaRecorder.setVideoSource(1);
            CamcorderProfile profile = this.getCamcorderProfile(this.mVideoQuality);
            this.mMediaRecorder.setProfile(profile);
            if (videoFile == null) {
                videoFile = this.getVideoFile();
            }

            if (videoFile == null) {
                return false;
            } else {
                this.mMediaRecorderOutputFile = videoFile;
                this.mMediaRecorder.setOutputFile(videoFile.getPath());
                this.mMediaRecorder.setPreviewDisplay(this.mPreview.getSurface());
                this.mMediaRecorder.setOrientationHint(this.calculateCaptureRotation());
                if (maxDuration > 0) {
                    this.mMediaRecorder.setMaxDuration(maxDuration);
                    this.mMediaRecorder.setOnInfoListener(new OnInfoListener() {
                        public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                            if (what == 800) {
                                Camera1.this.stopVideo();
                            }

                        }
                    });
                }

                try {
                    this.mMediaRecorder.prepare();
                } catch (IllegalStateException var7) {
                    this.releaseMediaRecorder();
                    return false;
                } catch (IOException var8) {
                    this.releaseMediaRecorder();
                    return false;
                }

                return true;
            }
        }
    }

    private void releaseMediaRecorder() {
        Object var1 = this.mCameraLock;
        synchronized(this.mCameraLock) {
            if (this.mMediaRecorder != null) {
                this.mMediaRecorder.reset();
                this.mMediaRecorder.release();
                this.mMediaRecorder = null;
                this.mCamera.lock();
            }

        }
    }

    private File getVideoFile() {
        if (!Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
            return null;
        } else {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Camera");
            return !mediaStorageDir.exists() && !mediaStorageDir.mkdirs() ? null : new File(mediaStorageDir.getPath() + File.separator + "video.mp4");
        }
    }

    private CamcorderProfile getCamcorderProfile(int videoQuality) {
        CamcorderProfile camcorderProfile = null;
        switch(videoQuality) {
            case 0:
                if (CamcorderProfile.hasProfile(this.mCameraId, 4)) {
                    camcorderProfile = CamcorderProfile.get(this.mCameraId, 4);
                } else {
                    camcorderProfile = this.getCamcorderProfile(6);
                }
                break;
            case 1:
                if (CamcorderProfile.hasProfile(this.mCameraId, 5)) {
                    camcorderProfile = CamcorderProfile.get(this.mCameraId, 5);
                } else {
                    camcorderProfile = this.getCamcorderProfile(0);
                }
                break;
            case 2:
                if (CamcorderProfile.hasProfile(this.mCameraId, 6)) {
                    camcorderProfile = CamcorderProfile.get(this.mCameraId, 6);
                } else {
                    camcorderProfile = this.getCamcorderProfile(1);
                }
                break;
            case 3:
                try {
                    camcorderProfile = CamcorderProfile.get(this.mCameraId, 8);
                } catch (Exception var4) {
                    camcorderProfile = this.getCamcorderProfile(4);
                }
                break;
            case 4:
                camcorderProfile = CamcorderProfile.get(this.mCameraId, 1);
                break;
            case 5:
                camcorderProfile = CamcorderProfile.get(this.mCameraId, 0);
                break;
            case 6:
                if (CamcorderProfile.hasProfile(this.mCameraId, 7)) {
                    camcorderProfile = CamcorderProfile.get(this.mCameraId, 7);
                } else {
                    camcorderProfile = this.getCamcorderProfile(5);
                }
        }

        if (camcorderProfile != null && this.mVideoBitRate != 0) {
            camcorderProfile.videoBitRate = this.mVideoBitRate;
        }

        return camcorderProfile;
    }

    void setTapToAutofocusListener(AutoFocusCallback callback) {
        if (this.mFocus != 2) {
            throw new IllegalArgumentException("Please set the camera to FOCUS_TAP.");
        } else {
            this.mAutofocusCallback = callback;
        }
    }

    private int getFocusAreaSize() {
        return 300;
    }

    private int getFocusMeteringAreaWeight() {
        return 1000;
    }

    private void resetFocus(final boolean success, Camera camera) {
        this.mHandler.removeCallbacksAndMessages((Object)null);
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                synchronized(Camera1.this.mCameraLock) {
                    if (Camera1.this.mCamera != null) {
                        Camera1.this.mCamera.cancelAutoFocus();
                        Parameters parameters = Camera1.this.getCameraParameters();
                        if (parameters == null) {
                            return;
                        }

                        if (parameters.getFocusMode() != "continuous-picture") {
                            parameters.setFocusMode("continuous-picture");
                            parameters.setFocusAreas((List)null);
                            parameters.setMeteringAreas((List)null);
                            Camera1.this.mCamera.setParameters(parameters);
                        }

                        if (Camera1.this.mAutofocusCallback != null) {
                            Camera1.this.mAutofocusCallback.onAutoFocus(success, Camera1.this.mCamera);
                        }
                    }

                }
            }
        }, 3000L);
    }

    private Rect calculateFocusArea(float x, float y) {
        int padding = this.getFocusAreaSize() / 2;
        int centerX = (int)(x * 2000.0F);
        int centerY = (int)(y * 2000.0F);
        int left = centerX - padding;
        int top = centerY - padding;
        int right = centerX + padding;
        int bottom = centerY + padding;
        if (left < 0) {
            left = 0;
        }

        if (right > 2000) {
            right = 2000;
        }

        if (top < 0) {
            top = 0;
        }

        if (bottom > 2000) {
            bottom = 2000;
        }

        return new Rect(left - 1000, top - 1000, right - 1000, bottom - 1000);
    }
}
