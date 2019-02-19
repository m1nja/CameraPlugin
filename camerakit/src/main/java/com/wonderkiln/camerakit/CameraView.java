//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.text.TextRecognizer.Builder;
import com.wonderkiln.camerakit.CameraImpl.ImageCapturedCallback;
import com.wonderkiln.camerakit.CameraImpl.VideoCapturedCallback;
import com.wonderkiln.camerakit.core.R.styleable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraView extends CameraViewLayout {
    private static Handler sWorkerHandler;
    private int mFacing;
    private int mFlash;
    private int mFocus;
    private int mMethod;
    private boolean mPinchToZoom;
    private float mZoom;
    private int mPermissions;
    private int mVideoQuality;
    private int mJpegQuality;
    private int mVideoBitRate;
    private boolean mLockVideoAspectRatio;
    private boolean mCropOutput;
    private boolean mDoubleTapToToggleFacing;
    private boolean mAdjustViewBounds;
    private DisplayOrientationDetector mDisplayOrientationDetector;
    private CameraImpl mCameraImpl;
    private PreviewImpl mPreviewImpl;
    private boolean mIsStarted;
    private EventDispatcher mEventDispatcher;
    private FocusMarkerLayout focusMarkerLayout;

    public CameraView(@NonNull Context context) {
        this(context, (AttributeSet)null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, styleable.CameraView, 0, 0);

            try {
                this.mFacing = a.getInteger(styleable.CameraView_ckFacing, 0);
                this.mFlash = a.getInteger(styleable.CameraView_ckFlash, 0);
                this.mFocus = a.getInteger(styleable.CameraView_ckFocus, 1);
                this.mMethod = a.getInteger(styleable.CameraView_ckMethod, 0);
                this.mPinchToZoom = a.getBoolean(styleable.CameraView_ckPinchToZoom, true);
                this.mZoom = a.getFloat(styleable.CameraView_ckZoom, 1.0F);
                this.mPermissions = a.getInteger(styleable.CameraView_ckPermissions, 0);
                this.mVideoQuality = a.getInteger(styleable.CameraView_ckVideoQuality, 0);
                this.mJpegQuality = a.getInteger(styleable.CameraView_ckJpegQuality, 100);
                this.mCropOutput = a.getBoolean(styleable.CameraView_ckCropOutput, false);
                this.mVideoBitRate = a.getInteger(styleable.CameraView_ckVideoBitRate, 0);
                this.mDoubleTapToToggleFacing = a.getBoolean(styleable.CameraView_ckDoubleTapToToggleFacing, false);
                this.mLockVideoAspectRatio = a.getBoolean(styleable.CameraView_ckLockVideoAspectRatio, false);
                this.mAdjustViewBounds = a.getBoolean(styleable.CameraView_android_adjustViewBounds, false);
            } finally {
                a.recycle();
            }
        }

        this.mEventDispatcher = new EventDispatcher();
        this.mPreviewImpl = new SurfaceViewPreview(context, this);
        this.mCameraImpl = new Camera1(this.mEventDispatcher, this.mPreviewImpl);
        this.mIsStarted = false;
        WindowManager windowService = (WindowManager)context.getSystemService("window");
        boolean isChromebookInLaptopMode = context.getPackageManager().hasSystemFeature("org.chromium.arc.device_management") && windowService.getDefaultDisplay().getRotation() == 0;
        if (this.mCameraImpl.frontCameraOnly() || isChromebookInLaptopMode) {
            this.mFacing = 1;
        }

        this.setFacing(this.mFacing);
        this.setFlash(this.mFlash);
        this.setFocus(this.mFocus);
        this.setMethod(this.mMethod);
        this.setPinchToZoom(this.mPinchToZoom);
        this.setZoom(this.mZoom);
        this.setPermissions(this.mPermissions);
        this.setVideoQuality(this.mVideoQuality);
        this.setVideoBitRate(this.mVideoBitRate);
        this.setLockVideoAspectRatio(this.mLockVideoAspectRatio);
        if (!this.isInEditMode()) {
            this.mDisplayOrientationDetector = new DisplayOrientationDetector(context) {
                public void onDisplayOrDeviceOrientationChanged(int displayOrientation, int deviceOrientation) {
                    CameraView.this.mCameraImpl.setDisplayAndDeviceOrientation(displayOrientation, deviceOrientation);
                    CameraView.this.mPreviewImpl.setDisplayOrientation(displayOrientation);
                }
            };
            this.focusMarkerLayout = new FocusMarkerLayout(this.getContext());
            this.addView(this.focusMarkerLayout);
        }

    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isInEditMode()) {
            this.mDisplayOrientationDetector.enable(ViewCompat.isAttachedToWindow(this) ? DisplayManagerCompat.getInstance(this.getContext().getApplicationContext()).getDisplay(0) : null);
        }

    }

    protected void onDetachedFromWindow() {
        if (!this.isInEditMode()) {
            this.mDisplayOrientationDetector.disable();
        }

        super.onDetachedFromWindow();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mAdjustViewBounds) {
            Size previewSize = this.getPreviewSize();
            if (previewSize == null) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }

            int width;
            float ratio;
            int height;
            if (this.getLayoutParams().width == -2) {
                width = MeasureSpec.getSize(heightMeasureSpec);
                ratio = (float)width / (float)previewSize.getHeight();
                height = (int)((float)previewSize.getWidth() * ratio);
                super.onMeasure(MeasureSpec.makeMeasureSpec(height, 1073741824), heightMeasureSpec);
                return;
            }

            if (this.getLayoutParams().height == -2) {
                width = MeasureSpec.getSize(widthMeasureSpec);
                ratio = (float)width / (float)previewSize.getWidth();
                height = (int)((float)previewSize.getHeight() * ratio);
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, 1073741824));
                return;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isStarted() {
        return this.mIsStarted;
    }

    public void addController(CameraKitController controller) {
    }

    public void start() {
        if (!this.mIsStarted && this.isEnabled()) {
            this.mIsStarted = true;
            int cameraCheck = ContextCompat.checkSelfPermission(this.getContext(), "android.permission.CAMERA");
            int audioCheck = ContextCompat.checkSelfPermission(this.getContext(), "android.permission.RECORD_AUDIO");
            switch(this.mPermissions) {
                case 0:
                    if (cameraCheck != 0 || audioCheck != 0) {
                        this.requestPermissions(true, true);
                        return;
                    }
                    break;
                case 1:
                    if (cameraCheck != 0) {
                        this.requestPermissions(true, true);
                        return;
                    }
                    break;
                case 2:
                    if (cameraCheck != 0) {
                        this.requestPermissions(true, false);
                        return;
                    }
            }

            sWorkerHandler.postDelayed(new Runnable() {
                public void run() {
                    CameraView.this.mCameraImpl.start();
                }
            }, 100L);
        }
    }

    public void stop() {
        if (this.mIsStarted) {
            this.mIsStarted = false;
            this.mCameraImpl.stop();
        }
    }

    protected CameraImpl getCameraImpl() {
        return this.mCameraImpl;
    }

    protected PreviewImpl getPreviewImpl() {
        return this.mPreviewImpl;
    }

    protected void onZoom(float modifier, boolean start) {
        if (this.mPinchToZoom) {
            this.mCameraImpl.modifyZoom((modifier - 1.0F) * 0.8F + 1.0F);
        }

    }

    protected void onTapToFocus(float x, float y) {
        if (this.mFocus == 2 || this.mFocus == 3) {
            this.focusMarkerLayout.focus(x, y);
            float px = x - this.getPreviewImpl().getX();
            float py = y - this.getPreviewImpl().getY();
            this.mCameraImpl.setFocusArea(px / (float)this.getPreviewImpl().getWidth(), py / (float)this.getPreviewImpl().getHeight());
        }

    }

    protected void onToggleFacing() {
        if (this.mDoubleTapToToggleFacing) {
            this.toggleFacing();
        }

    }

    @Nullable
    public CameraProperties getCameraProperties() {
        return this.mCameraImpl.getCameraProperties();
    }

    public int getFacing() {
        return this.mFacing;
    }

    public boolean isFacingFront() {
        return this.mFacing == 1;
    }

    public boolean isFacingBack() {
        return this.mFacing == 0;
    }

    public void setFacing(final int facing) {
        this.mFacing = facing;
        sWorkerHandler.post(new Runnable() {
            public void run() {
                CameraView.this.mCameraImpl.setFacing(facing);
            }
        });
    }

    public void setFlash(int flash) {
        this.mFlash = flash;
        this.mCameraImpl.setFlash(flash);
    }

    public int getFlash() {
        return this.mFlash;
    }

    public void setFocus(int focus) {
        this.mFocus = focus;
        if (this.mFocus == 3) {
            this.mCameraImpl.setFocus(2);
        } else {
            this.mCameraImpl.setFocus(this.mFocus);
        }
    }

    public void setMethod(int method) {
        this.mMethod = method;
        this.mCameraImpl.setMethod(this.mMethod);
    }

    public void setPinchToZoom(boolean zoom) {
        this.mPinchToZoom = zoom;
    }

    public void setZoom(float zoom) {
        this.mZoom = zoom;
        this.mCameraImpl.setZoom(zoom);
    }

    public void setPermissions(int permissions) {
        this.mPermissions = permissions;
    }

    public void setVideoQuality(int videoQuality) {
        this.mVideoQuality = videoQuality;
        this.mCameraImpl.setVideoQuality(this.mVideoQuality);
    }

    public void setVideoBitRate(int videoBirRate) {
        this.mVideoBitRate = videoBirRate;
        this.mCameraImpl.setVideoBitRate(this.mVideoBitRate);
    }

    public void setLockVideoAspectRatio(boolean lockVideoAspectRatio) {
        this.mLockVideoAspectRatio = lockVideoAspectRatio;
        this.mCameraImpl.setLockVideoAspectRatio(lockVideoAspectRatio);
    }

    public void setJpegQuality(int jpegQuality) {
        this.mJpegQuality = jpegQuality;
    }

    public void setCropOutput(boolean cropOutput) {
        this.mCropOutput = cropOutput;
    }

    public int toggleFacing() {
        switch(this.mFacing) {
            case 0:
                this.setFacing(1);
                break;
            case 1:
                this.setFacing(0);
        }

        return this.mFacing;
    }

    public int toggleFlash() {
        switch(this.mFlash) {
            case 0:
                this.setFlash(1);
                break;
            case 1:
                this.setFlash(2);
                break;
            case 2:
            case 3:
                this.setFlash(0);
        }

        return this.mFlash;
    }

    public void captureImage() {
        this.captureImage((CameraKitEventCallback)null);
    }

    public boolean setTextDetectionListener(CameraKitEventCallback<CameraKitTextDetect> callback) throws GooglePlayServicesUnavailableException {
        TextRecognizer textRecognizer = (new Builder(this.getContext())).build();
        textRecognizer.setProcessor(new TextProcessor(this.mEventDispatcher, callback));
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getContext().getApplicationContext());
        if (code != 0) {
            throw new GooglePlayServicesUnavailableException();
        } else if (textRecognizer.isOperational()) {
            this.mCameraImpl.setTextDetector(textRecognizer);
            return true;
        } else {
            return false;
        }
    }

    public void captureImage(final CameraKitEventCallback<CameraKitImage> callback) {
        this.mCameraImpl.captureImage(new ImageCapturedCallback() {
            public void imageCaptured(byte[] jpeg) {
                PostProcessor postProcessor = new PostProcessor(jpeg);
                postProcessor.setJpegQuality(CameraView.this.mJpegQuality);
                postProcessor.setFacing(CameraView.this.mFacing);
                if (CameraView.this.mCropOutput) {
                    postProcessor.setCropOutput(AspectRatio.of(CameraView.this.getWidth(), CameraView.this.getHeight()));
                }

                CameraKitImage image = new CameraKitImage(postProcessor.getJpeg());
                if (callback != null) {
                    callback.callback(image);
                }

                CameraView.this.mEventDispatcher.dispatch(image);
            }
        });
    }

    public void captureVideo() {
        this.captureVideo((File)null, (CameraKitEventCallback)null);
    }

    public void captureVideo(File videoFile) {
        this.captureVideo(videoFile, (CameraKitEventCallback)null);
    }

    public void captureVideo(CameraKitEventCallback<CameraKitVideo> callback) {
        this.captureVideo((File)null, callback);
    }

    public void captureVideo(File videoFile, CameraKitEventCallback<CameraKitVideo> callback) {
        this.captureVideo(videoFile, 0, callback);
    }

    public void captureVideo(File videoFile, int maxDuration, final CameraKitEventCallback<CameraKitVideo> callback) {
        this.mCameraImpl.captureVideo(videoFile, maxDuration, new VideoCapturedCallback() {
            public void videoCaptured(File file) {
                CameraKitVideo video = new CameraKitVideo(file);
                if (callback != null) {
                    callback.callback(video);
                }

                CameraView.this.mEventDispatcher.dispatch(video);
            }
        });
    }

    public void stopVideo() {
        this.mCameraImpl.stopVideo();
    }

    public Size getPreviewSize() {
        return this.mCameraImpl != null ? this.mCameraImpl.getPreviewResolution() : null;
    }

    public Size getCaptureSize() {
        return this.mCameraImpl != null ? this.mCameraImpl.getCaptureResolution() : null;
    }

    private void requestPermissions(boolean requestCamera, boolean requestAudio) {
        Activity activity = null;

        for(Context context = this.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper)context).getBaseContext()) {
            if (context instanceof Activity) {
                activity = (Activity)context;
            }
        }

        List<String> permissions = new ArrayList();
        if (requestCamera) {
            permissions.add("android.permission.CAMERA");
        }

        if (requestAudio) {
            permissions.add("android.permission.RECORD_AUDIO");
        }

        if (activity != null) {
            ActivityCompat.requestPermissions(activity, (String[])permissions.toArray(new String[permissions.size()]), 16);
        }

    }

    public void addCameraKitListener(CameraKitEventListener CameraKitEventListener) {
        this.mEventDispatcher.addListener(CameraKitEventListener);
    }

    public void bindCameraKitListener(Object object) {
        this.mEventDispatcher.addBinding(object);
    }

    static {
        HandlerThread workerThread = new HandlerThread("CameraViewWorker");
        workerThread.setDaemon(true);
        workerThread.start();
        sWorkerHandler = new Handler(workerThread.getLooper());
    }
}
