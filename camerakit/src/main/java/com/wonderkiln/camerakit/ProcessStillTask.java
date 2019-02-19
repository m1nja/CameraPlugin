//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

class ProcessStillTask implements Runnable {
    private byte[] data;
    private Camera camera;
    private int rotation;
    private ProcessStillTask.OnStillProcessedListener onStillProcessedListener;

    public ProcessStillTask(byte[] data, Camera camera, int rotation, ProcessStillTask.OnStillProcessedListener onStillProcessedListener) {
        this.data = data;
        this.camera = camera;
        this.rotation = rotation;
        this.onStillProcessedListener = onStillProcessedListener;
    }

    public void run() {
        Parameters parameters = this.camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        byte[] rotatedData = (new Rotation(this.data, width, height, this.rotation)).getYuv();
        int postWidth;
        int postHeight;
        switch(this.rotation) {
            case 0:
            case 180:
            default:
                postWidth = width;
                postHeight = height;
                break;
            case 90:
            case 270:
                postWidth = height;
                postHeight = width;
        }

        YuvImage yuv = new YuvImage(rotatedData, parameters.getPreviewFormat(), postWidth, postHeight, (int[])null);
        this.onStillProcessedListener.onStillProcessed(yuv);
    }

    interface OnStillProcessedListener {
        void onStillProcessed(YuvImage var1);
    }
}
