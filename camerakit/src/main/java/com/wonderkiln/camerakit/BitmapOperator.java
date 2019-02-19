//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

public class BitmapOperator {
    private ByteBuffer handler;

    private BitmapOperator() {
    }

    public BitmapOperator(byte[] bitmap) {
        this.storeBitmap(bitmap);
    }

    private void storeBitmap(byte[] bitmap) {
        if (this.handler != null) {
            this.freeBitmap();
        }

        this.handler = this.jniStoreBitmapData(bitmap);
    }

    public void rotateBitmap(int degrees) {
        if (this.handler != null) {
            if (degrees == 90) {
                this.jniRotateBitmapCw90(this.handler);
            } else if (degrees == 180) {
                this.jniRotateBitmap180(this.handler);
            } else if (degrees == 270) {
                this.jniRotateBitmapCcw90(this.handler);
            }

        }
    }

    public void cropBitmap(int left, int top, int right, int bottom) {
        if (this.handler != null) {
            this.jniCropBitmap(this.handler, left, top, right, bottom);
        }
    }

    public void flipBitmapHorizontal() {
        if (this.handler != null) {
            this.jniFlipBitmapHorizontal(this.handler);
        }
    }

    public void flipBitmapVertical() {
        if (this.handler != null) {
            this.jniFlipBitmapVertical(this.handler);
        }
    }

    public byte[] getJpeg(int quality) {
        return this.handler == null ? null : this.jniGetJpegData(this.handler, quality);
    }

    public byte[] getJpegAndFree(int quality) {
        byte[] jpeg = this.getJpeg(quality);
        this.freeBitmap();
        return jpeg;
    }

    public Bitmap getBitmap() {
        return this.handler == null ? null : this.jniGetBitmapFromStoredBitmapData(this.handler);
    }

    public Bitmap getBitmapAndFree() {
        Bitmap bitmap = this.getBitmap();
        this.freeBitmap();
        return bitmap;
    }

    public int getWidth() {
        return this.handler == null ? -1 : this.jniGetWidth(this.handler);
    }

    public int getHeight() {
        return this.handler == null ? -1 : this.jniGetHeight(this.handler);
    }

    private void freeBitmap() {
        if (this.handler != null) {
            this.jniFreeBitmapData(this.handler);
            this.handler = null;
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.handler != null) {
            this.freeBitmap();
        }
    }

    private native ByteBuffer jniStoreBitmapData(byte[] var1);

    private native Bitmap jniGetBitmapFromStoredBitmapData(ByteBuffer var1);

    private native byte[] jniGetJpegData(ByteBuffer var1, int var2);

    private native int jniGetWidth(ByteBuffer var1);

    private native int jniGetHeight(ByteBuffer var1);

    private native void jniFreeBitmapData(ByteBuffer var1);

    private native void jniRotateBitmapCcw90(ByteBuffer var1);

    private native void jniRotateBitmapCw90(ByteBuffer var1);

    private native void jniRotateBitmap180(ByteBuffer var1);

    private native void jniCropBitmap(ByteBuffer var1, int var2, int var3, int var4, int var5);

    private native void jniFlipBitmapHorizontal(ByteBuffer var1);

    private native void jniFlipBitmapVertical(ByteBuffer var1);

    static {
        System.loadLibrary("jpge");
        System.loadLibrary("jpgd");
        System.loadLibrary("JniYuvOperator");
        System.loadLibrary("JniBitmapOperator");
    }
}
