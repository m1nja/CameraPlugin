//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Frame.Builder;
import java.lang.Thread.State;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class FrameProcessingRunnable implements Runnable {
    private static final String TAG = FrameProcessingRunnable.class.getSimpleName();
    private Detector<?> mDetector;
    private long mStartTimeMillis = SystemClock.elapsedRealtime();
    private final Object mLock = new Object();
    private boolean mActive = true;
    private long mPendingTimeMillis;
    private int mPendingFrameId = 0;
    private ByteBuffer mPendingFrameData;
    private Thread mProcessingThread;
    private Map<byte[], ByteBuffer> mBytesToByteBuffer = new HashMap();
    private Size mPreviewSize;
    private Camera mCamera;

    public FrameProcessingRunnable(Detector<?> detector, Size mPreviewSize, Camera mCamera) {
        this.mDetector = detector;
        this.mPreviewSize = mPreviewSize;
        this.mCamera = mCamera;
        this.mProcessingThread = new Thread(this);
    }

    @SuppressLint({"Assert"})
    public void release() {
        assert this.mProcessingThread.getState() == State.TERMINATED;

        this.mDetector.release();
    }

    public void run() {
        while(true) {
            Object var3 = this.mLock;
            Frame outputFrame;
            ByteBuffer data;
            synchronized(this.mLock) {
                while(this.mActive && this.mPendingFrameData == null) {
                    try {
                        this.mLock.wait();
                    } catch (InterruptedException var13) {
                        return;
                    }
                }

                if (!this.mActive) {
                    return;
                }

                if (this.mPreviewSize == null) {
                    Log.d("WHAT", "waitin for preview size to not be null");
                    continue;
                }

                outputFrame = (new Builder()).setImageData(this.mPendingFrameData, this.mPreviewSize.getWidth(), this.mPreviewSize.getHeight(), 17).setId(this.mPendingFrameId).setTimestampMillis(this.mPendingTimeMillis).setRotation(0).build();
                data = this.mPendingFrameData;
                this.mPendingFrameData = null;
            }

            try {
                this.mDetector.receiveFrame(outputFrame);
            } catch (Throwable var11) {
                Log.e(TAG, "Exception thrown from receiver.", var11);
            } finally {
                this.mCamera.addCallbackBuffer(data.array());
            }
        }
    }

    public void cleanup() {
        if (this.mProcessingThread != null) {
            try {
                this.mProcessingThread.join();
            } catch (InterruptedException var2) {
                Log.d(TAG, "Frame processing thread interrupted on release.");
            }

            this.mProcessingThread = null;
        }

        this.setActive(false);
        this.mBytesToByteBuffer.clear();
    }

    public void start() {
        this.mProcessingThread = new Thread(this);
        this.setActive(true);
        this.mProcessingThread.start();
        this.mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                FrameProcessingRunnable.this.setNextFrame(bytes, camera);
            }
        });
        this.mCamera.addCallbackBuffer(this.createPreviewBuffer(this.mPreviewSize));
        this.mCamera.addCallbackBuffer(this.createPreviewBuffer(this.mPreviewSize));
        this.mCamera.addCallbackBuffer(this.createPreviewBuffer(this.mPreviewSize));
        this.mCamera.addCallbackBuffer(this.createPreviewBuffer(this.mPreviewSize));
    }

    private void setActive(boolean active) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mActive = active;
            this.mLock.notifyAll();
        }
    }

    private void setNextFrame(byte[] data, Camera camera) {
        Object var3 = this.mLock;
        synchronized(this.mLock) {
            if (this.mPendingFrameData != null) {
                camera.addCallbackBuffer(this.mPendingFrameData.array());
                this.mPendingFrameData = null;
            }

            if (!this.mBytesToByteBuffer.containsKey(data)) {
                Log.d(TAG, "Skipping frame.  Could not find ByteBuffer associated with the image data from the camera.");
            } else {
                this.mPendingTimeMillis = SystemClock.elapsedRealtime() - this.mStartTimeMillis;
                ++this.mPendingFrameId;
                this.mPendingFrameData = (ByteBuffer)this.mBytesToByteBuffer.get(data);
                this.mLock.notifyAll();
            }
        }
    }

    private void addBuffer(byte[] byteArray, ByteBuffer buffer) {
        this.mBytesToByteBuffer.put(byteArray, buffer);
    }

    private byte[] createPreviewBuffer(Size previewSize) {
        int bitsPerPixel = ImageFormat.getBitsPerPixel(17);
        long sizeInBits = (long)(previewSize.getHeight() * previewSize.getWidth() * bitsPerPixel);
        int bufferSize = (int)Math.ceil((double)sizeInBits / 8.0D) + 1;
        byte[] byteArray = new byte[bufferSize];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (buffer.hasArray() && buffer.array() == byteArray) {
            this.addBuffer(byteArray, buffer);
            return byteArray;
        } else {
            throw new IllegalStateException("Failed to create valid buffer for camera source.");
        }
    }
}
