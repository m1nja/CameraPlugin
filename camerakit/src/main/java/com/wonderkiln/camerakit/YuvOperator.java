//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import java.nio.ByteBuffer;

public class YuvOperator {
    private ByteBuffer handler;
    private int width;
    private int height;

    private YuvOperator() {
    }

    public YuvOperator(byte[] yuv, int width, int height) {
        this.storeYuvData(yuv, width, height);
        this.width = width;
        this.height = height;
    }

    private void storeYuvData(byte[] yuv, int width, int height) {
        if (this.handler != null) {
            this.freeYuvData();
        }

        this.handler = this.jniStoreYuvData(yuv, width, height);
    }

    public void rotate(int rotation) {
        if (this.handler != null) {
            if (rotation == 90) {
                this.jniRotateYuvCw90(this.handler);
            } else if (rotation == 180) {
                this.jniRotateYuv180(this.handler);
            } else if (rotation == 270) {
                this.jniRotateYuvCcw90(this.handler);
            }

        }
    }

    public byte[] getYuvData() {
        byte[] yuv = this.jniGetYuvData(this.handler);
        this.freeYuvData();
        return yuv;
    }

    private void freeYuvData() {
        if (this.handler != null) {
            this.jniFreeYuvData(this.handler);
            this.handler = null;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.handler != null) {
            this.freeYuvData();
        }
    }

    private native ByteBuffer jniStoreYuvData(byte[] var1, int var2, int var3);

    private native void jniRotateYuvCcw90(ByteBuffer var1);

    private native void jniRotateYuvCw90(ByteBuffer var1);

    private native void jniRotateYuv180(ByteBuffer var1);

    private native byte[] jniGetYuvData(ByteBuffer var1);

    private native void jniFreeYuvData(ByteBuffer var1);

    static {
        System.loadLibrary("yuvOperator");
    }
}
