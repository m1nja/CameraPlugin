//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wonderkiln.camerakit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CameraKitImage extends CameraKitEvent {
    private byte[] jpeg;

    CameraKitImage(byte[] jpeg) {
        super("CKImageCapturedEvent");
        this.jpeg = jpeg;
    }

    public byte[] getJpeg() {
        return this.jpeg;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(this.jpeg, 0, this.jpeg.length);
    }
}
