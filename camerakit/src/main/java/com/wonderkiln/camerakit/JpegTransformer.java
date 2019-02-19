//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Rect;
import java.nio.ByteBuffer;

public class JpegTransformer {
    private ByteBuffer mHandler;

    public JpegTransformer(byte[] jpeg) {
        this.mHandler = this.jniStoreJpeg(jpeg, jpeg.length);
    }

    public byte[] getJpeg() {
        return this.jniCommit(this.mHandler);
    }

    public int getWidth() {
        return this.jniGetWidth(this.mHandler);
    }

    public int getHeight() {
        return this.jniGetHeight(this.mHandler);
    }

    public void rotate(int degrees) {
        this.jniRotate(this.mHandler, degrees);
    }

    public void flipHorizontal() {
        this.jniFlipHorizontal(this.mHandler);
    }

    public void flipVertical() {
        this.jniFlipVertical(this.mHandler);
    }

    public void crop(Rect crop) {
        this.jniCrop(this.mHandler, crop.left, crop.top, crop.width(), crop.height());
    }

    private native ByteBuffer jniStoreJpeg(byte[] var1, int var2);

    private native byte[] jniCommit(ByteBuffer var1);

    private native int jniGetWidth(ByteBuffer var1);

    private native int jniGetHeight(ByteBuffer var1);

    private native void jniRotate(ByteBuffer var1, int var2);

    private native void jniFlipHorizontal(ByteBuffer var1);

    private native void jniFlipVertical(ByteBuffer var1);

    private native void jniCrop(ByteBuffer var1, int var2, int var3, int var4, int var5);

    static {
        System.loadLibrary("jpegTransformer");
    }
}
